package com.example.todoboom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements
        MyAdapter.OnTodoListener, MyAdapter.OnTodoLongListener {

    private static final String EMPTY_MSG = "you can't create an empty TODO item, oh silly!";
    private ArrayList<TodoItem> todoItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static boolean shouldHideKeyBoard = false;
    private static int idCounter = 0;

    /* Firebase */
    private FirebaseFirestore db;
    private CollectionReference todoItemsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        todoItemsRef = db.collection("todoList");
        initializeRecyclerView();
        setFireStore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent callingIntent = getIntent();
        int todoItemPosition = callingIntent.getIntExtra("position", 0);
        if (callingIntent.getBooleanExtra("shouldDelete", false)) {
            toastMessage("your todo was deleted");
            deleteTodoItemFromDbRef(todoItems.get(todoItemPosition));
            saveTodoItemsListInMyPref();
        } else if (shouldMarkTodoItemAsDone(callingIntent)) {
            markTodoAsDone(todoItemPosition);
        }
        else if(callingIntent.getBooleanExtra("shouldUnmarkDone", false)){
            unmarkTodoFromDone(todoItemPosition);
        }
        if (shouldUpdateDescription(callingIntent)) {
            updateTodoItem(callingIntent, todoItemPosition);
        }
        MyPreferences.saveStateToMyPref(getApplicationContext(), todoItems);
        notifyAdapterOnChanges();
    }

    private void deleteTodoItemFromDbRef(TodoItem todoItem) {
        DocumentReference todoItemRef = getTodoItemDocumentReference(todoItem);
        todoItemRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("Debug", "deleted todoItem");
            }
        });
    }

    private DocumentReference getTodoItemDocumentReference(TodoItem todoItem) {
        return todoItemsRef.document(todoItem.getDbId());
    }

    private void setFireStore() {
        todoItemsRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<TodoItem> todoItemsFromDb = new ArrayList<TodoItem>();
                            Log.e("successful", "successful get todoItems");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                todoItemsFromDb.add(document.toObject(TodoItem.class));
                            }
                            todoItems.clear();
                            for(TodoItem todoItem: todoItemsFromDb) {
                                todoItems.add(todoItem);
                            }
                            logTodoListSize();
                            saveTodoItemsListInMyPref();
                            notifyAdapterOnChanges();
                        } else {
                            Log.e("error", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void unmarkTodoFromDone(int position) {
        TodoItem todoItem = todoItems.get(position);
        String todoText = todoItem.getDescription();
        todoItem.setDescription(todoText.substring(6));
        toastMessage("TODO " + todoText + " is now not DONE!");
        todoItem.setDone(false);
        todoItems.set(position, todoItem);
        saveTodoItemChangeInFireStore(todoItem);
    }

    private void updateTodoItem(Intent callingIntent, int todoItemPosition) {
        TodoItem todoItem = todoItems.get(todoItemPosition);
        todoItem.setDescription(callingIntent.getStringExtra("updateDescription"));
        todoItem.setEditTimestamp(callingIntent.getStringExtra("newEditTimestamp"));
        saveTodoItemChangeInFireStore(todoItem);
    }

    private boolean shouldUpdateDescription(Intent callingIntent) {
        return callingIntent.getBooleanExtra("shouldUpdateDescription", false);
    }

    private boolean shouldMarkTodoItemAsDone(Intent callingIntent) {
        return callingIntent.getBooleanExtra("shouldMarkAsDone", false);
    }

    private void markTodoAsDone(int position) {
        TodoItem todoItem = todoItems.get(position);
        String todoText = todoItem.getDescription();
        toastMessage("TODO " + todoText + " is now DONE. BOOM!");
        todoItem.setDescription("done: " + todoText);
        todoItem.setDone(true);
        todoItems.set(position, todoItem);
        saveTodoItemChangeInFireStore(todoItem);
    }

    private void saveTodoItemChangeInFireStore(TodoItem todoItem) {
        DocumentReference todoItemRef = getTodoItemDocumentReference(todoItem);
        todoItemRef.set(todoItem);
    }

    /**
     * This method initializes the RecyclerView.
     */
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

        mAdapter = new MyAdapter(todoItems, this, getApplicationContext(), this);
        recyclerView.setAdapter(mAdapter);
    }

    public void createButtonOnClick(View view) {
        shouldHideKeyBoard = true;
        EditText et = findViewById(R.id.etInputText);
        String inputText = et.getText().toString();
        if (inputText.length() == 0) {
            toastMessage(EMPTY_MSG);
            shouldHideKeyBoard = false;
        } else {
            addNewTodoItemToList(inputText);
            et.setText("");
            mAdapter.notifyItemChanged(todoItems.size() - 1);
            saveTodoItemsListInMyPref();
        }
        final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(getApplicationContext(), 200) & shouldHideKeyBoard) {
                    // if more than 200 dp, it's probably a keyboard...
                    hideSoftKeyboard(MainActivity.this);
                }
            }
        });
    }

    private void addNewTodoItemToList(String inputText) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Date date = ts;
        String timestampStr = date.toString();
        final TodoItem todoItem = new TodoItem(inputText, false, timestampStr, timestampStr, idCounter, null);
        idCounter += 1;
        todoItems.add(todoItem);
        todoItemsRef.add(todoItem).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                todoItem.setDbId(documentReference.getId());
                todoItems.set(todoItems.size()-1, todoItem);
                saveTodoItemChangeInFireStore(todoItem);
                saveTodoItemsListInMyPref();
                notifyAdapterOnChanges();
                Log.e("Success", "Success adding todo item to todoitems list");
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
        shouldHideKeyBoard = false;
    }

    public void toastMessage(String message) {
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
        if (!(todoItem.isDone())){
            callNotCompletedActivity(position);
        }
        else {
            callEditCompletedTodoItemActivity(position);
        }

    }

    private void callEditCompletedTodoItemActivity(int position) {
        Intent intent = new Intent(getApplicationContext(), EditCompletedTodoItemActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private void callNotCompletedActivity(int position) {
        Intent intent = new Intent(getApplicationContext(), EditNotCompletedTodoItemActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public void onTodoLongClick(int position) {
        //todo - can be deleted from ex4
//        Intent intent = new Intent(getApplicationContext(), DeleteTodoDialog.class);
//        intent.putExtra("position", position);
//        startActivity(intent);
    }

    private void notifyAdapterOnChanges() {
        mAdapter.notifyDataSetChanged();
    }

    private void saveTodoItemsListInMyPref() {
        MyPreferences.saveStateToMyPref(getApplicationContext(), todoItems);
    }

    private void logTodoListSize() {
        Log.e("Todo list Size is: ", Integer.toString(todoItems.size())); //todo change to debug?
    }
}
