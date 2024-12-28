package com.example.schoolnotification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class StudyScheduleListActivity extends AppCompatActivity {
    private ArrayList<StudySchedule> studySchedules;
    private ListView lvStudyScheduleList;
    private StudyScheduleAdapter ssAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_schedule_list);

        TextView tvTitle = findViewById(R.id.tvTitle);
        lvStudyScheduleList = findViewById(R.id.lvStudyScheduleList);

        // Navigation/control buttons action
        findViewById(R.id.btnCreateNew).setOnClickListener(v -> {
            Intent intent = new Intent(StudyScheduleListActivity.this, StudyScheduleActivity.class);
            startActivity(intent);
            //finish();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        studySchedules = new ArrayList<>();
        ssAdapter = new StudyScheduleAdapter(this, studySchedules);
        lvStudyScheduleList.setAdapter(ssAdapter);

        lvStudyScheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(StudyScheduleListActivity.this, StudyScheduleActivity.class);
                StudySchedule routine = studySchedules.get(position);

                String data = routine.getDate() + "---"
                        + routine.getCourseName() + "---"
                        + routine.getStartTime()+ "---"
                        + routine.getEndTime() + "---"
                        + routine.getAdditionalInfo() + "---"
                        + routine.getAlarm();

                i.putExtra("ID", routine.getId());
                i.putExtra("DATA", data);
                startActivity(i);
            }
        });

        loadStudySchedules();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadStudySchedules();
    }

    private void loadStudySchedules() {
        studySchedules.clear();
        String query = "SELECT * FROM StudySchedule";
        StudyScheduleDB db = new StudyScheduleDB(this);
        Cursor cursor = db.selectStudySchedule(query);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String courseName = cursor.getString(cursor.getColumnIndexOrThrow("courseName"));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow("startTime"));
                String endTime = cursor.getString(cursor.getColumnIndexOrThrow("endTime"));
                String additionalInfo = cursor.getString(cursor.getColumnIndexOrThrow("additionalInfo"));
                String alarm = cursor.getString(cursor.getColumnIndexOrThrow("alarm"));

                StudySchedule schedule = new StudySchedule(id, date, courseName, startTime, endTime, additionalInfo, alarm);
                studySchedules.add(schedule);
            }
            cursor.close();

        }else{
            return;
        }
        db.close();
        ssAdapter.notifyDataSetInvalidated();
        ssAdapter.notifyDataSetChanged();
    }
}
