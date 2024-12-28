package com.example.schoolnotification;

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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StudyScheduleActivity extends AppCompatActivity {

    private EditText classDateEditText, startTimeEditText, endTimeEditText,
            additionalInfoEditText, alarmEditText;
    private TextView courseNameTextView;
    private Button cancelButton, saveButton;

    private StudyScheduleDB studyScheduleDB;
    private int scheduleId = -1; // default value indicating a new schedule

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_schedule);
        Intent i = getIntent();
        // Initialize views
        classDateEditText = findViewById(R.id.classDate);
        startTimeEditText = findViewById(R.id.startTime);
        endTimeEditText = findViewById(R.id.EndTime);
        additionalInfoEditText = findViewById(R.id.Addition);
        alarmEditText = findViewById(R.id.alarm);
        courseNameTextView = findViewById(R.id.tvCourseName);
        cancelButton = findViewById(R.id.buttoncancel);
        saveButton = findViewById(R.id.buttonsave);

        studyScheduleDB = new StudyScheduleDB(this);


        if(i.hasExtra("DATA")) {
            String value = i.getStringExtra("DATA");
            String[] values = value.split("---");

            classDateEditText.setText(values[0]);
            startTimeEditText.setText(values[2]);
            endTimeEditText.setText(values[3]);
            additionalInfoEditText.setText(values[4]);
            alarmEditText.setText(values[5]);
            courseNameTextView.setText(values[1]);
        }

        if(i.hasExtra("ID")) {
            scheduleId = i.getIntExtra("ID",-1);
        }
        classDateEditText.setOnClickListener(v -> showDate());

        // Set up click listeners for time pickers
        startTimeEditText.setOnClickListener(v -> showStartTimePicker());

        endTimeEditText.setOnClickListener(v -> showEndTimePicker());

        //alarmEditText.setOnClickListener(v -> showTimePickerDialog(alarmEditText));

        // Set up click listeners for buttons
        cancelButton.setOnClickListener(v -> finish());

        saveButton.setOnClickListener(v -> saveSchedule());

        // Set course name (This could be passed via Intent or set directly in the code)
        courseNameTextView.setText("Course Name Example");
    }

    public void showStartTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTimeEditText.setText(String.format("%02d:%02d", hourOfDay, minute));
                        //return String.format("%02d:%02d", hourOfDay, minute);
                    }
                }, hourOfDay, minute, true);

        timePickerDialog.show();
    }
    private void showDate() {
        DatePicker datePicker = new DatePicker(this);
        int date = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int date) {
                String d = Integer.toString(date);
                if(date<10) {
                    d = "0" + date;
                }

                if((month+1)<10){
                    classDateEditText.setText(year + "-" + "0" +(month+1)+"-"+d);

                }else{
                    classDateEditText.setText(year + "-" +(month+1)+"-"+d);
                }

            }
        }, year, month, date);
        datePickerDialog.show();

    }
    public void showEndTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        endTimeEditText.setText(String.format("%02d:%02d", hourOfDay, minute));
                        //return null;
                    }
                }, hourOfDay, minute, true);

        timePickerDialog.show();
    }
    private void saveSchedule() {
        String classDate = classDateEditText.getText().toString().trim();
        String startTime = startTimeEditText.getText().toString().trim();
        String endTime = endTimeEditText.getText().toString().trim();
        String additionalInfo = additionalInfoEditText.getText().toString().trim();
        String alarmTime = alarmEditText.getText().toString().trim();
        String courseName = courseNameTextView.getText().toString().trim();

        String errMsg = "";

        // Validate class date
        if (classDate.isEmpty()) {
            errMsg += "Please enter the class date\n";
        }

        // Validate start time
        if (startTime.isEmpty()) {
            errMsg += "Please enter the start time\n";
        }

        // Validate end time
        if (endTime.isEmpty()) {
            errMsg += "Please enter the end time\n";
        }

        // Validate alarm time
        if (alarmTime.isEmpty()) {
            errMsg += "Please enter the alarm time\n";
        }

        if (errMsg.length() > 0) {

            showErrorDialog(errMsg);
            System.out.println(errMsg);
            return;
        }
        // Save the schedule to the database
        if (scheduleId == -1) {
            // Insert new schedule
            studyScheduleDB.insertStudySchedule(classDate, courseName, startTime, endTime, additionalInfo, alarmTime);
            Toast.makeText(this, "Schedule saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Update existing schedule
            studyScheduleDB.updateStudySchedule(scheduleId, classDate, courseName, startTime, endTime, additionalInfo, alarmTime);
            Toast.makeText(this, "Schedule updated successfully", Toast.LENGTH_SHORT).show();
        }

        String[] keys = {"action", "sid", "semester", "id", "classDate", "courseName", "startTime", "endTime", "additionalInfo", "alarmTime"};
        String[] values = {"backup", "2020-1-60-006", "2024-1", String.valueOf(scheduleId), classDate, courseName, startTime, endTime, additionalInfo, alarmTime};

        // Make HTTP request
        httpRequest(keys, values);

        // Set deadline alarm 10 minutes before the start time
        setDeadlineAlarm(classDate, startTime, courseName, additionalInfo);

        // Close the activity
        finish();
    }

    private void setDeadlineAlarm(String classDate, String startTime, String courseName, String additionalInfo) {
        // Parse date and start time
        String[] dateParts = classDate.split("-");
        String[] timeParts = startTime.split(":");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1; // Calendar.MONTH is zero-based
        int day = Integer.parseInt(dateParts[2]);
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Create a Calendar instance for the start time
        Calendar startTimeCalendar = Calendar.getInstance();
        startTimeCalendar.set(year, month, day, hour, minute, 0);

        // Subtract 10 minutes from the start time
        startTimeCalendar.add(Calendar.MINUTE, -10);

        // Set the alarm
        setAlarm(courseName.hashCode(), "Upcoming Class", "You have a class for " + courseName + " in 10 minutes", startTimeCalendar);
    }

    private void setAlarm(int requestCode, String title, String message, Calendar deadlineCalendar) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

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

    private void showErrorDialog(String errorMessage){
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setMessage(errorMessage);
        builder.setTitle("Error");
        builder.setCancelable(true);
        builder.setPositiveButton("back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void httpRequest(final String keys[],final String values[]){
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... voids) {
                List<NameValuePair> params=new ArrayList<NameValuePair>();
                for (int i=0; i<keys.length; i++){
                    params.add(new BasicNameValuePair(keys[i],values[i]));
                }
                String url= "https://www.muthosoft.com/univ/cse489/index.php";
                try {
                    String data= RemoteAccess.getInstance().makeHttpRequest(url,"POST",params);
                    return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(String data){
                if(data!=null){
                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}

