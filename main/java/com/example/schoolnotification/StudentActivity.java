package com.example.schoolnotification;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String className;
    private String subjectName;
    private int position;
    private RecyclerView recyclerView;
    private StudentAdapter stud_ad;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<StudentItem> studentItems = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private long cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        dbHelper = new DatabaseHelper(this);
        className = getIntent().getStringExtra("className");
        subjectName = getIntent().getStringExtra("subjectName");
        position = getIntent().getIntExtra("position", -1);
        cid = getIntent().getLongExtra("cid", -1);

        setToolbar();
        loadData();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        stud_ad = new StudentAdapter(this, studentItems);
        recyclerView.setAdapter(stud_ad);
        stud_ad.setOnItemClickListener(position -> changeStatus(position));
        loadStatusData();

    }



    private void loadData() {
        Cursor cursor = dbHelper.getStudentTable(cid);
        if (cursor != null) {
            try {
                studentItems.clear();
                while (cursor.moveToNext()) {
                    int idIndex = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.S_ID));
                    String studentName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_NAME_KEY));
                    int rollNumber = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_ROLL_KEY));
                    studentItems.add(new StudentItem(idIndex, rollNumber, studentName));
                }
            } finally {
                cursor.close();
            }
        }

    }

    private void changeStatus(int position) {
        String status = studentItems.get(position).getStatus();
        if (status.equals("P")) status = "A";
        else status = "P";
        studentItems.get(position).setStatus(status);
        stud_ad.notifyItemChanged(position);
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.title_tool);
        TextView subtitle = toolbar.findViewById(R.id.subtitle_tool);
        ImageButton back = toolbar.findViewById(R.id.back_bu);
        ImageButton timer= toolbar.findViewById(R.id.time);
        ImageButton save = toolbar.findViewById(R.id.save_bu);
        Button addButton = toolbar.findViewById(R.id.add_stud);
        save.setOnClickListener(v -> saveStatus());

        title.setText(className);
        subtitle.setText(subjectName);


          timer.setOnClickListener(v -> {
            // Create an Intent to start the TimerActivity
            Intent intent = new Intent(StudentActivity.this, TimerActivity.class);
            startActivity(intent);
        });




        back.setOnClickListener(v -> onBackPressed());
        addButton.setOnClickListener(v -> showAddStudentDialog());
        toolbar.setOnMenuItemClickListener(menuItem -> onMenuClick(menuItem));
    }

    private void saveStatus() {
        for (StudentItem studentItem : studentItems) {
            String status = studentItem.getStatus();
            if (!"P".equals(status)) {
                status = "A";
            }
            long value = dbHelper.addStatus(studentItem.getSid(), status);
            if(value==-1)dbHelper.updateStatus(studentItem.getSid(), status);
        }
    }
    private void loadStatusData(){
        for (StudentItem studentItem : studentItems) {
            String status = dbHelper.getStatus(studentItem.getSid());
            if (status != null)studentItem.setStatus(status);
        }
        stud_ad.notifyDataSetChanged();

    }
    private boolean onMenuClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.add_stud) {
            showAddStudentDialog();
        }
        return true;
    }

    private void showAddStudentDialog() {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), MyDialog.STUDENT_ADD_DIALOG);
        dialog.setListener((roll, name) -> addStudent(roll, name));
    }

    private void addStudent(String roll, String name) {
        try {
            int rollNumber = Integer.parseInt(roll);
            long sid = dbHelper.addStudent(cid, name, rollNumber);
            StudentItem studentItem = new StudentItem(sid, rollNumber, name);
            studentItems.add(studentItem);
            stud_ad.notifyDataSetChanged();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid roll number. Please enter a valid number.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        deleteStudent(item.getGroupId());

        return super.onContextItemSelected(item);
    }
    private void deleteStudent(int position) {
        dbHelper.deleteStudent(studentItems.get(position).getSid());
        studentItems.remove(position);
        stud_ad.notifyItemRemoved(position);


    }
    }





