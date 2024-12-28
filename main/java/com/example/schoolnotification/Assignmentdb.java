package com.example.schoolnotification;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class Assignmentdb extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AssignmentDatabase.db";
    private static final int DATABASE_VERSION = 1;
    public Assignmentdb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE assignments ("
                + "uid INTEGER ,"
                + "date TEXT,"
                + "subject TEXT,"
                + "deadline_time TEXT,"
                + "additional_info TEXT,"
                + "notification_alarm TEXT"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS assignments");
            onCreate(db);
        }
    }

    public void insertAssignment(String uid,String date, String subject, String deadlineTime, String additionalInfo, String notificationAlarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uid", uid);
        values.put("date", date);
        values.put("subject", subject);
        values.put("deadline_time", deadlineTime);
        values.put("additional_info", additionalInfo);
        values.put("notification_alarm", notificationAlarm);
        db.insert("assignments", null, values);
        db.close();
    }

    public void updateAssignment(String uid,String date, String subject, String deadlineTime, String additionalInfo, String notificationAlarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uid", uid);
        values.put("date", date);
        values.put("subject", subject);
        values.put("deadline_time", deadlineTime);
        values.put("additional_info", additionalInfo);
        values.put("notification_alarm", notificationAlarm);
        db.update("assignments", values, "uid=?", new String[]{String.valueOf(uid)});
    }

    public void deleteAssignment(String uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("assignments", "uid=?", new String[]{String.valueOf(uid)});
    }

    public Cursor selectAssignments(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }
}



