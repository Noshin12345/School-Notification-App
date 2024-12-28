package com.example.schoolnotification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;


import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvClassRoutine, tvExam, tvStudyPlan, tvAssignment, tvAttendance, tvSetting;
    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvClassRoutine = findViewById(R.id.tvClassRoutine);
        tvExam = findViewById(R.id.tvExam);
        tvStudyPlan = findViewById(R.id.tvStudyPlan);
        tvAssignment = findViewById(R.id.tvAssignment);
        tvAttendance = findViewById(R.id.tvAttendance);
        tvSetting = findViewById(R.id.tvSetting);
        btnExit = findViewById(R.id.btnExit);

        tvClassRoutine.setOnClickListener(this);
        tvExam.setOnClickListener(this);
        tvStudyPlan.setOnClickListener(this);
        tvAssignment.setOnClickListener(this);
        tvAttendance.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        btnExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvClassRoutine)
        {
            Intent i = new Intent(MainActivity.this,ClassRoutine.class);
            startActivity(i);
            //finish();

        } else if (v.getId() == R.id.tvExam)
        {
            Intent i = new Intent(MainActivity.this, ExamList.class);
            startActivity(i);
           // finish();

        } else if (v.getId() == R.id.tvStudyPlan)
        {
            Intent i = new Intent(MainActivity.this,StudyScheduleListActivity.class);
            startActivity(i);
            //finish();

        } else if (v.getId() == R.id.tvAssignment)
        {
            Intent i = new Intent(MainActivity.this,AssignmentList.class);
            startActivity(i);
            //finish();

        } else if (v.getId() == R.id.tvAttendance) {
            Intent i = new Intent(MainActivity.this,AttendanceMainActivity.class);
            startActivity(i);
           // finish();

        } else if (v.getId() == R.id.tvSetting) {

        } else if (v.getId() == R.id.btnExit) {
            finish();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        String[] keys = {"action", "sid", "semester"};
        String[] values = {"restore", "2020-1-60-026", "2024-27"};
        httpRequest(keys, values);
        String[] keys1 = {"action", "sid", "semester"};
        String[] values1 = {"restore", "2020-1-60-006", "2024-1"};
        httpRequest1(keys1, values1);
    }

    private void httpRequest(final String[] keys, final String[] values) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params = new ArrayList<>();
                for (int i = 0; i < keys.length; i++) {
                    params.add(new BasicNameValuePair(keys[i], values[i]));
                }
                String url = "https://www.muthosoft.com/univ/cse489/index.php";
                try {
                    return RemoteAccess.getInstance().makeHttpRequest(url, "POST", params);
                } catch (Exception e) {
                    Log.e("HttpRequest", "Error making HTTP request", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String data) {
                if (data != null) {
                    updateLocalDBByServerData(data);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to retrieve data from server", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void updateLocalDBByServerData(String data) {
        try {
            JSONObject jo = new JSONObject(data);
            if (jo.has("classes")) {
                List<Routine> classes = new ArrayList<>();
                JSONArray ja = jo.getJSONArray("classes");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject summary = ja.getJSONObject(i);

                    String id = summary.getString("id");
                    String weekday = summary.getString("weekday");
                    String subject = summary.getString("subject");
                    int startHour = summary.getInt("startHour");
                    int startMin = summary.getInt("startMin");
                    int endHour = summary.getInt("endHour");
                    int endMin = summary.getInt("endMin");
                    String additionalInfo = summary.getString("additionalInfo");
                    String notificationAlarm = summary.getString("notificationAlarm");

                    Routine routine = new Routine(id, weekday, subject, startHour, startMin, endHour, endMin, additionalInfo, notificationAlarm);
                    classes.add(routine);
                }

                // Update your local database or UI with the `classes` list as needed
                // Example: updateUI(classes);
            }
        } catch (JSONException e) {
            Log.e("UpdateLocalDB", "Error parsing JSON data", e);
        }
    }

    private void httpRequest1(final String[] keys, final String[] values) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params = new ArrayList<>();
                for (int i = 0; i < keys.length; i++) {
                    params.add(new BasicNameValuePair(keys[i], values[i]));
                }
                String url = "https://www.muthosoft.com/univ/cse489/index.php";
                try {
                    return RemoteAccess.getInstance().makeHttpRequest(url, "POST", params);
                } catch (Exception e) {
                    Log.e("HttpRequest", "Error making HTTP request", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String data) {
                if (data != null) {
                    updateLocalDBByServerData1(data);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to retrieve data from server", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void updateLocalDBByServerData1(String data) {
        try {
            JSONObject jo = new JSONObject(data);
            if (jo.has("classes")) {
                List<StudySchedule> studySchedules = new ArrayList<>();
                JSONArray ja = jo.getJSONArray("classes");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject summary = ja.getJSONObject(i);

                    int id = summary.getInt("id");
                    String date = summary.getString("classdate");
                    String courseName = summary.getString("courseName");
                    String startTime = summary.getString("startTime");
                    String endTime = summary.getString("endTime");
                    String additionalInfo = summary.getString("additionalInfo");
                    String alarm = summary.getString("alarm");

                    StudySchedule schedule = new StudySchedule(id, date, courseName, startTime, endTime, additionalInfo, alarm);
                    studySchedules.add(schedule);
                }

                // Update your local database or UI with the `studySchedules` list as needed
                // Example: updateUI(studySchedules);
            }
        } catch (JSONException e) {
            Log.e("UpdateLocalDB", "Error parsing JSON data", e);
        }
    }



}
