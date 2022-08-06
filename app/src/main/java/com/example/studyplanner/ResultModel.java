package com.example.studyplanner;

public class ResultModel {
    String CourseCode;
    String CT1Mark;
    String CT1Date;
    String CT2Mark;
    String CT2Date;
    String CT3Mark;
    String CT3Date;
    String AssignmentMark;
    String AssignmentDate;
    String FinalMark;
    String FinalDate;

    public ResultModel() {
    }

    public ResultModel(String courseCode, String CT1Mark, String CT1Date, String CT2Mark, String CT2Date, String CT3Mark, String CT3Date, String assignmentMark, String assignmentDate, String finalMark, String finalDate) {
        CourseCode = courseCode;
        this.CT1Mark = CT1Mark;
        this.CT1Date = CT1Date;
        this.CT2Mark = CT2Mark;
        this.CT2Date = CT2Date;
        this.CT3Mark = CT3Mark;
        this.CT3Date = CT3Date;
        this.AssignmentMark = assignmentMark;
        this.AssignmentDate = assignmentDate;
        this.FinalMark = finalMark;
        this.FinalDate = finalDate;
    }

    public String getCourseCode() {
        return CourseCode;
    }

    public void setCourseCode(String courseCode) {
        CourseCode = courseCode;
    }

    public String getCT1Mark() {
        return CT1Mark;
    }

    public void setCT1Mark(String CT1Mark) {
        this.CT1Mark = CT1Mark;
    }

    public String getCT1Date() {
        return CT1Date;
    }

    public void setCT1Date(String CT1Date) {
        this.CT1Date = CT1Date;
    }

    public String getCT2Mark() {
        return CT2Mark;
    }

    public void setCT2Mark(String CT2Mark) {
        this.CT2Mark = CT2Mark;
    }

    public String getCT2Date() {
        return CT2Date;
    }

    public void setCT2Date(String CT2Date) {
        this.CT2Date = CT2Date;
    }

    public String getCT3Mark() {
        return CT3Mark;
    }

    public void setCT3Mark(String CT3Mark) {
        this.CT3Mark = CT3Mark;
    }

    public String getCT3Date() {
        return CT3Date;
    }

    public void setCT3Date(String CT3Date) {
        this.CT3Date = CT3Date;
    }

    public String getAssignmentMark() {
        return AssignmentMark;
    }

    public void setAssignmentMark(String assignmentMark) {
        AssignmentMark = assignmentMark;
    }

    public String getAssignmentDate() {
        return AssignmentDate;
    }

    public void setAssignmentDate(String assignmentDate) {
        AssignmentDate = assignmentDate;
    }

    public String getFinalMark() {
        return FinalMark;
    }

    public void setFinalMark(String finalMark) {
        FinalMark = finalMark;
    }

    public String getFinalDate() {
        return FinalDate;
    }

    public void setFinalDate(String finalDate) {
        FinalDate = finalDate;
    }
}
