package com.example.schoolnotification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ClassRoutine extends AppCompatActivity implements View.OnClickListener {

    private Button btnSaturday, btnSunday, btnMonday, btnTuesday, btnWednesday, btnThursday, btnFriday;
    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_routine);

        btnSaturday = findViewById(R.id.btnSaturDay);
        btnSunday = findViewById(R.id.btnSunday);
        btnMonday = findViewById(R.id.btnMonday);
        btnTuesday = findViewById(R.id.btnTuesday);
        btnWednesday = findViewById(R.id.btnWednesday);
        btnThursday = findViewById(R.id.btnThursday);
        btnFriday = findViewById(R.id.btnFriday);
        btnExit = findViewById(R.id.buttonExit);

        btnSaturday.setOnClickListener(this);
        btnSunday.setOnClickListener(this);
        btnMonday.setOnClickListener(this);
        btnTuesday.setOnClickListener(this);
        btnWednesday.setOnClickListener(this);
        btnThursday.setOnClickListener(this);
        btnFriday.setOnClickListener(this);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSaturDay)
        {
            Intent i = new Intent(ClassRoutine.this,ClassRoutineList.class);
            i.putExtra("WEEKDAY", "Saturday");
            startActivity(i);
            finish();

        } else if (v.getId() == R.id.btnSunday)
        {
            Intent i = new Intent(ClassRoutine.this,ClassRoutineList.class);
            i.putExtra("WEEKDAY", "Sunday");
            startActivity(i);
            finish();

        } else if (v.getId() == R.id.btnMonday)
        {
            Intent i = new Intent(ClassRoutine.this,ClassRoutineList.class);
            i.putExtra("WEEKDAY", "Monday");
            startActivity(i);
            finish();

        } else if (v.getId() == R.id.btnTuesday)
        {
            Intent i = new Intent(ClassRoutine.this,ClassRoutineList.class);
            i.putExtra("WEEKDAY", "Tuesday");
            startActivity(i);
            finish();

        } else if (v.getId() == R.id.btnWednesday)
        {
            Intent i = new Intent(ClassRoutine.this,ClassRoutineList.class);
            i.putExtra("WEEKDAY", "Wednesday");
            startActivity(i);
            finish();

        } else if (v.getId() == R.id.btnThursday)
        {
            Intent i = new Intent(ClassRoutine.this,ClassRoutineList.class);
            i.putExtra("WEEKDAY", "Thursday");
            startActivity(i);
            finish();

        } else if (v.getId() == R.id.btnFriday)
        {
            Intent i = new Intent(ClassRoutine.this,ClassRoutineList.class);
            i.putExtra("WEEKDAY", "Friday");
            startActivity(i);
            finish();

        }
    }
}
