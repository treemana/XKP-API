/*
 * Copyright (c) 2014-2019 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.service.impl;

import cn.itgardener.xkp.common.util.JsonUtil;
import cn.itgardener.xkp.core.mapper.*;
import cn.itgardener.xkp.core.model.Class;
import cn.itgardener.xkp.core.model.*;
import cn.itgardener.xkp.service.BenchmarkService;
import cn.itgardener.xkp.service.HistoryService;
import cn.itgardener.xkp.service.ScoreService;
import cn.itgardener.xkp.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final BenchmarkService benchmarkService;
    private final HistoryMapper historyMapper;

    @Autowired
    public HistoryServiceImpl(AcademyMapper academyMapper, ClassMapper classMapper, CourseMapper courseMapper,
                              ScoreService scoreService, SpecialtyMapper specialtyMapper, StudentService studentService, BenchmarkService benchmarkService, HistoryMapper historyMapper) {
        this.academyMapper = academyMapper;
        this.classMapper = classMapper;
        this.courseMapper = courseMapper;
        this.scoreService = scoreService;
        this.specialtyMapper = specialtyMapper;
        this.studentService = studentService;
        this.benchmarkService = benchmarkService;
        this.historyMapper = historyMapper;
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
            if (null == specialty) {
                classMapper.deleteBySystemId(oneClass.getSystemId());
                continue;
            }
            history.setSpecialtyName(specialty.getName());

            history.setAcademyId(specialty.getAcademyId());

            // 填充学院信息
            Academy academy = academyMapper.selectAcademyById(specialty.getAcademyId());
            history.setAcademyName(academy.getName());

            history.setTitleDate(getTitleDate());

            // 填充大表数据
            String benchMarkDate = JsonUtil.getJsonString(benchmarkService.getBenchmarkByClassId(oneClass.getSystemId()));

            history.setBenchmarkData(benchMarkDate);

            // 填充课程数据
            Course course = new Course();
            course.setClassId(oneClass.getSystemId());
            String courses = JsonUtil.getJsonString(courseMapper.selectByCondition(course));

            history.setCourses(courses);
            historyMapper.insert(history);
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

    @Override
    public List<Object> getHistory(int titleId) {
        History res = historyMapper.selectByCondition(titleId);
        return JsonUtil.getListFromJson(res.getBenchmarkData());
    }

    @Override
    public List<Object> getTitle(int classId) {
        Map<String, Object> map = new HashMap<>(2);
        List<History> historys = historyMapper.selectTitle(classId);
        List<Object> ret = new ArrayList<>();
        for (History history : historys) {
            map.put("systemId", history.getSystemId());
            map.put("name", history.getTitleDate());
            ret.add(map);
        }
        return ret;
    }

    @Override
    public List<String> getGrade() {
        return historyMapper.selectGrade();
    }

    @Override
    public List<Object> getCourses(int titleId) {
        History res = historyMapper.selectByCondition(titleId);
        return JsonUtil.getListFromJson(res.getCourses());
    }

}
