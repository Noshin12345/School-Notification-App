package com.example.schoolnotification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.schoolnotification.DatabaseHelper;


import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int Version = 2;
    private static final String CLASS_TABLE_NAME = "CLASS_TABLE";
    public static final String C_ID = "_CID";
    public static final String CLASS_NAME_KEY = "CLASS_NAME";
    public static final String SUBJECT_NAME_KEY = "SUBJECT_NAME";
    public static final String SCHEDULED_TIME_KEY = "SCHEDULED_TIME";// Corrected key

    private static final String CREATE_CLASS_TABLE =
            "CREATE TABLE " + CLASS_TABLE_NAME + " (" +
                    C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    CLASS_NAME_KEY + " TEXT NOT NULL, " +
                    SUBJECT_NAME_KEY + " TEXT NOT NULL, " +
                    SCHEDULED_TIME_KEY + " INTEGER, " +
                    "UNIQUE(" + CLASS_NAME_KEY + ", " + SUBJECT_NAME_KEY + ")" +
                    ");";

    private static final String DROP_CLASS_TABLE = "DROP TABLE IF EXISTS " + CLASS_TABLE_NAME;
    private static final String SELECT_CLASS_TABLE = "SELECT * FROM " + CLASS_TABLE_NAME;


    private static final String STUDENT_TABLE_NAME = "STUDENT_TABLE";

    public static final String S_ID = "_SID";
    public static final String STUDENT_NAME_KEY = "Student_NAME";
    public static final String STUDENT_ROLL_KEY = "ROLL";  // Corrected key


    private static final String CREATE_STUDENT_TABLE =
            "CREATE TABLE " + STUDENT_TABLE_NAME + " (" +
                    S_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    C_ID + " INTEGER NOT NULL, " +
                    STUDENT_NAME_KEY + " TEXT NOT NULL, " +
                    STUDENT_ROLL_KEY + " INTEGER, " +
                    "FOREIGN KEY(" + C_ID + ") REFERENCES " + CLASS_TABLE_NAME + "(" + C_ID + ")" +
                    ");";



    private static final String DROP_STUDENT_TABLE = "DROP TABLE IF EXISTS " + STUDENT_TABLE_NAME;
    private static final String SELECT_STUDENT_TABLE = "SELECT * FROM " + STUDENT_TABLE_NAME;



    private static final String STATUS_TABLE_NAME = "STATUS_TABLE";

    public static final String STATUS_ID = "_STATUS_ID";
    public static final String DATE_KEY = "STATUS_DATE";
    public static final String STATUS_KEY = "STATUS";  // Corrected key


    private static final String CREATE_STATUS_TABLE =
            "CREATE TABLE " + STATUS_TABLE_NAME +
                    " (" +
                    STATUS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    S_ID + " INTEGER NOT NULL, " +
                    STATUS_KEY + " TEXT NOT NULL, " +
                    "UNIQUE ("+ S_ID + "),"+
                    "FOREIGN KEY(" + S_ID + ") REFERENCES " + STUDENT_TABLE_NAME + "(" + S_ID + ")" +
                    ");";



    private static final String DROP_STATUS_TABLE = "DROP TABLE IF EXISTS " + STATUS_TABLE_NAME;
    private static final String SELECT_STATUS_TABLE = "SELECT * FROM " + STATUS_TABLE_NAME;



    public DatabaseHelper(@Nullable Context context) {
        super(context, "Attendence.db", null, Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CLASS_TABLE);
        db.execSQL(CREATE_STUDENT_TABLE);
        db.execSQL(CREATE_STATUS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_CLASS_TABLE);
            db.execSQL(DROP_STUDENT_TABLE);
            db.execSQL(DROP_STATUS_TABLE);
            onCreate(db);  // Recreate table after dropping it
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    long addClass(String className, String subjectName){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLASS_NAME_KEY, className);
        values.put(SUBJECT_NAME_KEY, subjectName);

        return database.insert(CLASS_TABLE_NAME, null, values);


    }

    Cursor getClassTable(){
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery(SELECT_CLASS_TABLE, null);
    }

    int deleteClass(long cid){
        SQLiteDatabase database = this.getReadableDatabase();
       return database.delete(CLASS_TABLE_NAME, C_ID+"=?", new String[]{String.valueOf(cid)});
    }

    long addStudent(long classId, String studentName, int studentRoll) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(C_ID, classId);
        values.put(STUDENT_NAME_KEY, studentName);
        values.put(STUDENT_ROLL_KEY, studentRoll);

        return database.insert(STUDENT_TABLE_NAME, null, values);
    }



    Cursor getStudentTable(long cid) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.query(
                STUDENT_TABLE_NAME,
                null,
                C_ID + "=?",
                new String[]{String.valueOf(cid)},
                null,
                null,
                null
        );

    }

    int deleteStudent(long sid){
        SQLiteDatabase database = this.getReadableDatabase();
        return database.delete(STUDENT_TABLE_NAME, S_ID+"=?", new String[]{String.valueOf(sid)});
    }
    public void updateClassWithTime(long cid, long scheduledTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCHEDULED_TIME_KEY, scheduledTime);
        db.update(CLASS_TABLE_NAME, values, C_ID + " = ?", new String[]{String.valueOf(cid)});
        db.close();
    }

    long addStatus(long sid, String status){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(S_ID,sid);
        values.put(STATUS_KEY, status);
        return database.insert(STATUS_TABLE_NAME,null,values);

    }
    long updateStatus(long sid, String status){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS_KEY, status);
        //String whereClause = S_ID+'='+sid;
        String whereClause = S_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(sid)};
        return database.update(STATUS_TABLE_NAME,values,whereClause,whereArgs);

    }
   /* String getStatus(long sid){
        String status= null;
        SQLiteDatabase database = this.getReadableDatabase();
        String whereClause = S_ID+'='+sid;
        Cursor cursor= database.query(STATUS_TABLE_NAME,null,whereClause,null,null,null,null);

        try {
            cursor = database.query(STATUS_TABLE_NAME, null, whereClause, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int statusIndex = cursor.getColumnIndex(STATUS_KEY);
                if (statusIndex != -1) {
                    status = cursor.getString(statusIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return status;
    }*/
    String getStatus(long sid){
        String status = null;
        SQLiteDatabase database = this.getReadableDatabase();
        String[] projection = {STATUS_KEY};
        String selection = S_ID + "=?";
        String[] selectionArgs = {String.valueOf(sid)};
        try (Cursor cursor = database.query(STATUS_TABLE_NAME, projection, selection, selectionArgs, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int statusIndex = cursor.getColumnIndex(STATUS_KEY);
                if (statusIndex != -1) {
                    status = cursor.getString(statusIndex);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }




}