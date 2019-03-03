/*
 * Copyright (c) 2014-2019 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.mapper.provider;

import cn.itgardener.xkp.core.model.Score;
import org.apache.ibatis.jdbc.SQL;

/**
 * Created by Hunter-Yi on 17-9-12 上午10:30
 */
public class ScoreProvider {

    public String selectByCondition(Score score) {
        return new SQL() {
            {
                SELECT("student_id AS studentId,course_id AS courseId,examination,inspection,type");
                FROM("xkp_score");
                WHERE();
                if (null != score.getStudentId()) {
                    WHERE("student_id=#{studentId}");
                }
                if (null != score.getCourseId()) {
                    WHERE("course_id=#{courseId}");
                }
            }
        }.toString();
    }

    public String updateByCondition(Score score) {
        return new SQL() {
            {
                UPDATE("xkp_score");
                SET("examination=#{examination}");
                SET("inspection=#{inspection}");
                SET("type=#{type}");
                WHERE("student_id=#{studentId}");
                WHERE("course_id=#{courseId}");
            }
        }.toString();
    }
}
