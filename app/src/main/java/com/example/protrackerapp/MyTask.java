package com.example.protrackerapp;
import com.google.firebase.database.IgnoreExtraProperties;

public class MyTask {
    private String type;
    private String title;
    private String status;

    public MyTask() {
    }

    public MyTask(String type, String title, String status) {
        this.type = type;
        this.title = title;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
