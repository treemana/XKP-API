/*
 * Copyright (c) 2014-2019 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.mapper.provider;

import cn.itgardener.xkp.core.model.Course;
import org.apache.ibatis.jdbc.SQL;

/**
 * Created by Hunter-Yi on 17-8-30 下午10:26
 */
public class CourseProvider {
    public String selectByCondition(Course course) {
        return new SQL() {
            {
                SELECT("system_id AS systemId,class_id AS classId,credit,name,type");
                FROM("xkp_course");
                WHERE();
                if (null != course.getClassId()) {
                    WHERE("class_id=#{classId}");
                }
                ORDER_BY("credit desc");
            }
        }.toString();
    }
}
