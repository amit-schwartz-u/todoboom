package com.example.todoboom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class EditCompletedTodoItemActivity extends AppCompatActivity {

    TodoItem todoItem;
    ArrayList<TodoItem> todoItems;
    int todoItemPosition;
    TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_completed_todo_item);
        todoItems = MyPreferences.loadData(getApplicationContext());
        todoItemPosition = getIntent().getIntExtra("position", 0);
        todoItem = todoItems.get(todoItemPosition);
        setMyContentView();
    }

    private void setMyContentView() {
        tvDescription = findViewById(R.id.tv_completed_todo_description);
        tvDescription.setText(todoItem.getDescription());
        TextView tvCreateTimestamp = findViewById(R.id.tv_completed_create_timestamp);
        tvCreateTimestamp.setText("created: " + todoItem.getCreationTimestamp());
        TextView tvEditTimestamp = findViewById(R.id.tv_completed_edit_timestamp);
        tvEditTimestamp.setText("last modified: " + todoItem.getEditTimestamp());
    }

    public void unmarkDoneButtonOnClick(View view) {
        Intent sendBackIntent = new Intent(getApplicationContext(), MainActivity.class);
        sendBackIntent.putExtra("position", todoItemPosition);
        sendBackIntent.putExtra("shouldUnmarkDone", true);
        startActivity(sendBackIntent);
        finish();
    }

    public void deleteButtonOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), DeleteTodoDialog.class);
        intent.putExtra("position", todoItemPosition);
        startActivity(intent);
    }
}