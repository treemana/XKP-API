/*
 * Copyright (c) 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.model;

/**
 * @author : Hunter
 * @date : 2018-07-11 15:30
 * @since : Java 8
 */
public class History {

    // 主键
    private Integer systemId;

    // 学院 id
    private Integer academyId;

    // 学院名称
    private String academyName;

    // 专业 id
    private Integer specialtyId;

    // 专业名称
    private String specialtyName;

    // 班级 id
    private Integer classId;

    // 班级名称
    private String className;

    // 年级
    private String grade;

    // 标题名称
    private String titleDate;

    // 课程信息
    private String courses;

    // 大表数据
    private String benchmarkData;

    public Integer getSystemId() {
        return systemId;
    }

    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public Integer getAcademyId() {
        return academyId;
    }

    public void setAcademyId(Integer academyId) {
        this.academyId = academyId;
    }

    public String getAcademyName() {
        return academyName;
    }

    public void setAcademyName(String academyName) {
        this.academyName = academyName;
    }

    public Integer getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(Integer specialtyId) {
        this.specialtyId = specialtyId;
    }

    public String getSpecialtyName() {
        return specialtyName;
    }

    public void setSpecialtyName(String specialtyName) {
        this.specialtyName = specialtyName;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTitleDate() {
        return titleDate;
    }

    public void setTitleDate(String titleDate) {
        this.titleDate = titleDate;
    }

    public String getCourses() {
        return courses;
    }

    public void setCourses(String courses) {
        this.courses = courses;
    }

    public String getBenchmarkData() {
        return benchmarkData;
    }

    public void setBenchmarkData(String benchmarkData) {
        this.benchmarkData = benchmarkData;
    }

    @Override
    public String toString() {
        return "History{" +
                "systemId=" + systemId +
                ", academyId=" + academyId +
                ", academyName='" + academyName + '\'' +
                ", specialtyId=" + specialtyId +
                ", specialtyName='" + specialtyName + '\'' +
                ", classId=" + classId +
                ", className='" + className + '\'' +
                ", grade='" + grade + '\'' +
                ", titleDate='" + titleDate + '\'' +
                ", courses='" + courses + '\'' +
                ", benchmarkData='" + benchmarkData + '\'' +
                '}';
    }
}
