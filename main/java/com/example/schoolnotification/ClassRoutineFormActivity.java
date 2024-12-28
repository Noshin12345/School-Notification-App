package com.example.schoolnotification;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;


public class ClassRoutineFormActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etWeekDay, etSubject, etStartTime, etEndTime, etAdditionalInfo;
    private RadioGroup rgNotificationAlarm;
    private RadioButton rbNotification, rbAlarm, rbBoth;
    private Button buttonSave, buttonCancel;
    private String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_routine_form);
        Intent i = getIntent();

        etWeekDay = findViewById(R.id.etWeekDay);
        etSubject = findViewById(R.id.etSubject);
        etStartTime = findViewById(R.id.startTime);
        etEndTime = findViewById(R.id.EndTime);
        etAdditionalInfo = findViewById(R.id.Addition);

        rgNotificationAlarm = findViewById(R.id.rgNotificationAlarm);
        rbNotification = findViewById(R.id.rbNotification);
        rbAlarm = findViewById(R.id.rbAlarm);
        rbBoth = findViewById(R.id.rbBoth);

        buttonSave = findViewById(R.id.buttonsave);
        buttonCancel = findViewById(R.id.buttoncancel);

        requestExactAlarmPermission();
        if (i.hasExtra("DATA")) {
            String value = i.getStringExtra("DATA");
            try {
                String[] values = value.split("---");

                etWeekDay.setText(values[0]);
                etSubject.setText(values[1]);
                int startMin = Integer.parseInt(values[3]);
                if(startMin<10){
                    String StartTime = values[2] + ":" + "0"+values[3];
                    etStartTime.setText(StartTime);
                }else{
                    String StartTime = values[2] + ":" +values[3];
                    etStartTime.setText(StartTime);
                }

                int endMin = Integer.parseInt(values[5]);
                if(endMin<10){
                    String EndTime = values[4] + ":" + "0"+values[5];
                    etEndTime.setText(EndTime);
                }else{
                    String EndTime = values[4] + ":" +values[5];
                    etEndTime.setText(EndTime);
                }

                etAdditionalInfo.setText(values[6]);

                String NotificationAlarm = values[7];
                switch (NotificationAlarm) {
                    case "Notification":
                        rbNotification.setChecked(true);
                        break;
                    case "Alarm":
                        rbAlarm.setChecked(true);
                        break;
                    case "Both":
                        rbBoth.setChecked(true);
                        break;
                    default:
                        // Handle unknown value or leave all unchecked
                        break;
                }
                buttonSave.setText("Update");
            } catch (Exception e) {
                e.printStackTrace();
                showErrorDialog(String.valueOf(e));
                Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
            }
        }

        if (i.hasExtra("ID")) {
            uid = i.getStringExtra("ID");
        }

        buttonSave.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        etStartTime.setOnClickListener(this);
        etEndTime.setOnClickListener(this);
        etWeekDay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonsave) {
            saveData();
        } else if (v.getId() == R.id.buttoncancel) {
            finish();
        } else if (v == etStartTime) {
            showStartTimePicker();
        } else if (v == etEndTime) {
            showEndTimePicker();
        } else if (v == etWeekDay) {
            showDayOfWeekDialog(v);
        }
    }

    private void saveData() {
        String weekday = etWeekDay.getText().toString().trim();
        String subject = etSubject.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        int startHour=0;
        int startMin = 0;
        String endTime = etEndTime.getText().toString().trim();
        int endHour=0;
        int endMin=0;
        String additionalInfo = etAdditionalInfo.getText().toString().trim();
        String err="";

        // Retrieve data from selected RadioButton in RadioGroup
        String notificationAlarm = "";
        try {
            int selectedRadioButtonId = rgNotificationAlarm.getCheckedRadioButtonId();
            if (selectedRadioButtonId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                notificationAlarm = selectedRadioButton.getText().toString();
            } else {
                notificationAlarm = "Nothing";
            }
        } catch(NullPointerException e) {
            // Handle the case when no RadioButton is selected
        }

        // Validations for input fields
        if(weekday.isEmpty()){
            err+="Give a weekday\n";
        }
        if(subject.isEmpty()){
            err+="Give a subject\n";
        }
        if(startTime.isEmpty()){
            err+="Choose a start time\n";
        } else {
            String start[] = startTime.split(":");
            startHour = Integer.parseInt(start[0]);
            startMin = Integer.parseInt(start[1]);
        }
        if(endTime.isEmpty()){
            err+="Choose an end time\n";
        } else {
            String end[] = endTime.split(":");
            endHour = Integer.parseInt(end[0]);
            endMin = Integer.parseInt(end[1]);
        }
        if(additionalInfo.isEmpty()){
            additionalInfo = "  ";
        }

        // Display error message if there are any errors
        if (err.length() > 0) {
            showErrorDialog(err);
            return;
        }

        // If no errors, proceed to save data and set deadline alarm
        SchoolNotificationDB DB = new SchoolNotificationDB(ClassRoutineFormActivity.this);
        if (uid.length() == 0) {
            String key = weekday + subject + startHour + System.currentTimeMillis();
            uid = key;
            DB.insertRoutine(uid, weekday, subject, startHour, startMin, endHour, endMin, additionalInfo, notificationAlarm);
            Toast.makeText(ClassRoutineFormActivity.this, "Saved successfully.", Toast.LENGTH_LONG).show();
        } else {
            DB.updateRoutine(uid, weekday, subject, startHour, startMin, endHour, endMin, additionalInfo, notificationAlarm);
            Toast.makeText(ClassRoutineFormActivity.this, "Updated", Toast.LENGTH_LONG).show();
        }
        DB.close();
        String[] keys = {"action", "sid", "semester", "uid", "weekday", "subject", "startHour", "startMin", "endHour", "endMin", "additionalInfo", "notificationAlarm"};
        String[] values = {"backup", "2020-1-60-026", "2024-1", uid, weekday, subject, String.valueOf(startHour), String.valueOf(startMin), String.valueOf(endHour), String.valueOf(endMin), additionalInfo, notificationAlarm};

        httpRequest(keys, values);

        // Set deadline alarm 10 minutes before the start time
        setDeadlineAlarm(weekday, startTime, subject, additionalInfo);

        finish();
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

    public void showStartTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        etStartTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                        //return null;
                    }
                }, hourOfDay, minute, true);

        timePickerDialog.show();
    }

    public void showEndTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        etEndTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                        //return null;
                    }
                }, hourOfDay, minute, true);

        timePickerDialog.show();
    }
    public void showDayOfWeekDialog(View view) {
        final String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Day of Week");
        builder.setItems(daysOfWeek, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedDay = daysOfWeek[which];
                etWeekDay.setText(selectedDay);
            }
        });
        builder.show();
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

    private int getDayOfWeek(String dayOfWeek) {
        switch (dayOfWeek.toLowerCase()) {
            case "sunday":
                return Calendar.SUNDAY;
            case "monday":
                return Calendar.MONDAY;
            case "tuesday":
                return Calendar.TUESDAY;
            case "wednesday":
                return Calendar.WEDNESDAY;
            case "thursday":
                return Calendar.THURSDAY;
            case "friday":
                return Calendar.FRIDAY;
            case "saturday":
                return Calendar.SATURDAY;
            default:
                throw new IllegalArgumentException("Invalid day of week: " + dayOfWeek);
        }
    }
    private void setDeadlineAlarm(String weekday, String startTime, String courseName, String additionalInfo) {
        // Parse date and start time
        String[] timeParts = startTime.split(":");
        //int year = Integer.parseInt(dateParts[0]);
       // int month = Integer.parseInt(dateParts[1]) - 1; // Calendar.MONTH is zero-based
       // int day = Integer.parseInt(dateParts[2]);
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Create a Calendar instance for the start time
        Calendar startTimeCalendar = Calendar.getInstance();
       // startTimeCalendar.set(Calendar.YEAR, year);
        //startTimeCalendar.set(Calendar.MONTH, month);
       // startTimeCalendar.set(Calendar.DAY_OF_MONTH, day);
        startTimeCalendar.set(Calendar.DAY_OF_WEEK,getDayOfWeek(weekday));
        startTimeCalendar.set(Calendar.HOUR_OF_DAY, hour);
        startTimeCalendar.set(Calendar.MINUTE, minute);
        startTimeCalendar.set(Calendar.SECOND, 0);

        // Subtract 10 minutes from the start time
        startTimeCalendar.add(Calendar.MINUTE, -10);

        // Set the alarm
        setAlarm(uid, "Upcoming class", "You have an class for " + courseName + " in 10 minutes", startTimeCalendar);
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



