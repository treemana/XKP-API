/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.model;

/**
 * Created by Hunter-Yi on 17-9-16 下午1:36
 */
public class Sort implements Comparable<Sort> {

    private String studentNumber;

    private float number;

    private int rank;

    @Override
    public int compareTo(Sort o) {
        int rtv = 0;
        if (this.number > o.getNumber()) {
            rtv = -1;
        } else if (this.number < o.getNumber()) {
            rtv = 1;
        }
        return rtv;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public float getNumber() {
        return number;
    }

    public void setNumber(float number) {
        this.number = number;
    }

}
