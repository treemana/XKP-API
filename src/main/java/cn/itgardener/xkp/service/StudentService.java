/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.service;

import cn.itgardener.xkp.common.XkpException;
import cn.itgardener.xkp.core.model.Student;

import java.util.List;
import java.util.Map;

/**
 * Created by Hunter-Yi on 17-8-28 下午8:59
 */
public interface StudentService {

    boolean postStudent(Student student) throws XkpException;

    boolean putBaseScore(Student student) throws XkpException;

    boolean deleteStudent(int systemId);

    List<Map<String, Object>> getStudent(Student student);

    List<Student> getBaseScore(Student student);

    String getStringByStudentNumber(Student student);

    void initStudent();
}
