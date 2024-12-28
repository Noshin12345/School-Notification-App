package com.example.schoolnotification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudyScheduleDB extends SQLiteOpenHelper {

    public StudyScheduleDB(Context context) {
        super(context, "StudyScheduleDB.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DB @ OnCreate");
        String sql = "CREATE TABLE StudySchedule("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "date TEXT,"
                + "courseName TEXT,"
                + "startTime TEXT,"
                + "endTime TEXT,"
                + "additionalInfo TEXT,"
                + "alarm TEXT"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Write code to modify database schema here
        // db.execSQL("ALTER table my_table  ......");
        // db.execSQL("CREATE TABLE  ......");
    }

    public void insertStudySchedule(String date, String courseName, String startTime, String endTime, String additionalInfo, String alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date", date);
        cv.put("courseName", courseName);
        cv.put("startTime", startTime);
        cv.put("endTime", endTime);
        cv.put("additionalInfo", additionalInfo);
        cv.put("alarm", alarm);
        db.insert("StudySchedule", null, cv);
        db.close();
    }

    public void updateStudySchedule(int id, String date, String courseName, String startTime, String endTime, String additionalInfo, String alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date", date);
        cv.put("courseName", courseName);
        cv.put("startTime", startTime);
        cv.put("endTime", endTime);
        cv.put("additionalInfo", additionalInfo);
        cv.put("alarm", alarm);
        db.update("StudySchedule", cv, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteStudySchedule(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("StudySchedule", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public Cursor selectStudySchedule(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try {
            res = db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}

