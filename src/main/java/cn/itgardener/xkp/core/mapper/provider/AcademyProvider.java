/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.mapper.provider;

import org.apache.ibatis.jdbc.SQL;

/**
 * Created by zhengyi on 17-7-25.
 */
public class AcademyProvider {
    public String getAllAcademy() {
        return new SQL() {
            {
                SELECT("system_id AS systemId,name");
                FROM("xkp_academy");
                ORDER_BY("name");
            }
        }.toString();
    }
}
