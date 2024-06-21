package com.example.protrackerapp;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class TaskViewHolder extends RecyclerView.ViewHolder {

    public TextView taskNameTextView;
    public TextView taskStatusTextView;

    public TaskViewHolder(View itemView) {
        super(itemView);

        taskNameTextView = itemView.findViewById(R.id.task_name_text_view);
        taskStatusTextView = itemView.findViewById(R.id.task_status_text_view);
    }
}
