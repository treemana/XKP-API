/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.model.vo;

import cn.itgardener.xkp.core.model.Manager;

import java.util.List;

/**
 * Created by zhengyi on 17-7-28.
 */
public class ManagerVo extends Manager {
    private String oldPassword;
    private String newPassword;
    private List<Integer> academyIds;
    private List<String> grades;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public List<Integer> getAcademyIds() {
        return academyIds;
    }

    public void setAcademyIds(List<Integer> academyIds) {
        this.academyIds = academyIds;
    }

    public List<String> getGrades() {
        return grades;
    }

    public void setGrades(List<String> grades) {
        this.grades = grades;
    }
}
