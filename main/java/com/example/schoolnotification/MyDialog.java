package com.example.schoolnotification;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MyDialog extends DialogFragment {
    public static final String CLASS_ADD_DIALOG="addClass";
    public static final String STUDENT_ADD_DIALOG="addStudent";
   private OnClickListener listener;
   public interface OnClickListener{
       void onClick(String text1, String text2);
   }
   public void setListener( OnClickListener listener){
       this.listener = listener;
   }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog=null;
        if(getTag().equals(CLASS_ADD_DIALOG))dialog= getAddClassDialog();
        if(getTag().equals(STUDENT_ADD_DIALOG))dialog= getAddStudentDialog();
        return dialog;
    }

    private Dialog getAddStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);
        builder.setView(view);
        TextView title = view.findViewById(R.id.titleDialog) ;
        title.setText("Add New Student");

        EditText roll_ed = view.findViewById(R.id.edt01);
        EditText  name_ed = view.findViewById(R.id.edt02);

        roll_ed.setHint("Student Roll");
        name_ed.setHint("Student Name");

        Button cancel = view.findViewById(R.id.cancel_bu);
        Button add = view.findViewById(R.id.add_bu);

        cancel.setOnClickListener(v -> dismiss()); // Dismiss the dialog
        add.setOnClickListener(v -> {

           //String roll = roll_ed.getText().toString();
            String name = name_ed.getText().toString();
            //roll_ed.setText(String.valueOf(Integer.parseInt(roll)+1));
            String roll = roll_ed.getText().toString();

            if (!TextUtils.isEmpty(roll) && TextUtils.isDigitsOnly(roll)) {
                int rollValue = Integer.parseInt(roll);
                roll_ed.setText(String.valueOf(rollValue + 1));
                // Rest of your code
            } else {
                // Handle the case where roll is not a valid integer
                // For example, show an error message or log a warning
            }
            roll_ed.setText("");
            name_ed.setText("");
            listener.onClick(roll,name);


        });

        return builder.create();


    }

    private Dialog getAddClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);
        builder.setView(view);
        TextView title = view.findViewById(R.id.titleDialog) ;
        title.setText("Add New Class");

     EditText class_ed = view.findViewById(R.id.edt01);
     EditText  sub_ed = view.findViewById(R.id.edt02);

     class_ed.setHint("Class name");
     sub_ed.setHint("Subject Name");

        Button cancel = view.findViewById(R.id.cancel_bu);
        Button add = view.findViewById(R.id.add_bu);

        cancel.setOnClickListener(v -> dismiss()); // Dismiss the dialog
        add.setOnClickListener(v -> {

            String className = class_ed.getText().toString();
            String subName = sub_ed.getText().toString();
            listener.onClick(className, subName);

            dismiss();
        });

        return builder.create();


}
}
