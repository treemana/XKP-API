/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.mapper.provider;

import cn.itgardener.xkp.core.model.Manager;
import cn.itgardener.xkp.core.model.vo.ManagerVo;
import org.apache.ibatis.jdbc.SQL;

/**
 * Created by zhengyi on 17-7-25.
 */
public class ManagerProvider {

    public String insert(Manager manager) {
        return new SQL() {
            {
                INSERT_INTO("xkp_manager");
                VALUES("academy_id", "#{academyId}");
                VALUES("username", "#{username}");
                VALUES("password", "#{password}");
                if (null != manager.getSpecialtyId()) {
                    VALUES("specialty_id", "#{specialtyId}");
                }
                if (null != manager.getClassId()) {
                    VALUES("class_id", "#{classId}");
                }
                if (null != manager.getGrade()) {
                    VALUES("grade", "#{grade}");
                }
                if (null != manager.getType()) {
                    VALUES("type", "#{type}");
                }
            }
        }.toString();
    }

    public String selectByCondition(Manager manager) {
        return new SQL() {
            {
                SELECT("system_id AS systemId,academy_id AS academyId,specialty_id AS specialtyId,class_id AS classId,username,`password`,`type`,grade,status,token");
                FROM("xkp_manager");
                if (null != manager.getAcademyId()) {
                    WHERE("academy_id=#{academyId}");
                }
                if (null != manager.getSpecialtyId()) {
                    WHERE("specialty_id=#{specialtyId}");
                }
                if (null != manager.getClassId()) {
                    WHERE("class_id=#{classId}");
                }
                if (null != manager.getGrade()) {
                    WHERE("grade=#{grade}");
                }
                if (null != manager.getUsername()) {
                    WHERE("username=#{username}");
                }
                if (null != manager.getPassword()) {
                    WHERE("password=#{password}");
                }
                if (null != manager.getType()) {
                    WHERE("type=#{type}");
                }
                if (null != manager.getStatus()) {
                    WHERE("status=#{status}");
                }
                if (null != manager.getToken()) {
                    WHERE("token=#{token}");
                }
            }
        }.toString();
    }

    public String updateByCondition(ManagerVo managerVo) {
        return new SQL() {
            {
                UPDATE("xkp_manager");
                SET("status=#{status}");
                if (null != managerVo.getAcademyIds() && 0 < managerVo.getAcademyIds().size()) {
                    String academySql = "academy_id IN (";
                    for (Integer academyId : managerVo.getAcademyIds()) {
                        academySql = academySql + academyId + ",";
                    }
                    academySql = academySql.substring(0, academySql.lastIndexOf(","));
                    academySql += ")";
                    WHERE(academySql);
                }
                if (null != managerVo.getGrades() && 0 < managerVo.getGrades().size()) {
                    String gradeSql = "grade IN (";
                    for (String grade : managerVo.getGrades()) {
                        gradeSql = gradeSql + "'" + grade + "',";
                    }
                    gradeSql = gradeSql.substring(0, gradeSql.lastIndexOf(","));
                    gradeSql += ")";
                    WHERE(gradeSql);
                }
                WHERE("type=#{type}");
            }
        }.toString();
    }
}
