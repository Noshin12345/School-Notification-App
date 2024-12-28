package com.example.schoolnotification;

public class Routine {
    String uid;
    String weekday;
    String subject;
    int startHour;
    int startMin;
    int endHour;
    int endMin;
    String additionalInfo;
    String notificationAlarm;

    public Routine(String uid, String weekday, String subject, int startHour, int startMin, int endHour, int endMin, String additionalInfo, String notificationAlarm) {
        this.uid = uid;
        this.weekday = weekday;
        this.subject = subject;
        this.startHour = startHour;
        this.startMin = startMin;
        this.endHour = endHour;
        this.endMin = endMin;
        this.additionalInfo = additionalInfo;
        this.notificationAlarm = notificationAlarm;
    }

}

