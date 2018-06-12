/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.model;

import java.util.List;

/**
 * Created by Hunter-Yi on 17-9-13 上午11:48
 */
public class Benchmark extends Student {

    // 课程分数
    private List<Score> marks;

    // 平均绩点
    private float point;

    // 其他
    private float other;

    // 智育
    private float score;

    // 总分
    private float total;

    // 综合排名
    private int complexRank;

    // 智育排名
    private int scoreRank;

    public List<Score> getMarks() {
        return marks;
    }

    public void setMarks(List<Score> marks) {
        this.marks = marks;
    }

    public float getPoint() {
        return point;
    }

    public void setPoint(float point) {
        this.point = point;
    }

    public float getOther() {
        return other;
    }

    public void setOther(float other) {
        this.other = other;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public int getComplexRank() {
        return complexRank;
    }

    public void setComplexRank(int complexRank) {
        this.complexRank = complexRank;
    }

    public int getScoreRank() {
        return scoreRank;
    }

    public void setScoreRank(int scoreRank) {
        this.scoreRank = scoreRank;
    }
}
