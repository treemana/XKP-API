/*
 * Copyright (c) 2014-2019 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.web;

import cn.itgardener.xkp.common.ErrorMessage;
import cn.itgardener.xkp.common.RestData;
import cn.itgardener.xkp.common.XkpException;
import cn.itgardener.xkp.common.util.GlobalConst;
import cn.itgardener.xkp.common.util.JsonUtil;
import cn.itgardener.xkp.common.util.TokenUtil;
import cn.itgardener.xkp.core.mapper.CourseMapper;
import cn.itgardener.xkp.core.model.Benchmark;
import cn.itgardener.xkp.core.model.Course;
import cn.itgardener.xkp.core.model.Manager;
import cn.itgardener.xkp.core.model.Student;
import cn.itgardener.xkp.core.model.vo.ScoreVo;
import cn.itgardener.xkp.service.BenchmarkService;
import cn.itgardener.xkp.service.HistoryService;
import cn.itgardener.xkp.service.ScoreService;
import cn.itgardener.xkp.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Hunter-Yi on 17-8-24 下午10:16
 */
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
public class StudentApi {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StudentService studentService;
    private final CourseMapper courseMapper;
    private final ScoreService scoreService;
    private final BenchmarkService benchmarkService;
    private final HistoryService historyService;

    @Autowired
    public StudentApi(StudentService studentService, CourseMapper courseMapper, ScoreService scoreService, BenchmarkService benchmarkService, HistoryService historyService) {
        this.studentService = studentService;
        this.courseMapper = courseMapper;
        this.scoreService = scoreService;
        this.benchmarkService = benchmarkService;
        this.historyService = historyService;
    }

    @RequestMapping(value = "/student", method = RequestMethod.POST)
    public RestData postStudent(@RequestBody Student student, HttpServletRequest request) {
        logger.info("POST postStudent : " + JsonUtil.getJsonString(student));
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        try {
            if (studentService.postStudent(student)) {
                return new RestData(student.getSystemId());
            } else {
                return new RestData(1, ErrorMessage.POST_STUDENT_FAILED);
            }
        } catch (XkpException e) {
            return new RestData(1, e.getMessage());
        }
    }

