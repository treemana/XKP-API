/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.model;

/**
 * Created by zhengyi on 17-7-21.
 */
public class Score {

    // 学生id
    private Integer studentId;

    // 课程id
    private Integer courseId;

    // 考试分数
    private Float examination;

    // 考察成绩
    private String inspection;

    // true 考试, false 考察
    private boolean type;

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Float getExamination() {
        return examination;
    }

    public void setExamination(Float examination) {
        this.examination = examination;
    }

    public String getInspection() {
        return inspection;
    }

    public void setInspection(String inspection) {
        this.inspection = inspection;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }
}
