package com.example.schoolnotification;
public class ExaminationList {
    String uid="",  date="", courseName="", starttime="", endtime="", additional="", alarmtime="";
    public ExaminationList  (String uid, String date, String courseName, String starttime,String endtime, String additional, String alarmtime){
        this.uid = uid;
        this.date= date;
        this.courseName=courseName;
        this.starttime=starttime;
        this.endtime=endtime;
        this.additional=additional;
        this.alarmtime=alarmtime;
    }
}
