package com.example.noteapp.Domain;

public class Note {
    String phone;
    String noteId;
    String title;
    String content;
    String date;
    String time;
    int color;
    boolean isBlock;

    public Note() {
    }

    public Note(String noteId,String title, String content, String date, String time, int color) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
        this.color = color;
        this.isBlock = false;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }
}
