package com.example.schoolnotification;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class TimerActivity extends AppCompatActivity{
    private long classId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        classId = getIntent().getLongExtra("CLASS_ID",-1);
        // existing code...

        // Set notification button click listener
        Button setNotificationButton = findViewById(R.id.set_notification_button);
        setNotificationButton.setOnClickListener(v -> showDateTimePickers());
    }

    private void showDateTimePickers() {
        Calendar currentDate = Calendar.getInstance();
        Calendar date = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(TimerActivity.this, (view, year, month, dayOfMonth) -> {
            date.set(year, month, dayOfMonth);

            TimePickerDialog timePickerDialog = new TimePickerDialog(TimerActivity.this, (view1, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                date.set(Calendar.SECOND, 0);

               long time = date.getTimeInMillis();
               scheduleNotification(time);
               // saveScheduledTimeToDatabase(classId, time);

                // Schedule the alarm
             //scheduleNotification(date.getTimeInMillis());
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true);

            timePickerDialog.show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void scheduleNotification(long timeInMillis) {
        Intent intent = new Intent(TimerActivity.this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(TimerActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);

    }
    /*private void saveScheduledTimeToDatabase(long classId, long scheduledTime) {
        // Save the scheduled time to the database, associated with the given classId
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.updateClassWithTime(classId, scheduledTime);
    }*/
}
