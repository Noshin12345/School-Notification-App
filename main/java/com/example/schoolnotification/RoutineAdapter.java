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

public class RoutineAdapter extends ArrayAdapter<Routine> {
    private final Context context;
    private final ArrayList<Routine> values;
    private LayoutInflater inflater;

    public RoutineAdapter(@NonNull Context context, @NonNull ArrayList<Routine> items) {
        super(context, -1, items);
        this.context = context;
        this.values = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.list_item_routine, parent, false);
        TextView tvWeekday = rowView.findViewById(R.id.tvWeekday);
        TextView tvSubject = rowView.findViewById(R.id.tvSubject);
        TextView tvTime = rowView.findViewById(R.id.tvTime);
        Button buttonDelete = rowView.findViewById(R.id.buttonDelete);

        Routine routine = values.get(position);
        tvWeekday.setText(routine.weekday);
        tvSubject.setText(routine.subject);
        String time = String.format("%02d:%02d - %02d:%02d", routine.startHour, routine.startMin, routine.endHour, routine.endMin);
        tvTime.setText(time);

        // Set the click listener for the delete button
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
                Routine routine = values.get(position);
                SchoolNotificationDB db = new SchoolNotificationDB(context);
                db.deleteRoutine(routine.uid);
                Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show();
                db.close();
                values.remove(position);
                notifyDataSetChanged();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
