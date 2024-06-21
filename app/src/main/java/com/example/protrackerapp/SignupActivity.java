package com.example.protrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    EditText signupUsername, signupEmail, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);        signupUsername = findViewById(R.id.username);


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    database = FirebaseDatabase.getInstance();
                    reference = database.getReference("users");

                    String email = signupEmail.getText().toString();
                    String username = signupUsername.getText().toString();
                    String password = signupPassword.getText().toString();

                    HelperClass userData = new HelperClass(username, password, email);
                    reference.child("users").child(username).setValue(userData);

                    Toast.makeText(SignupActivity.this, "You have signed up successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(SignupActivity.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}

//
//
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.HashMap;
//public class SignupActivity extends AppCompatActivity {
//
//    private EditText usernameEditText;
//    private EditText emailEditText;
//    private EditText passwordEditText;
//    private Button signupButton;
//
//    private FirebaseAuth mAuth;
//    private DatabaseReference mDatabaseReference;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_signup);
//
//        // Initialize FirebaseAuth and Database instances
//        mAuth = FirebaseAuth.getInstance();
//        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users"); // Reference to "users" node
//
//        // Find view elements by ID
//        usernameEditText = findViewById(R.id.username);
//        emailEditText = findViewById(R.id.signup_email);
//        passwordEditText = findViewById(R.id.password);
//        signupButton = findViewById(R.id.signup_button);
//
//        // Set onClickListener for signup button
//        signupButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String username = usernameEditText.getText().toString().trim();
//                String email = emailEditText.getText().toString().trim();
//                String password = passwordEditText.getText().toString().trim();
//
//                // Optional: Input validation for empty fields or email format
//                // Consider using regular expressions for email validation
//
//                createUserWithEmailAndPassword(username, email, password);
//            }
//        });
//    }
//
//    private void createUserWithEmailAndPassword(String username, String email, String password) {
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        // User creation successful
//                        FirebaseUser user = mAuth.getCurrentUser(); // Get current user
//                        saveUserData(user.getUid(), username, email); // Call saveUserData function
//
//                        // Optional: Navigate to LoginActivity or other screen
//                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish(); // Prevent back button from returning to SignupActivity
//                    } else {
//                        // Signup failed, handle error (consider more specific error handling)
//                        Exception exception = task.getException();
//                        if (exception != null) {
//                            String errorMessage = exception.getMessage();
//                            Toast.makeText(SignupActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(SignupActivity.this, "Signup failed!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//
//    private void saveUserData(String userId, String username, String email) {
//        // Create a HashMap for user data (excluding password)
//        HashMap<String, Object> userData = new HashMap<>();
//        userData.put("username", username);
//        userData.put("email", email);
//
//        // Save user data to the database (users/{userId} node)
//        mDatabaseReference.child(userId).setValue(userData)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d("SignupActivity", "User data saved successfully!");
//                        } else {
//                            Log.w("SignupActivity", "Failed to save user data!", task.getException());
//                            Toast.makeText(SignupActivity.this, "Error saving data!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//}
