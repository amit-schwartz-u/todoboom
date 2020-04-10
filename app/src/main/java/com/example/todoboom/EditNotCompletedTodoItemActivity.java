package com.example.todoboom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class EditNotCompletedTodoItemActivity extends AppCompatActivity {
    TodoItem todoItem;
    ArrayList<TodoItem> todoItems;
    int todoItemPosition;
    EditText editText;
    Intent sendBackIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo_item);
        todoItems = MyPreferences.loadData(getApplicationContext());
        todoItemPosition = getIntent().getIntExtra("position", 0);
        todoItem = todoItems.get(todoItemPosition);
        setMyContentView();

        sendBackIntent = new Intent(getApplicationContext(), MainActivity.class);
        sendBackIntent.putExtra("position", todoItemPosition);
    }

    private void setMyContentView() {
        editText = findViewById(R.id.et_todo_description);
        editText.setText(todoItem.getDescription());
        TextView tvCreateTimestamp = findViewById(R.id.tv_create_timestamp);
        tvCreateTimestamp.setText("created: " + todoItem.getCreationTimestamp());
        TextView tvEditTimestamp = findViewById(R.id.tv_edit_timestamp);
        tvEditTimestamp.setText("last modified: " + todoItem.getEditTimestamp());

    }

    public void markDoneButtonOnClick(View view) {
        sendBackIntent.putExtra("shouldMarkAsDone", true);
        startActivity(sendBackIntent);
        finish();
    }

    public void applyButtonOnClick(View view) {
        editText.setText(editText.getText());
        Timestamp ts=new Timestamp(System.currentTimeMillis());
        Date date=ts;
        sendBackIntent.putExtra("newEditTimestamp", date.toString());
        sendBackIntent.putExtra("shouldUpdateDescription", true);
        //todo need to check empty string?
        sendBackIntent.putExtra("updateDescription", editText.getText().toString());
        startActivity(sendBackIntent);
        finish();
    }
}
