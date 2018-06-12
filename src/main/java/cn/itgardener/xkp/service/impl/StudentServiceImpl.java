/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.service.impl;

import cn.itgardener.xkp.common.XkpException;
import cn.itgardener.xkp.core.mapper.AcademyMapper;
import cn.itgardener.xkp.core.mapper.ClassMapper;
import cn.itgardener.xkp.core.mapper.SpecialtyMapper;
import cn.itgardener.xkp.core.mapper.StudentMapper;
import cn.itgardener.xkp.core.model.Academy;
import cn.itgardener.xkp.core.model.Class;
import cn.itgardener.xkp.core.model.Specialty;
import cn.itgardener.xkp.core.model.Student;
import cn.itgardener.xkp.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hunter-Yi on 17-8-28 下午9:00
 */
@Service
public class StudentServiceImpl implements StudentService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StudentMapper studentMapper;
    private final ClassMapper classMapper;
    private final SpecialtyMapper specialtyMapper;
    private final AcademyMapper academyMapper;

    @Autowired
    public StudentServiceImpl(StudentMapper studentMapper, ClassMapper classMapper, SpecialtyMapper specialtyMapper, AcademyMapper academyMapper) {
        this.studentMapper = studentMapper;
        this.classMapper = classMapper;
        this.specialtyMapper = specialtyMapper;
        this.academyMapper = academyMapper;
    }

    @Override
    public boolean postStudent(Student student) throws XkpException {
        Student studentCondition = new Student();
        studentCondition.setStudentNumber(student.getStudentNumber());
        List<Student> students = studentMapper.selectByCondition(studentCondition);
        if (0 < students.size()) {
            throw new XkpException("当前学号已存在!");
        }
        return 0 < studentMapper.insert(student);
    }

    @Override
    public boolean putBaseScore(Student student) throws XkpException {
        return 0 < studentMapper.updateBySystemId(student);
    }

    @Override
    public List<Map<String, Object>> getStudent(Student student) {
        List<Map<String, Object>> rtv = new ArrayList<>();
        List<Student> students = studentMapper.selectByCondition(student);
        for (Student oneStudent : students) {
            Map<String, Object> map = new HashMap<>();
            map.put("systemId", oneStudent.getSystemId());
            map.put("name", oneStudent.getName());
            map.put("studentNumber", oneStudent.getStudentNumber());
            rtv.add(map);
        }
        return rtv;
    }

    @Override
    public List<Student> getBaseScore(Student student) {
        return studentMapper.selectByCondition(student);
    }

    @Override
    public String getStringByStudentNumber(Student student) {
        String rtv;
        Student studentContion = new Student();
        studentContion.setStudentNumber(student.getStudentNumber());
        try {
            student = studentMapper.selectByCondition(studentContion).get(0);
            Class xkpClass = classMapper.selectBySystemId(student.getClassId());
            Specialty specialty = specialtyMapper.selectBySystemId(xkpClass.getSpecialtyId());
            Academy academy = academyMapper.selectAcademyById(specialty.getAcademyId());
            rtv = academy.getName() + "-" + specialty.getName() + "-" + xkpClass.getGrade() + "级-" + xkpClass.getName();
        } catch (Exception e) {
            logger.error("根据学号查询学生出错! studentNumber=" + student.getStudentNumber());
            rtv = "未找到相关信息";
        }
        return rtv;
    }

    @Override
    public void initStudent() {
        studentMapper.updateInit();
    }

    @Override
    public boolean deleteStudent(int systemId) {
        return 0 < studentMapper.deleteBySystemId(systemId);
    }
}
