/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.model.vo;

import cn.itgardener.xkp.core.model.Score;

import java.util.List;

/**
 * Created by Hunter-Yi on 17-9-12 上午11:04
 */
public class ScoreVo {

    private int studentId;

    private List<Score> marks;

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public List<Score> getMarks() {
        return marks;
    }

    public void setMarks(List<Score> marks) {
        this.marks = marks;
    }
}
