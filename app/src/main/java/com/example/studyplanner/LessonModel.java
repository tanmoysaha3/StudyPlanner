package com.example.studyplanner;

public class LessonModel {
    String CourseCode;
    String Date;
    String Topics;

    public LessonModel() {
    }

    public LessonModel(String courseCode, String date, String topics) {
        CourseCode = courseCode;
        Date = date;
        Topics = topics;
    }

    public String getCourseCode() {
        return CourseCode;
    }

    public void setCourseCode(String courseCode) {
        CourseCode = courseCode;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTopics() {
        return Topics;
    }

    public void setTopics(String topics) {
        Topics = topics;
    }
}
