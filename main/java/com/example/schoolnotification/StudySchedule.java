package com.example.schoolnotification;

public class StudySchedule {
    private int id;
    private String date;
    private String courseName;
    private String startTime;
    private String endTime;
    private String additionalInfo;
    private String alarm;

    // Constructor, getters, and setters

    public StudySchedule(int id, String date, String courseName, String startTime, String endTime, String additionalInfo, String alarm) {
        this.id = id;
        this.date = date;
        this.courseName = courseName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.additionalInfo = additionalInfo;
        this.alarm = alarm;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public String getAlarm() {
        return alarm;
    }
}

