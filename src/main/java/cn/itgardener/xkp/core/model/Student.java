/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.model;

/**
 * Created by zhengyi on 17-7-21.
 */
public class Student {

    private Integer systemId;

    // 班级 id
    private Integer classId;

    // 学号
    private String studentNumber;

    // 姓名
    private String name;

    // 职务说明
    private String dutyDesc;

    // 学术说明
    private String academicDesc;

    // 操行平等
    private String behavior = "良";

    // 德育
    private Float moral;

    // 文体
    private Float activity;

    // 职务
    private Float duty;

    // 学术
    private Float academic;

    public Integer getSystemId() {
        return systemId;
    }

    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDutyDesc() {
        return dutyDesc;
    }

    public void setDutyDesc(String dutyDesc) {
        this.dutyDesc = dutyDesc;
    }

    public String getAcademicDesc() {
        return academicDesc;
    }

    public void setAcademicDesc(String academicDesc) {
        this.academicDesc = academicDesc;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public Float getMoral() {
        return moral;
    }

    public void setMoral(Float moral) {
        this.moral = moral;
    }

    public Float getActivity() {
        return activity;
    }

    public void setActivity(Float activity) {
        this.activity = activity;
    }

    public Float getDuty() {
        return duty;
    }

    public void setDuty(Float duty) {
        this.duty = duty;
    }

    public Float getAcademic() {
        return academic;
    }

    public void setAcademic(Float academic) {
        this.academic = academic;
    }
}
