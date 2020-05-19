package com.example.todoboom;

public class TodoItem {
    String description;
    int isDone;
    String creationTimestamp;
    String editTimestamp;
    int id;
    String dbId;

    public TodoItem(){
    };

    public TodoItem(String description, int isDone, String creationTimestamp, String editTimestamp, int id, String dbId) {
        this.description = description;
        this.isDone = isDone;
        this.creationTimestamp = creationTimestamp;
        this.editTimestamp = editTimestamp;
        this.id = id;
        this.dbId = dbId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int isDone() {
        return isDone;
    }

    public void setIsDone(int isDone) {
        this.isDone = isDone;
    }

    public String getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public String getEditTimestamp() {
        return editTimestamp;
    }

    public void setEditTimestamp(String editTimestamp) {
        this.editTimestamp = editTimestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

}
