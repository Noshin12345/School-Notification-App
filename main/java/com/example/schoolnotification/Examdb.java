package com.example.schoolnotification;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class Examdb extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExamDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public Examdb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE exams ("
                + "uid INTEGER ,"
                + "date TEXT,"
                + "subject TEXT,"
                + "start_time TEXT,"
                + "end_time TEXT,"
                + "additional_info TEXT,"
                + "notification_alarm TEXT"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS exams");
            onCreate(db);
        }
    }

    public void insertExam(String uid, String date, String subject, String startTime, String endTime, String additionalInfo, String notificationAlarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uid", uid);
        values.put("date", date);
        values.put("subject", subject);
        values.put("start_time", startTime);
        values.put("end_time", endTime);
        values.put("additional_info", additionalInfo);
        values.put("notification_alarm", notificationAlarm);
        db.insert("exams", null, values);
    }

    public void updateExam(String uid, String date, String subject, String startTime, String endTime, String additionalInfo, String notificationAlarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uid", uid);
        values.put("date", date);
        values.put("subject", subject);
        values.put("start_time", startTime);
        values.put("end_time", endTime);
        values.put("additional_info", additionalInfo);
        values.put("notification_alarm", notificationAlarm);
        db.update("exams", values, "uid=?", new String[]{String.valueOf(uid)});
    }

    public void deleteExam(String uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("exams", "uid=?", new String[]{uid});
        db.close();
    }


    public Cursor selectExams(String query) {
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
