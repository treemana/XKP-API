/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.service.impl;

import cn.itgardener.xkp.core.mapper.CourseMapper;
import cn.itgardener.xkp.service.HistoryService;
import cn.itgardener.xkp.service.ScoreService;
import cn.itgardener.xkp.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : Hunter
 * @date : 2018-06-27 16:40
 * @since : Java 8
 */
@Service
public class HistoryServiceImpl implements HistoryService {
    private final CourseMapper courseMapper;
    private final ScoreService scoreService;
    private final StudentService studentService;

    @Autowired
    public HistoryServiceImpl(CourseMapper courseMapper, ScoreService scoreService, StudentService studentService) {
        this.courseMapper = courseMapper;
        this.scoreService = scoreService;
        this.studentService = studentService;
    }


    @Override
    public boolean postHistory() {


        courseMapper.deleteAll();
        scoreService.deleteAll();
        studentService.initStudent();
        return true;
    }

}
