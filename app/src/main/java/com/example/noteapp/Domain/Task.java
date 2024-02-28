package com.example.noteapp.Domain;

public class Task {
    String taskId;
    String title;
    String description;
    String date;
    String Completed;
    String firstTime;
    String secondTime;
    String time;
    String event;

    public Task() {
    }

    public Task(String taskId ,String title, String Completed, String description, String date, String time, String event) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.Completed = Completed;
        this.firstTime = firstTime;
        this.secondTime = secondTime;
        this.time = time;
        this.event = event;
    }

    public String getIdTask() {
        return taskId;
    }

    public void setIdTask(String idTask) {
        this.taskId = idTask;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCompleted() {
        return Completed;
    }

    public void setCompleted(String completed) {
        Completed = completed;
    }

    public String getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }

    public String getSecondTime() {
        return secondTime;
    }

    public void setSecondTime(String secondTime) {
        this.secondTime = secondTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
