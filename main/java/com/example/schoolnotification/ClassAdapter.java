package com.example.schoolnotification;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    ArrayList<ClassItem> classItems;
    Context context;
    private OnItemClickListener onItemClickListener;


    public interface OnItemClickListener{
        void onClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public ClassAdapter(Context context, ArrayList<ClassItem> classItems) {
        this.classItems = classItems;
        this.context = context;

    }


    public static class ClassViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView className;
        TextView subjectName;
        TextView scheduledTime; // New TextView for scheduled time

        public ClassViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener){
            super(itemView);
            className = itemView.findViewById(R.id.class_tv);
            subjectName = itemView.findViewById(R.id.subject_tv);
            scheduledTime = itemView.findViewById(R.id.scheduled_time_tv);
            itemView.setOnClickListener(v -> onItemClickListener.onClick(getAdapterPosition()));
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(getAdapterPosition(),1,0,"DELETE");
        }
    }

  @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType ){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item,parent, false);
        return new ClassViewHolder(itemView, onItemClickListener);
  }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {

        holder.className.setText(classItems.get(position).getClassName());
        holder.subjectName.setText(classItems.get(position).getSubjectName());
        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        //String formattedTime = sdf.format(new Date(classItems.get(position).getScheduledTime()));
        //holder.scheduledTime.setText(formattedTime);


    }

    @Override
    public int getItemCount() {
        return classItems.size();
    }
}
