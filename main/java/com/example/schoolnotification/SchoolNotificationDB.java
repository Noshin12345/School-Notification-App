package com.example.schoolnotification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SchoolNotificationDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "schoolnotification.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "routine";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_WEEKDAY = "weekday";
    private static final String COLUMN_SUBJECT = "subject";
    private static final String COLUMN_START_HOUR = "start_hour";
    private static final String COLUMN_START_MIN = "start_min";
    private static final String COLUMN_END_HOUR = "end_hour";
    private static final String COLUMN_END_MIN = "end_min";
    private static final String COLUMN_ADDITIONAL_INFO = "additional_info";
    private static final String COLUMN_NOTIFICATION_ALARM = "notification_alarm";

    public SchoolNotificationDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ROUTINE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " TEXT PRIMARY KEY,"
                + COLUMN_WEEKDAY + " TEXT,"
                + COLUMN_SUBJECT + " TEXT,"
                + COLUMN_START_HOUR + " INTEGER,"
                + COLUMN_START_MIN + " INTEGER,"
                + COLUMN_END_HOUR + " INTEGER,"
                + COLUMN_END_MIN + " INTEGER,"
                + COLUMN_ADDITIONAL_INFO + " TEXT,"
                + COLUMN_NOTIFICATION_ALARM + " TEXT" + ")";
        db.execSQL(CREATE_ROUTINE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertRoutine(String id, String weekday, String subject, int startHour, int startMin, int endHour, int endMin, String additionalInfo, String notificationAlarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_WEEKDAY, weekday);
        values.put(COLUMN_SUBJECT, subject);
        values.put(COLUMN_START_HOUR, startHour);
        values.put(COLUMN_START_MIN, startMin);
        values.put(COLUMN_END_HOUR, endHour);
        values.put(COLUMN_END_MIN, endMin);
        values.put(COLUMN_ADDITIONAL_INFO, additionalInfo);
        values.put(COLUMN_NOTIFICATION_ALARM, notificationAlarm);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void updateRoutine(String id, String weekday, String subject, int startHour, int startMin, int endHour, int endMin, String additionalInfo, String notificationAlarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WEEKDAY, weekday);
        values.put(COLUMN_SUBJECT, subject);
        values.put(COLUMN_START_HOUR, startHour);
        values.put(COLUMN_START_MIN, startMin);
        values.put(COLUMN_END_HOUR, endHour);
        values.put(COLUMN_END_MIN, endMin);
        values.put(COLUMN_ADDITIONAL_INFO, additionalInfo);
        values.put(COLUMN_NOTIFICATION_ALARM, notificationAlarm);

        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{id});
        db.close();
    }

    public void deleteRoutine(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{id});
        db.close();
    }

    public Cursor selectClassRoutineList(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        try{
            res = db.rawQuery(query, null);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}

