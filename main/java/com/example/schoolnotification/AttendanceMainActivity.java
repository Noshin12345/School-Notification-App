package com.example.schoolnotification;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class AttendanceMainActivity extends AppCompatActivity {
    FloatingActionButton fu;
    RecyclerView recyclerView;
    ClassAdapter classAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ClassItem> classItems = new ArrayList<>();
    DatabaseHelper dbHelper;



    // ArrayList<ClassItem> classItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_main);
        dbHelper = new DatabaseHelper(this);
        fu = findViewById(R.id.fu);
        fu.setOnClickListener(v -> showDialog());
        loadData();
        recyclerView = findViewById(R.id.recy);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter(this, classItems);
        recyclerView.setAdapter(classAdapter);
        classAdapter.setOnItemClickListener(position -> gotoItemActivity(position));

    }


    /*private void loadData(){
        Cursor cursor =  dbHelper.getClassTable();
        classItems.clear();
        while(cursor.moveToNext()){
            int idIndex = cursor.getInt(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.C_ID)));
            String className = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CLASS_NAME_KEY));
            String subjectName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SUBJECT_NAME_KEY));
            classItems.add(new ClassItem(idIndex,className,subjectName))
        }

    }*/

    private void loadData() {
        Cursor cursor = dbHelper.getClassTable();
        if (cursor != null) {
            try {
                classItems.clear();

                // Fetch column indices before the loop
                int idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.C_ID);
                int classNameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.CLASS_NAME_KEY);
                int subjectNameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.SUBJECT_NAME_KEY);
                int scheduledTimeIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.SCHEDULED_TIME_KEY);
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(idIndex);
                    String className = cursor.getString(classNameIndex);
                    String subjectName = cursor.getString(subjectNameIndex);
                    long scheduledTime = cursor.getLong(scheduledTimeIndex);
                    classItems.add(new ClassItem(id, className, subjectName, scheduledTime));
                }
            } finally {
                cursor.close();
            }
        }
    }

    private void gotoItemActivity(int position) {
        Intent intent = new Intent(this, StudentActivity.class);
        intent.putExtra("className", classItems.get(position).getClassName());
        intent.putExtra("subjectName", classItems.get(position).getSubjectName());
        intent.putExtra("position",position);
        intent.putExtra("cid", classItems.get(position).getCid());
        Log.d("MainActivity", "Navigating to StudentActivity with class: " + classItems.get(position).getClassName() + " and subject: " + classItems.get(position).getSubjectName());

        startActivity(intent);
    }

    private void showDialog() {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), MyDialog.CLASS_ADD_DIALOG);
        dialog.setListener((className, subName)-> addClass(className, subName, System.currentTimeMillis()));
    }

    private void addClass(String className, String subName, long scheduledTime) {
        long cid = dbHelper.addClass(className,subName);
        ClassItem classItem = new ClassItem(cid,className ,subName, scheduledTime);
        classItems.add(classItem);
        // classAdapter.notifyDataSetChanged();


    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        deleteClass(item.getGroupId());

        return super.onContextItemSelected(item);
    }

    private void deleteClass(int position) {
        dbHelper.deleteClass(classItems.get(position).getCid());
        classItems.remove(position);
        classAdapter.notifyItemRemoved(position);
    }




}