package com.example.schoolnotification;

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

public class AssignmentList extends AppCompatActivity {
    private Button btnAddNew, btnBack;
    private TextView tvTitleContactList;
    private ArrayList<AssignmenLIST> assignments;
    private AssignmentListAdapter adapter;
    private ListView lvAssignmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_list);

        btnAddNew = findViewById(R.id.btnCreateNew);
        btnBack = findViewById(R.id.btnBack);
        tvTitleContactList = findViewById(R.id.tvTitle);
        lvAssignmentList = findViewById(R.id.lvContactList);

        btnBack.setOnClickListener(v -> {
            // Uncomment and implement the back navigation if needed
            // Intent i = new Intent(AssignmentList.this, Login.class);
            // startActivity(i);
            finish();
        });

        btnAddNew.setOnClickListener(v -> {
            Intent i = new Intent(AssignmentList.this, Assignment.class);
            startActivity(i);
        });

        assignments = new ArrayList<>();
        adapter = new AssignmentListAdapter(this, assignments);
        lvAssignmentList.setAdapter(adapter);

        lvAssignmentList.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(AssignmentList.this, Assignment.class);
            AssignmenLIST assignment = assignments.get(position);
            String value = assignment.date + "---" + assignment.courseName + "---" + assignment.deadline + "---" + assignment.additional + "---" + assignment.alarmtime;
            i.putExtra("UID", assignment.uid);
            i.putExtra("Value", value);
            startActivity(i);
        });

        loadData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadData();
    }

    public void loadData() {
        assignments.clear();
        Assignmentdb db = new Assignmentdb(this);
        Cursor rows = db.selectAssignments("SELECT * FROM assignments");
        if (rows.getCount() > 0) {
            while (rows.moveToNext()) {
                String uid = rows.getString(0);
                String date = rows.getString(1);
                String courseName = rows.getString(2);
                String deadline = rows.getString(3);
                String additional = rows.getString(4);
                String alarmtime = rows.getString(5);
                AssignmenLIST assignment = new AssignmenLIST(uid, date, courseName, deadline, additional, alarmtime);
                assignments.add(assignment);
            }
            rows.close();
        }
        db.close();
        adapter.notifyDataSetChanged();
    }


    @Override
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
                    Toast.makeText(AssignmentList.this, "Failed to retrieve data from server", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void updateLocalDBByServerData(String data) {
        try {
            JSONObject jo = new JSONObject(data);
            Assignmentdb db = new Assignmentdb(AssignmentList.this);

            if (jo.has("classes")) {
                JSONArray ja = jo.getJSONArray("classes");

                for (int i = 0; i < ja.length(); i++) {
                    JSONObject Exam = ja.getJSONObject(i);

                    // Log the JSON object for debugging
                    Log.d("JSON Object",  Exam.toString());

                    // Check if the object has the required keys
                    if (Exam.has("uid") && Exam.has("date") && Exam.has("courseName") &&
                            Exam.has("deadline") && Exam.has("additional") && Exam.has("alarmtime")) {

                        String uid = Exam.getString("uid");
                        String date = Exam.getString("date");
                        String courseName = Exam.getString("courseName");
                        String deadline = Exam.getString("deadline");
                        String additional = Exam.getString("additional");
                        String alarmtime =Exam.getString("alarmtime");

                        Log.d("ID", uid);
                        Log.d("Date", date);
                        Log.d("CourseName", courseName);
                        Log.d("Deadline", deadline);
                        Log.d("Additional", additional);
                        Log.d("AlarmTime", alarmtime);

                        // Insert the assignment into the database
                        try {
                            db.insertAssignment(uid, date, courseName, deadline, additional, alarmtime);
                        } catch (Exception e) {
                            Log.e("AssignmentList", "Error inserting assignment into database", e);
                        }
                    } else {
                        // Log missing keys
                        Log.e("AssignmentList", "JSON object missing required keys: " + Exam.toString());
                    }
                }
            }
            db.close();
        } catch (Exception e) {
            Log.e("AssignmentList", "Error updating local database from server data", e);
        }
    }

}
