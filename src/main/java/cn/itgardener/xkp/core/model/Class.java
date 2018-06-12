/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.model;

/**
 * Created by zhengyi on 17-7-21.
 * <p>
 * Described by You-Bian on 17-10-27
 * <p>
 * 该model对应数据库中的xkp_class表
 */
public class Class {

    private Integer systemId;

    // 专业 id
    private Integer specialtyId;

    // 年级
    private String grade;

    // 班级名称
    private String name;

    public Integer getSystemId() {
        return systemId;
    }

    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public Integer getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(Integer specialtyId) {
        this.specialtyId = specialtyId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
