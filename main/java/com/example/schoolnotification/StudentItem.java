package com.example.schoolnotification;

public class StudentItem {
    public long getSid() {
        return sid;
    }

    private long sid;
    private int roll;
    private String name;
    private String  status;

    public StudentItem(long sid, int roll, String name) {
        this.sid = sid;
        this.roll = roll;
        this.name = name;
        status ="";
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public int getRoll() {
        return roll;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }
}
