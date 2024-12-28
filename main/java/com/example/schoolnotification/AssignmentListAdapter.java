package com.example.schoolnotification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class AssignmentListAdapter extends ArrayAdapter<AssignmenLIST> {

    private final Context context;
    private final ArrayList<AssignmenLIST> values;

    public AssignmentListAdapter(@NonNull Context context, @NonNull ArrayList<AssignmenLIST> items) {
        super(context, -1, items);
        this.context = context;
        this.values = items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inf.inflate(R.layout.activity_assignment_list_main, parent, false);

        TextView courseName = rowView.findViewById(R.id.tvCourseName);
        TextView lectureDate = rowView.findViewById(R.id.tvLectureDate);
        TextView time = rowView.findViewById(R.id.Time);


        AssignmenLIST c = values.get(position);
        courseName.setText(c.courseName);
        lectureDate.setText(c.date);
        time.setText(c.deadline);

      /*  btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                values.remove(position);
                notifyDataSetChanged();
                // Optionally, delete the item from the database here
            }
        });

       */

        return rowView;
    }
}
