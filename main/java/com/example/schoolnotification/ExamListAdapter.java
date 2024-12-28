package com.example.schoolnotification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ExamListAdapter extends ArrayAdapter<ExaminationList> {

    private final Context context;
    private final ArrayList<ExaminationList> values;

    public ExamListAdapter(@NonNull Context context, @NonNull ArrayList<ExaminationList> items) {
        super(context, -1, items);
        this.context = context;
        this.values = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inf.inflate(R.layout.activity_lecture_list_main, parent, false);

        TextView courseName = rowView.findViewById(R.id.tvCourseName);
        TextView lectureDate = rowView.findViewById(R.id.tvLectureDate);
        TextView time = rowView.findViewById(R.id.Time);


        ExaminationList c = values.get(position);
        courseName.setText(c.courseName);
        lectureDate.setText(c.date);
        time.setText(c.starttime);


        return rowView;
    }
}
