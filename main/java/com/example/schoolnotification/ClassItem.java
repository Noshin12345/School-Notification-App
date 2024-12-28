package com.example.schoolnotification;
public class ClassItem {
    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }
    private long scheduledTime;
    public ClassItem(long cid, String className, String subjectName, long scheduledTime){
        this.cid = cid;
        this.className = className;
        this.subjectName = subjectName;
        this.scheduledTime = scheduledTime;
    }



    private long cid;
  private  String className;
  private  String subjectName;

    public String getSubjectName() {
        return subjectName;
    }

    public String getClassName() {
        return className;
    }
    public long getScheduledTime() {
        return scheduledTime;
    }
    public void setScheduledTime(long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ClassItem(String className, String subjectName){
        this.className = className;
        this.subjectName = subjectName;

    }




}
