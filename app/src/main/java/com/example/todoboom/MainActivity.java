package com.example.todoboom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        MyAdapter.OnTodoListener {

    private static final String EMPTY_MSG = "you can't create an empty TODO item, oh silly!";
    private ArrayList<TodoItem> todoItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeRecyclerView();
        logTodoListSize();
    }

    private void logTodoListSize() {
        Log.e("Todo list Size is: ", Integer.toString(todoItems.size())); //todo change to debug?
    }

    private void initializeRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.my_text_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        todoItems = MyPreferences.loadData(getApplicationContext());
        mAdapter = new MyAdapter(todoItems, this);
        recyclerView.setAdapter(mAdapter);
    }

    public void btnCreateOnClick(View view) {
        flag = true;
        EditText et = findViewById(R.id.etInputText);
        String inputText = et.getText().toString();
        if (inputText.length() == 0) {
            toastMessage(EMPTY_MSG);
            flag = false;
        } else {
            TodoItem todoItem = new TodoItem(inputText, false);
            todoItems.add(todoItem);
            mAdapter.notifyItemChanged(todoItems.size() - 1);
            et.setText("");
        }
        MyPreferences.saveStateToMyPref(getApplicationContext(), todoItems);
        final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(getApplicationContext(), 200) & flag) {
                    // if more than 200 dp, it's probably a keyboard...
                    hideSoftKeyboard(MainActivity.this);
                }
            }
        });
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
        flag = false;
    }

    private void toastMessage(String message) {
        Toast newToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        newToast.setGravity(Gravity.TOP | Gravity.LEFT, 150, 50);
        ViewGroup group = (ViewGroup) newToast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(20);
        newToast.show();
    }

    @Override
    public void onTodoClick(int position) {
        TodoItem todoItem = todoItems.get(position);
        if (!todoItem.isDone()) {
            String todoText = todoItem.getDescription();
            toastMessage("TODO " + todoText + "is now DONE. BOOM!");
            todoItem.setDescription("done: " + todoText);
            mAdapter.notifyDataSetChanged();
            todoItem.setIsDone(true);
            MyPreferences.saveStateToMyPref(getApplicationContext(), todoItems);
        }

    }

}
