package com.example.schoolnotification;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.app.AlarmManager;

public class Assignment extends AppCompatActivity {

    private EditText classDate, deadlineTime, additionalInfo, alarm, subject;
    private TextView tvCourseName;
    private Assignmentdb assignmentDB;
    private Button buttonCancel, buttonSave, buttonDelete;
    private String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        Intent i = getIntent();

        // Initialize database
        assignmentDB = new Assignmentdb(this);

        // Initialize UI elements
        classDate = findViewById(R.id.classDate);
        deadlineTime = findViewById(R.id.startTime);
        additionalInfo = findViewById(R.id.Addition);
        alarm = findViewById(R.id.alarm);
        tvCourseName = findViewById(R.id.tvCourseName);
        buttonCancel = findViewById(R.id.buttoncancel);
        buttonSave = findViewById(R.id.buttonsave);
        buttonDelete = findViewById(R.id.buttondelete);

        if (i.hasExtra("Value")) {
            String value = i.getStringExtra("Value");
            String[] values = value.split("---");

            classDate.setText(values[0]);
            tvCourseName.setText(values[1]);
            deadlineTime.setText(values[2]);
            additionalInfo.setText(values[3]);
            alarm.setText(values[4]);

            buttonSave.setText("Update");
        }
        requestExactAlarmPermission();
        if (i.hasExtra("UID")) {
            uid = i.getStringExtra("UID");
        }

        classDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        deadlineTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(deadlineTime);
            }
        });

        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(alarm);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uid.isEmpty()) {
                    Toast.makeText(Assignment.this, "Nothing to delete", Toast.LENGTH_SHORT).show();
                } else {
                    assignmentDB.deleteAssignment(uid);
                    assignmentDB.close();
                    Toast.makeText(Assignment.this, "Assignment deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void httpRequest(final String keys[], final String values[]) {
        if (keys.length != values.length) {
            Log.e("assignments", "Keys and values arrays have different lengths");
            return;
        }

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

            protected void onPostExecute(String data) {
                if (data != null) {
                    Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(Assignment.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String dateStr = String.format(Locale.getDefault(), "%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                classDate.setText(dateStr);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePickerDialog(final EditText editText) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format("%02d:%02d", hourOfDay, minute);
                editText.setText(time);
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void validateInputs() {
        String date = classDate.getText().toString().trim();
        String deadline = deadlineTime.getText().toString().trim();
        String courseName = tvCourseName.getText().toString().trim();
        String additional = additionalInfo.getText().toString().trim();
        String alarmTime = alarm.getText().toString().trim();

        if (date.isEmpty() || deadline.isEmpty() || courseName.isEmpty()) {
            showErrorDialog("Please fill in all the fields.");
        } else {
            Assignmentdb db = new Assignmentdb(this);
            if (uid.isEmpty()) {
                uid = date + System.currentTimeMillis();
                db.insertAssignment(uid, date, courseName, deadline, additional, alarmTime);
                Toast.makeText(Assignment.this, "Saved successfully.", Toast.LENGTH_LONG).show();

                String keys[] = {"action", "sid", "semester", "uid", "date", "courseName", "deadline", "additional", "alarmTime"};
                String values[] = {"backup", "2020-1-60-221", "2024-1", uid, date, courseName, deadline, additional, alarmTime};
                httpRequest(keys, values);
            } else {
                db.updateAssignment(uid, date, courseName, deadline, additional, alarmTime);
                Toast.makeText(Assignment.this, "Updated successfully.", Toast.LENGTH_LONG).show();
            }
            db.close();

            setDeadlineAlarm(date, deadline, courseName, additional);

            //Intent i = new Intent(Assignment.this, AssignmentList.class);
            //startActivity(i);
            finish();
        }
    }

    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(errorMessage)
                .setTitle("Error")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setAlarm(String uid, String title, String message, Calendar deadlineCalendar) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, uid.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule the alarm
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                Log.e("Alarm", "Cannot set exact alarms without permission");
                Toast.makeText(this, "Cannot set exact alarms without permission", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("Alarm", "Setting exact alarm for: " + deadlineCalendar.getTimeInMillis());
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, deadlineCalendar.getTimeInMillis(), pendingIntent);
        }
    }


    private void setDeadlineAlarm(String date, String startTime, String courseName, String additionalInfo) {
        Calendar deadlineCalendar = Calendar.getInstance();
        // Assuming date and startTime are in "yyyy-MM-dd" and "HH:mm" formats respectively
        String[] dateParts = date.split("-");
        String[] timeParts = startTime.split(":");
        deadlineCalendar.set(Calendar.YEAR, Integer.parseInt(dateParts[0]));
        deadlineCalendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
        deadlineCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[2]));
        deadlineCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
        deadlineCalendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        deadlineCalendar.set(Calendar.SECOND, 0);

        // Set the alarm 10 minutes before the deadline
        deadlineCalendar.add(Calendar.MINUTE, -10);

        // Log the set alarm time
        Log.d("Alarm", "Setting deadline alarm for: " + deadlineCalendar.getTime());

        // Set the alarm
        setAlarm(uid, "Upcoming Assignment", "You have an assignment for " + courseName + " in 10 minutes", deadlineCalendar);
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }

    private void saveAssignmentDetails() {
        String date = classDate.getText().toString();
        String deadline = deadlineTime.getText().toString();
        String additional = additionalInfo.getText().toString();
        String alarmTime = alarm.getText().toString();
        String courseName = tvCourseName.getText().toString();

        // Save in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("AssignmentDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Date", date);
        editor.putString("Deadline", deadline);
        editor.putString("AdditionalInfo", additional);
        editor.putString("Alarm", alarmTime);
        editor.putString("CourseName", courseName);
        editor.apply();
    }
}
