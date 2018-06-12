/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.model;

/**
 * Created by zhengyi on 17-7-21.
 */
public class Specialty {

    private int systemId;

    // 学院 id
    private int academyId;

    // 专业名称
    private String name;

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public int getAcademyId() {
        return academyId;
    }

    public void setAcademyId(int academyId) {
        this.academyId = academyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
