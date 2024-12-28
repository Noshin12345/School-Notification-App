package com.example.schoolnotification;;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExamList extends AppCompatActivity {

    private Button btnAddNew, btnBack;
    private TextView tvTitle;
    private ArrayList<ExaminationList> examList;
    private ExamListAdapter adapter;
    private ListView lvExamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);

        btnAddNew = findViewById(R.id.btnCreateNew);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        lvExamList = findViewById(R.id.lvExamList);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //Intent i = new Intent(ExamList.this, Login.class);
               // startActivity(i);
                finish();
            }
        });

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ExamList.this, Exam.class);
                startActivity(i);
            }
        });

        examList = new ArrayList<>();
        adapter = new ExamListAdapter(this, examList);
        lvExamList.setAdapter(adapter);

        lvExamList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ExamList.this, Exam.class);
                ExaminationList exam = examList.get(position);
                String value = exam.date + "---" + exam.courseName + "---" + exam.starttime + "---" + exam.endtime + "---" + exam.additional + "---" + exam.alarmtime;
                i.putExtra("UID", exam.uid);
                i.putExtra("Value", value);
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
        examList.clear();
        Examdb db = new Examdb(this);
        Cursor rows = db.selectExams("SELECT * FROM Exams");
        if (rows.getCount() > 0) {
            while (rows.moveToNext()) {
                String uid = rows.getString(0);
                String date = rows.getString(1);
                String courseName = rows.getString(2);
                String start = rows.getString(3);
                String end = rows.getString(4);
                String additional = rows.getString(5);
                String alarmtime = rows.getString(6);
                ExaminationList exam = new ExaminationList(uid, date, courseName, start, end, additional, alarmtime);
                examList.add(exam);
            }
            rows.close();
        }
        db.close();
        adapter.notifyDataSetChanged();
    }
    public void onStart(){
        super.onStart();
        // Send request to remote server for loading data
        String keys[] = {"action", "sid", "semester"};
        String values[] = {"restore", "2020-1-60-221", "2024-1"};
        httpRequest(keys, values);
    }
    private void httpRequest(final String keys[], final String values[]) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params = new ArrayList<>();
                for (int i = 0; i < keys.length; i++) {
                    params.add(new BasicNameValuePair(keys[i], values[i]));
                }
                String url = "https://www.muthosoft.com/univ/cse489/index.php";
                try {
                    String data = RemoteAccess.getInstance().makeHttpRequest(url, "POST", params);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(String data) {
                if (data != null) {
                    updateLocalDBByServerData(data);
                } else {
                    Toast.makeText(ExamList.this, "Failed to retrieve data from server", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void updateLocalDBByServerData(String data) {
        try {
            JSONObject jo = new JSONObject(data);
            Examdb db = new Examdb(ExamList.this);

            if (jo.has("classes")) {
                JSONArray ja = jo.getJSONArray("classes");

                for (int i = 0; i < ja.length(); i++) {
                    JSONObject Exam = ja.getJSONObject(i);

                    // Log the JSON object for debugging
                    Log.d("JSON Object",  Exam.toString());

                    // Check if the object has the required keys
                    if (Exam.has("uid") && Exam.has("date") && Exam.has("courseName") &&
                            Exam.has("start")&&
                    Exam.has("end") && Exam.has("additional") && Exam.has("alarmtime")) {

                        String uid = Exam.getString("uid");
                        String date = Exam.getString("date");
                        String courseName = Exam.getString("courseName");
                        String start = Exam.getString("start");
                        String end = Exam.getString("end");

                        String additional = Exam.getString("additional");
                        String alarmtime =Exam.getString("alarmtime");

                        Log.d("ID", uid);
                        Log.d("Date", date);
                        Log.d("CourseName", courseName);
                        Log.d("Start", start);
                        Log.d("end", end);
                        Log.d("Additional", additional);
                        Log.d("AlarmTime", alarmtime);

                        // Insert the assignment into the database
                        try {
                            db.insertExam(uid, date, courseName, start,end, additional, alarmtime);
                        } catch (Exception e) {
                            Log.e("ExamList", "Error inserting assignment into database", e);
                        }
                    } else {
                        // Log missing keys
                        Log.e("ExamList", "JSON object missing required keys: " + Exam.toString());
                    }
                }
            }
            db.close();
        } catch (Exception e) {
            Log.e("ExamList", "Error updating local database from server data", e);
        }
    }
}
