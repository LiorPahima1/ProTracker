package com.example.protrackerapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<MyTask> taskList;

    public TaskAdapter(List<MyTask> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        MyTask task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        private TextView taskTypeTextView;
        private TextView taskTitleTextView;
        private TextView taskStatusTextView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTypeTextView = itemView.findViewById(R.id.taskTypeTextView);
            taskTitleTextView = itemView.findViewById(R.id.task_name_text_view);
            taskStatusTextView = itemView.findViewById(R.id.task_status_text_view);
        }

        public void bind(MyTask task) {
            taskTypeTextView.setText(task.getType());
            taskTitleTextView.setText(task.getTitle());
            taskStatusTextView.setText(task.getStatus());
        }
    }
}
