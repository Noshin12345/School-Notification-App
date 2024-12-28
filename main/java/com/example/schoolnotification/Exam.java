package com.example.schoolnotification;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Exam extends AppCompatActivity {

    private EditText classDate, startTime, endTime, additionalInfo, alarm, subject;
    private EditText tvCourseName;
    private Button buttonCancel, buttonSave, buttondelete;
    private Examdb examDB;
    private String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        classDate = findViewById(R.id.classDate);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.EndTime);
        additionalInfo = findViewById(R.id.Addition);
        alarm = findViewById(R.id.alarm);
        tvCourseName = findViewById(R.id.tvCourseName);
        buttonCancel = findViewById(R.id.buttoncancel);
        buttonSave = findViewById(R.id.buttonsave);
        buttondelete = findViewById(R.id.buttondelete);

        Intent i = getIntent();

        // Initialize database
        examDB = new Examdb(this);

        if (i.hasExtra("Value")) {
            String value = i.getStringExtra("Value");
            String[] values = value.split("---");

            classDate.setText(values[0]);
            tvCourseName.setText(values[1]);
            startTime.setText(values[2]);
            endTime.setText(values[3]);
            additionalInfo.setText(values[4]);
            alarm.setText(values[5]);

            buttonSave.setText("Update");
        }

        if (i.hasExtra("UID")) {
            uid = i.getStringExtra("UID");
        }

        // Set up date picker dialog for classDate
        classDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set up time picker dialog for startTime
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(startTime);
            }
        });

        // Set up time picker dialog for endTime
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(endTime);
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

        buttondelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uid.isEmpty()) {
                    Toast.makeText(Exam.this, "Nothing to delete", Toast.LENGTH_SHORT).show();
                } else {
                    examDB.deleteExam(uid);
                    examDB.close();
                    Toast.makeText(Exam.this, "Contact deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(Exam.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String dateStr = String.format(Locale.getDefault(), "%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                classDate.setText(dateStr);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePickerDialog(final EditText timeField) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(Exam.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String timeStr = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                timeField.setText(timeStr);
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(errorMessage);
        builder.setTitle("Error");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void httpRequest(final String keys[], final String values[]) {
        if (keys.length != values.length) {
            Log.e("exams", "Keys and values arrays have different lengths");
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

    private void validateInputs() {
        String date = classDate.getText().toString().trim();
        String start = startTime.getText().toString().trim();
        String end = endTime.getText().toString().trim();
        String courseName = tvCourseName.getText().toString().trim();
        String additional = additionalInfo.getText().toString().trim();
        String alarmTime = alarm.getText().toString().trim();

        if (date.isEmpty()) {
            showErrorDialog("Please enter the date.");
        } else if (courseName.isEmpty()) {
            showErrorDialog("Course name is missing.");
        } else if (start.isEmpty()) {
            showErrorDialog("Please enter the start time.");
        } else if (end.isEmpty()) {
            showErrorDialog("Please enter the end time.");
        } else {
            Examdb db = new Examdb(this);
            if (uid.isEmpty()) {
                uid = date + System.currentTimeMillis();
                db.insertExam(uid, date, courseName, start, end, additional, alarmTime);
                Toast.makeText(Exam.this, "Saved successfully.", Toast.LENGTH_LONG).show();
                String keys[] = {"action", "sid", "semester", "uid", "date", "courseName", "start", "end", "additional", "alarmTime"};
                String values[] = {"backup", "2020-1-60-221", "2024-1", uid, date, courseName, start, end, additional, alarmTime};
                httpRequest(keys, values);
                setDeadlineAlarm(date, start, courseName, additional);
                finish();
            } else {
                db.updateExam(uid, date, courseName, start, end, additional, alarmTime);
                Toast.makeText(Exam.this, "Updated successfully.", Toast.LENGTH_LONG).show();
                setDeadlineAlarm(date, start, courseName, additional);
            }

            //Intent intent = new Intent(Exam.this, ExamList.class);
           // startActivity(intent);
            finish();
        }
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
                requestExactAlarmPermission();
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
        setAlarm(uid, "Upcoming Exam", "You have an exam for " + courseName + " in 10 minutes", deadlineCalendar);
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
}
