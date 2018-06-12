/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.core.mapper.provider;

import cn.itgardener.xkp.core.model.Student;
import org.apache.ibatis.jdbc.SQL;

/**
 * Created by zhengyi on 17-8-25 下午2:54
 */
public class StudentProvider {

    public String selectByCondition(Student student) {
        return new SQL() {
            {
                SELECT("system_id AS systemId,class_id AS classId,student_number AS studentNumber,duty_desc AS dutyDesc,academic_desc AS academicDesc");
                SELECT("name,behavior,moral,activity,duty,academic");
                FROM("xkp_student");
                WHERE();
                if (null != student.getClassId()) {
                    WHERE("class_id=#{classId}");
                }
                if (null != student.getStudentNumber()) {
                    WHERE("student_number=#{studentNumber}");
                }
                if (null != student.getSystemId()) {
                    WHERE("system_id=#{systemId}");
                }
                WHERE("system_id IS NOT NULL");
            }
        }.toString();
    }

    public String updateBySystemId(Student student) {
        return new SQL() {
            {
                UPDATE("xkp_student");
                if (null != student.getDutyDesc()) {
                    SET("duty_desc=#{dutyDesc}");
                }
                if (null != student.getAcademicDesc()) {
                    SET("academic_desc=#{academicDesc}");
                }
                if (null != student.getMoral()) {
                    SET("moral=#{moral}");
                }
                if (null != student.getActivity()) {
                    SET("activity=#{activity}");
                }
                if (null != student.getDuty()) {
                    SET("duty=#{duty}");
                }
                if (null != student.getAcademic()) {
                    SET("academic=#{academic}");
                }
                SET("behavior=#{behavior}");
                WHERE("system_id=#{systemId}");

            }
        }.toString();
    }

    public String updateInit() {
        return new SQL() {
            {
                UPDATE("xkp_student");
                SET("duty_desc=''");
                SET("academic_desc=''");
                SET("moral=0");
                SET("activity=0");
                SET("duty=0");
                SET("academic=0");
                SET("behavior='良'");
            }
        }.toString();
    }
}
