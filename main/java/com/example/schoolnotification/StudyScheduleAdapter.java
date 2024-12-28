package com.example.schoolnotification;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class StudyScheduleAdapter extends ArrayAdapter<StudySchedule> {
    private final Context context;
    private final ArrayList<StudySchedule> schedules;

    public StudyScheduleAdapter(@NonNull Context context, @NonNull ArrayList<StudySchedule> items) {
        super(context, -1, items);
        this.context = context;
        this.schedules = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.activity_row_study_time_item, parent, false);

        TextView dateTextView = rowView.findViewById(R.id.tvDate);
        TextView courseNameTextView = rowView.findViewById(R.id.tvCourseName);
        TextView startTimeTextView = rowView.findViewById(R.id.tvStartTime);
        TextView endTimeTextView = rowView.findViewById(R.id.tvEndTime);
        TextView additionalInfoTextView = rowView.findViewById(R.id.tvAdditionalInfo);
        TextView alarmTextView = rowView.findViewById(R.id.tvAlarm);
        Button buttonDelete = rowView.findViewById(R.id.buttonDelete);

        StudySchedule schedule = schedules.get(position);

        dateTextView.setText(schedule.getDate());
        courseNameTextView.setText(schedule.getCourseName());
        startTimeTextView.setText(schedule.getStartTime());
        endTimeTextView.setText(schedule.getEndTime());
        additionalInfoTextView.setText(schedule.getAdditionalInfo());
        alarmTextView.setText(schedule.getAlarm());
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(position);
            }
        });

        return rowView;
    }
    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you want to delete this item?");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StudySchedule ss = schedules.get(position);
                StudyScheduleDB db = new StudyScheduleDB(context);
                db.deleteStudySchedule(ss.getId());
                Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show();
                db.close();


                schedules.remove(position);
                notifyDataSetChanged();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}

