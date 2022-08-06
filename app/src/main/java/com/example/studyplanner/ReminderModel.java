package com.example.studyplanner;

public class ReminderModel {
    private String CourseName;
    private String EventType;
    private String StartTime;
    private String EndTime;

    public ReminderModel() {
    }

    public ReminderModel(String courseName, String eventType, String startTime, String endTime) {
        CourseName = courseName;
        EventType = eventType;
        StartTime = startTime;
        EndTime = endTime;
    }

    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public String getEventType() {
        return EventType;
    }

    public void setEventType(String eventType) {
        EventType = eventType;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }
}
