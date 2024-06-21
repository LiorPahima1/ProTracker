package com.example.protrackerapp;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.protrackerapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            retrieveTasks(currentUser.getEmail());
        } else {
            Log.e(TAG, "User is not logged in.");
        }

        findViewById(R.id.addTaskButton).setOnClickListener(view -> openAddTaskDialog());
    }

    private void retrieveTasks(String userEmail) {
        String tasksPath = "users/users/tasks/" + sanitizeEmail(userEmail);

        DatabaseReference userTasksRef = mDatabase.child(tasksPath);

        LinearLayout linearLayout = findViewById(R.id.taskContainer);

        userTasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    String title = taskSnapshot.child("title").getValue(String.class);
                    String status = taskSnapshot.child("status").getValue(String.class);
                    String type = taskSnapshot.child("type").getValue(String.class);

                    addTaskToUI(linearLayout, title, status, type, taskSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to retrieve tasks", databaseError.toException());
            }
        });
    }

    private String sanitizeEmail(String email) {
        return email.replace(".", "_");
    }

    private void addTaskToUI(LinearLayout linearLayout, String title, String status, String type, String taskId) {
        LayoutInflater inflater = getLayoutInflater();
        View taskView = inflater.inflate(R.layout.task_item_layout, null);

        TextView taskTypeTextView = taskView.findViewById(R.id.taskTypeTextView);
        taskTypeTextView.setText("Task Type: " + type);

        TextView taskNameTextView = taskView.findViewById(R.id.task_name_text_view);
        taskNameTextView.setText("Task Name: " + title);

        TextView taskStatusTextView = taskView.findViewById(R.id.task_status_text_view);
        taskStatusTextView.setText("Task Status: " + status);

        ImageView removeIcon = taskView.findViewById(R.id.removeIcon);
        removeIcon.setOnClickListener(v -> removeTask(taskId, taskView));

        // Add click listener to edit the task
        taskView.setOnClickListener(v -> openEditTaskDialog(taskId, title, status, type));

        // Set tag to identify the task view
        taskView.setTag(taskId);

        linearLayout.addView(taskView);
    }

    private void openAddTaskDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);

        EditText taskTitleEditText = dialogView.findViewById(R.id.taskTitleEditText);
        Spinner taskTypeSpinner = dialogView.findViewById(R.id.taskTypeSpinner);
        Spinner taskStatusSpinner = dialogView.findViewById(R.id.taskStatusSpinner);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.task_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskTypeSpinner.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this, R.array.task_statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskStatusSpinner.setAdapter(statusAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Add Task")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String taskType = taskTypeSpinner.getSelectedItem().toString();
                        String taskTitle = taskTitleEditText.getText().toString();
                        String taskStatus = taskStatusSpinner.getSelectedItem().toString();

                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            DatabaseReference currentUserTasksRef = FirebaseDatabase.getInstance()
                                    .getReference("users")
                                    .child("users")
                                    .child("tasks")
                                    .child(currentUser.getEmail().replace(".", "_"));

                            currentUserTasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int taskCount = (int) snapshot.getChildrenCount() + 1; // Increment count for new task
                                    String taskId = "task" + taskCount; // Generate task ID like "task1", "task2", etc.

                                    // Save the task to Firebase under the current user's email with the generated task ID
                                    DatabaseReference newTaskRef = currentUserTasksRef.child(taskId);
                                    newTaskRef.child("title").setValue(taskTitle);
                                    newTaskRef.child("type").setValue(taskType);
                                    newTaskRef.child("status").setValue(taskStatus);

                                    // Add the new task to the UI
                                    LinearLayout linearLayout = findViewById(R.id.taskContainer);
                                    addTaskToUI(linearLayout, taskTitle, taskStatus, taskType, taskId);

                                    Toast.makeText(DashboardActivity.this,
                                            "Task saved:\nType: " + taskType + "\nTitle: " + taskTitle + "\nStatus: " + taskStatus,
                                            Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeTask(String taskId, View taskView) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference currentUserTasksRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child("users")
                    .child("tasks")
                    .child(currentUser.getEmail().replace(".", "_"))
                    .child(taskId);
            currentUserTasksRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Remove the task view from the UI
                    ((LinearLayout) taskView.getParent()).removeView(taskView);
                    Toast.makeText(this, "Task removed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to remove task", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Method to open edit task dialog
    private void openEditTaskDialog(String taskId, String currentTitle, String currentStatus, String currentType) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_task, null);

        EditText taskTitleEditText = dialogView.findViewById(R.id.taskTitleEditText);
        Spinner taskTypeSpinner = dialogView.findViewById(R.id.taskTypeSpinner);
        Spinner taskStatusSpinner = dialogView.findViewById(R.id.taskStatusSpinner);

        taskTitleEditText.setText(currentTitle);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.task_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskTypeSpinner.setAdapter(typeAdapter);
        int typePosition = typeAdapter.getPosition(currentType);
        taskTypeSpinner.setSelection(typePosition);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this, R.array.task_statuses, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskStatusSpinner.setAdapter(statusAdapter);
        int statusPosition = statusAdapter.getPosition(currentStatus);
        taskStatusSpinner.setSelection(statusPosition);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Edit Task")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newTitle = taskTitleEditText.getText().toString();
                        String newType = taskTypeSpinner.getSelectedItem().toString();
                        String newStatus = taskStatusSpinner.getSelectedItem().toString();

                        updateTask(taskId, newTitle, newType, newStatus);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to update task details in Firebase and UI
    private void updateTask(String taskId, String newTitle, String newType, String newStatus) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference taskRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child("users")
                    .child("tasks")
                    .child(currentUser.getEmail().replace(".", "_"))
                    .child(taskId);

            Map<String, Object> taskUpdates = new HashMap<>();
            taskUpdates.put("title", newTitle);
            taskUpdates.put("type", newType);
            taskUpdates.put("status", newStatus);

            taskRef.updateChildren(taskUpdates)
                    .addOnSuccessListener(aVoid -> {
                        // Update UI
                        LinearLayout linearLayout = findViewById(R.id.taskContainer);
                        View taskView = linearLayout.findViewWithTag(taskId);
                        if (taskView != null) {
                            TextView taskNameTextView = taskView.findViewById(R.id.task_name_text_view);
                            taskNameTextView.setText("Task Name: " + newTitle);

                            TextView taskTypeTextView = taskView.findViewById(R.id.taskTypeTextView);
                            taskTypeTextView.setText("Task Type: " + newType);

                            TextView taskStatusTextView = taskView.findViewById(R.id.task_status_text_view);
                            taskStatusTextView.setText("Task Status: " + newStatus);
                        }

                        Toast.makeText(DashboardActivity.this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(DashboardActivity.this, "Failed to update task", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}

