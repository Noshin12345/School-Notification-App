package com.example.schoolnotification;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ClassRoutineList extends AppCompatActivity {

    private Button btnAddNew, btnBack;
    private TextView tvTitle;
    private ArrayList<Routine> routines;
    private RoutineAdapter adapter;
    private ListView lvRoutineList;
    String weekday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        setContentView(R.layout.activity_class_routine_list);
        btnAddNew = findViewById(R.id.btnCreateNew);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        lvRoutineList = findViewById(R.id.listViewRoutines);

        if (i.hasExtra("WEEKDAY")) {
            weekday = i.getStringExtra("WEEKDAY");
            tvTitle.setText(weekday);
        }


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ClassRoutineList.this, ClassRoutine.class);
               startActivity(i);
                finish();
            }
        });

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ClassRoutineList.this, ClassRoutineFormActivity.class);
                startActivity(i);
            }
        });

        routines = new ArrayList<>();
        adapter = new RoutineAdapter(this, routines);
        lvRoutineList.setAdapter(adapter);

        lvRoutineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ClassRoutineList.this, ClassRoutineFormActivity.class);
                Routine routine = routines.get(position);

                String data = routine.weekday + "---"
                        + routine.subject + "---"
                        + routine.startHour + "---"
                        + routine.startMin + "---"
                        + routine.endHour + "---"
                        + routine.endMin + "---"
                        + routine.additionalInfo + "---"
                        + routine.notificationAlarm;

                i.putExtra("ID", routine.uid);
                i.putExtra("DATA", data);
                startActivity(i);
            }
        });

        loadData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadData();
    }

    public void loadData() {
        routines.clear();
        SchoolNotificationDB db = new SchoolNotificationDB(this);
        String query = "SELECT * FROM routine WHERE weekday = '" + weekday + "'";
        Cursor cursor = db.selectClassRoutineList(query);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String weekday = cursor.getString(1);
                String subject = cursor.getString(2);
                int startHour = cursor.getInt(3);
                int startMin = cursor.getInt(4);
                int endHour = cursor.getInt(5);
                int endMin = cursor.getInt(6);
                String additionalInfo = cursor.getString(7);
                String notificationAlarm = cursor.getString(8);

                Routine ls = new Routine(id, weekday, subject, startHour, startMin, endHour, endMin, additionalInfo, notificationAlarm);
                routines.add(ls);
            }
            cursor.close();
        }
        db.close();
        adapter.notifyDataSetChanged();
    }
}
