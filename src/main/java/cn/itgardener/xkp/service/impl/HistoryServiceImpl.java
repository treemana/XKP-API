/*
 * Copyright (c) 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.service.impl;

import cn.itgardener.xkp.core.mapper.AcademyMapper;
import cn.itgardener.xkp.core.mapper.ClassMapper;
import cn.itgardener.xkp.core.mapper.CourseMapper;
import cn.itgardener.xkp.core.mapper.SpecialtyMapper;
import cn.itgardener.xkp.core.model.Academy;
import cn.itgardener.xkp.core.model.Class;
import cn.itgardener.xkp.core.model.History;
import cn.itgardener.xkp.core.model.Specialty;
import cn.itgardener.xkp.service.HistoryService;
import cn.itgardener.xkp.service.ScoreService;
import cn.itgardener.xkp.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * @author : Hunter
 * @date : 2018-06-27 16:40
 * @since : Java 8
 */
@Service
public class HistoryServiceImpl implements HistoryService {
    private final AcademyMapper academyMapper;
    private final ClassMapper classMapper;
    private final CourseMapper courseMapper;
    private final ScoreService scoreService;
    private final SpecialtyMapper specialtyMapper;
    private final StudentService studentService;

    @Autowired
    public HistoryServiceImpl(AcademyMapper academyMapper, ClassMapper classMapper, CourseMapper courseMapper,
                              ScoreService scoreService, SpecialtyMapper specialtyMapper, StudentService studentService) {
        this.academyMapper = academyMapper;
        this.classMapper = classMapper;
        this.courseMapper = courseMapper;
        this.scoreService = scoreService;
        this.specialtyMapper = specialtyMapper;
        this.studentService = studentService;
    }


    @Override
    public boolean postHistory() {

        // 获取所有有效班级
        List<Class> classes = classMapper.selectAll();
        for (Class oneClass : classes) {
            History history = new History();
            history.setClassId(oneClass.getSystemId());
            history.setClassName(oneClass.getName());
            history.setGrade(oneClass.getGrade());
            history.setSpecialtyId(oneClass.getSpecialtyId());

            // 填充专业信息
            Specialty specialty = specialtyMapper.selectBySystemId(oneClass.getSpecialtyId());
            history.setSpecialtyName(specialty.getName());
            history.setAcademyId(specialty.getAcademyId());

            // 填充学院信息
            Academy academy = academyMapper.selectAcademyById(specialty.getAcademyId());
            history.setAcademyName(academy.getName());

            history.setTitleDate(getTitleDate());

        }

        courseMapper.deleteAll();
        scoreService.deleteAll();
        studentService.initStudent();
        return true;
    }

    private String getTitleDate() {
        String title = "%d-%d年度%s学期";
        // 半年分隔月
        int halfYearFlag = 6;
        int yearLeft;
        int yearRight;
        String part;
        LocalDate localDate = LocalDate.now();
        yearRight = localDate.getYear();
        int month = localDate.getMonthValue();
        if (halfYearFlag < month) {
            part = "下";
            yearRight--;
        } else {
            part = "上";
        }

        yearLeft = yearRight - 1;
        return String.format(title, yearLeft, yearRight, part);
    }

}
