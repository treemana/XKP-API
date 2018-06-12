/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.model;

/**
 * Created by zhengyi on 17-7-18.
 * <p>
 * Described by You-Bian on 17-10-27
 * <p>
 * 该model对应数据库中的xkp_academy表
 * 是学院信息的表
 */
public class Academy {

    // 学院ID
    private int systemId;

    // 学院名称
    private String name;

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
