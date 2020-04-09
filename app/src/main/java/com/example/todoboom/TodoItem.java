package com.example.todoboom;

import java.util.Date;

public class TodoItem {
    String description;
    Boolean isDone;
    Date creationTimestamp;
    Date editTimestamp;
    int id;

    public TodoItem(String description, Boolean isDone, Date creationTimestamp, Date editTimestamp, int id) {
        this.description = description;
        this.isDone = isDone;
        this.creationTimestamp = creationTimestamp;
        this.editTimestamp = editTimestamp;
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isDone() {
        return isDone;
    }

    public void setIsDone(Boolean isDone) {
        this.isDone = isDone;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Date getEditTimestamp() {
        return editTimestamp;
    }

    public void setEditTimestamp(Date editTimestamp) {
        this.editTimestamp = editTimestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