    @RequestMapping(value = "/student/{classId}", method = RequestMethod.GET)
    public RestData getStudent(@PathVariable(value = "classId") int classId, HttpServletRequest request) {
        logger.info("GET getStudent : classId=" + classId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        Student student = new Student();
        student.setClassId(classId);
        List<Map<String, Object>> data = studentService.getStudent(student);
        return new RestData(data);
    }

    @RequestMapping(value = "/student", method = RequestMethod.GET)
    public RestData getStringByStudentNumber(Student student, HttpServletRequest request) {
        logger.info("GET getStringByStudentNumber : student=" + JsonUtil.getJsonString(student));
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (null == student.getStudentNumber()) {
            return new RestData(2, ErrorMessage.PARAMATER_ERROR);
        }
        String data = studentService.getStringByStudentNumber(student);
        return new RestData(data);
    }

    @RequestMapping(value = "/student/{systemId}", method = RequestMethod.DELETE)
    public RestData deleteStudent(@PathVariable(value = "systemId") int systemId, HttpServletRequest request) {
        logger.info("DELETE deleteStudent : systemId=" + systemId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        boolean data = studentService.deleteStudent(systemId);
        return new RestData(data);
    }

    @RequestMapping(value = "/base-score/{classId}", method = RequestMethod.GET)
    public RestData getBaseScore(@PathVariable(value = "classId") int classId, HttpServletRequest request) {
        logger.info("GET getBaseScore : classId=" + classId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        Student student = new Student();
        student.setClassId(classId);
        List<Student> data = studentService.getBaseScore(student);
        return new RestData(data);
    }

    @RequestMapping(value = "/base-score", method = RequestMethod.PUT)
    public RestData putBaseScore(@RequestBody Student student, HttpServletRequest request) {
        logger.info("PUT putBaseScore : " + JsonUtil.getJsonString(student));
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        boolean data = false;
        try {
            data = studentService.putBaseScore(student);
        } catch (XkpException e) {
            new RestData(1, e.getMessage());
        }
        return new RestData(data);
    }

    @RequestMapping(value = "/course", method = RequestMethod.POST)
    public RestData postCourse(@RequestBody Course course, HttpServletRequest request) {
        logger.info("POST postCourse : " + JsonUtil.getJsonString(course));
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (0 < courseMapper.insert(course)) {
            return new RestData(course.getSystemId());
        } else {
            return new RestData(1, ErrorMessage.POST_COURSE_FAILED);
        }
    }

    @RequestMapping(value = "/course/{classId}", method = RequestMethod.GET)
    public RestData getCourse(@PathVariable(value = "classId") int classId, HttpServletRequest request) {
        logger.info("GET getCourse : classId=" + classId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        Course course = new Course();
        course.setClassId(classId);
        List<Course> data = courseMapper.selectByCondition(course);
        return new RestData(data);
    }

    @RequestMapping(value = "/course/{systemId}", method = RequestMethod.DELETE)
    public RestData deleteCourse(@PathVariable(value = "systemId") int systemId, HttpServletRequest request) {
        logger.info("DELETE deleteCourse : systemId=" + systemId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"C".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        boolean data = 0 < courseMapper.deleteBySystemId(systemId);
        return new RestData(data);
    }

    @RequestMapping(value = "/score", method = RequestMethod.POST)
    public RestData postScore(@RequestBody ScoreVo scoreVo, HttpServletRequest request) {
        logger.info("POST postScore : " + JsonUtil.getJsonString(scoreVo));
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"C".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        if (scoreService.postScore(scoreVo)) {
            return new RestData(true);
        } else {
            return new RestData(1, ErrorMessage.POST_COURSE_FAILED);
        }
    }

    @RequestMapping(value = "/score/{classId}", method = RequestMethod.GET)
    public RestData getScore(@PathVariable(value = "classId") int classId, HttpServletRequest request) {
        logger.info("GET getScore : classId=" + classId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"C".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        List<Map<String, Object>> data = scoreService.getScoreByClassId(classId);
        return new RestData(data);
    }

    @RequestMapping(value = "/score", method = RequestMethod.PUT)
    public RestData putScore(@RequestBody ScoreVo scoreVo, HttpServletRequest request) {
        logger.info("PUT putScore : " + JsonUtil.getJsonString(scoreVo));
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"C".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }
        if (scoreService.putScore(scoreVo)) {
            return new RestData(true);
        } else {
            return new RestData(1, ErrorMessage.POST_COURSE_FAILED);
        }
    }

    @RequestMapping(value = "/score-table", method = RequestMethod.POST)
    public RestData postScoreTable(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        logger.info("POST score-import : ");
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        if (!"C".equals(currentUser.getType())) {
            return new RestData(1, ErrorMessage.NO_PERMITION);
        }

        RestData rtv;
        String fileName = GlobalConst.UPLOAD_PATH + TokenUtil.getToken() + ".xlsx";

        File localFile = new File(fileName);
        try {
            file.transferTo(localFile);
            rtv = scoreService.postScoreTable(fileName, currentUser);
            if (!localFile.delete()) {
                logger.warn("Delete " + fileName + " failed!");
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
            rtv = new RestData(1, e.getLocalizedMessage());
        }

        return rtv;
    }

    @RequestMapping(value = "/benchmark/{classId}", method = RequestMethod.GET)
    public RestData getBenchmark(@PathVariable(value = "classId") int classId, HttpServletRequest request) {
        logger.info("GET getBenchmark : classId=" + classId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        List<Benchmark> data = benchmarkService.getBenchmarkByClassId(classId);
        return new RestData(data);
    }

    @RequestMapping(value = "/benchmark/download-docx/{classId}", method = RequestMethod.GET)
    public void downloadBenchmarkDocx(@PathVariable(value = "classId") int classId, HttpServletResponse response) {
        logger.info("GET downloadBenchmarkDocx : classId=" + classId);
        benchmarkService.downLoadBenchmarkDocxByClassId(classId, response);
    }

    @RequestMapping(value = "/benchmark/download-xlsx/{classId}", method = RequestMethod.GET)
    public void downloadBenchmarkXlsx(@PathVariable(value = "classId") int classId, HttpServletResponse response) {
        logger.info("GET downloadBenchmarkXlsx : classId=" + classId);
        benchmarkService.downLoadBenchmarkXlsxByClassId(classId, response);
    }

    @RequestMapping(value = "/history/benchmark/{titleId}", method = RequestMethod.GET)
    public RestData getHistoryBenchmark(@PathVariable(value = "titleId") int titleId, HttpServletRequest request) {
        logger.info("GET getHistoryBenchmark ");
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        List<Object> data = historyService.getHistory(titleId);
        return new RestData(data);
    }

    @RequestMapping(value = "/history/title/{classId}", method = RequestMethod.GET)
    public RestData getHistoryTitle(@PathVariable(value = "classId") int classId, HttpServletRequest request) {
        logger.info("GET getHistoryTitle");
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }
        List<Object> data = historyService.getTitle(classId);
        return new RestData(data);
    }

    @RequestMapping(value = "/history/course/{titleId}", method = RequestMethod.GET)
    public RestData getHistoryCourse(@PathVariable(value = "titleId") int titleId, HttpServletRequest request) {
        logger.info("GET getHistoryCourse : titleId=" + titleId);
        Manager currentUser = TokenUtil.getManagerByToken(request);
        if (null == currentUser) {
            return new RestData(2, ErrorMessage.PLEASE_RELOGIN);
        }

        List<Object> data = historyService.getCourses(titleId);
        return new RestData(data);
    }

}
