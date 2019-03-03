/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.service.impl;

import cn.itgardener.xkp.common.ErrorMessage;
import cn.itgardener.xkp.common.RestData;
import cn.itgardener.xkp.core.mapper.CourseMapper;
import cn.itgardener.xkp.core.mapper.ScoreMapper;
import cn.itgardener.xkp.core.mapper.StudentMapper;
import cn.itgardener.xkp.core.model.Course;
import cn.itgardener.xkp.core.model.Manager;
import cn.itgardener.xkp.core.model.Score;
import cn.itgardener.xkp.core.model.Student;
import cn.itgardener.xkp.core.model.vo.ScoreVo;
import cn.itgardener.xkp.service.ScoreService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hunter-Yi on 17-9-12 上午9:42
 */
@Service
public class ScoreServiceImpl implements ScoreService {

    private final ScoreMapper scoreMapper;

    private final StudentMapper studentMapper;

    private final CourseMapper courseMapper;

    @Autowired
    public ScoreServiceImpl(ScoreMapper scoreMapper, StudentMapper studentMapper, CourseMapper courseMapper) {
        this.scoreMapper = scoreMapper;
        this.studentMapper = studentMapper;
        this.courseMapper = courseMapper;
    }

    @Override
    public List<Map<String, Object>> getScoreByClassId(int classId) {
        List<Map<String, Object>> rtv = new ArrayList<>();
        Student studentCondition = new Student();
        studentCondition.setClassId(classId);
        List<Student> students = studentMapper.selectByCondition(studentCondition);
        for (Student student : students) {
            Map<String, Object> scoreRtv = new HashMap<>();
            scoreRtv.put("studentId", student.getSystemId());
            scoreRtv.put("studentNumber", student.getStudentNumber());
            scoreRtv.put("name", student.getName());
            Score scoreCondition = new Score();
            scoreCondition.setStudentId(student.getSystemId());
            List<Score> scores = scoreMapper.selectByCondition(scoreCondition);
            List<Map<String, Object>> marks = new ArrayList<>();
            for (Score score : scores) {
                Map<String, Object> mark = new HashMap<>();
                mark.put("courseId", score.getCourseId());
                mark.put("type", score.isType());
                mark.put("examination", score.getExamination());
                mark.put("inspection", score.getInspection());
                marks.add(mark);
            }
            scoreRtv.put("marks", marks);
            rtv.add(scoreRtv);
        }
        return rtv;
    }

    @Override
    public boolean postScore(ScoreVo scoreVo) {
        int studentId = scoreVo.getStudentId();
        if (null != scoreVo.getMarks()) {
            for (Score score : scoreVo.getMarks()) {
                score.setStudentId(studentId);
                scoreMapper.insert(score);
            }
        }
        return true;
    }

    @Override
    public boolean putScore(ScoreVo scoreVo) {
        int studentId = scoreVo.getStudentId();
        if (null != scoreVo.getMarks()) {
            for (Score score : scoreVo.getMarks()) {
                score.setStudentId(studentId);
                List<Score> scores = scoreMapper.selectByCondition(score);
                if (1 == scores.size()) {
                    Score scoreNew = scores.get(0);
                    scoreNew.setExamination(score.getExamination());
                    scoreNew.setInspection(score.getInspection());
                    scoreNew.setType(score.isType());
                    scoreMapper.updateByCondition(scoreNew);
                } else if (0 == scores.size()) {
                    scoreMapper.insert(score);
                }
            }
        }
        return true;
    }

    @Override
    public void deleteAll() {
        scoreMapper.deleteAll();
    }

    @Override
    public RestData postScoreTable(String fileName, Manager currentUser) {
        InputStream is;
        XSSFWorkbook xwb;
        try {
            // 构造 XSSFWorkbook 对象,strPath 传入文件路径
            is = new FileInputStream(fileName);
            xwb = new XSSFWorkbook(is);
        } catch (IOException e) {
            return new RestData(1, ErrorMessage.UPLOAD_ERROR);
        }

        // 读取第一张表格内容
        XSSFSheet sheet = xwb.getSheetAt(0);
        // 定义 row
        XSSFRow row1;
        XSSFRow row2;
        XSSFRow row3;

        Student student = new Student();
        student.setClassId(currentUser.getClassId());

        //将课程存入课程表,
        int k = sheet.getFirstRowNum();
        row1 = sheet.getRow(k);
        row2 = sheet.getRow(++k);
        row3 = sheet.getRow(++k);

        Course course = new Course();
        String credit, name, type;
        for (int j = row1.getFirstCellNum() + 1; j < row1.getPhysicalNumberOfCells(); j++) {
            name = row1.getCell(j).toString();
            if (name.equals("") || null == name) {
                break;
            } else {
                course.setName(name);
            }
            credit = row2.getCell(j).toString();
            if (credit.equals("") || null == credit) {
                break;
            } else {
                course.setCredit(Float.parseFloat(credit));
            }
            type = row3.getCell(j).toString();
            if (type.equals("") || null == type) {
                break;
            } else {
                Float type1 = Float.parseFloat(type);
                if (type1 < 0.5) {
                    course.setType(false);
                } else {
                    course.setType(true);
                }
            }
            course.setClassId(currentUser.getClassId());
            courseMapper.insert(course);
        }

        // 将学生成绩存入成绩表
        XSSFRow row;
        Score score = new Score();
        String studentNumber;
        for (int i = sheet.getFirstRowNum() + 3; i < sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);

            // 获取studentId
            studentNumber = row.getCell(row.getFirstCellNum()).toString();
            Student stu = new Student();
            stu.setStudentNumber(studentNumber);
            List<Student> students = studentMapper.selectByCondition(stu);
            if (0 < students.size()) {
                score.setStudentId(students.get(0).getSystemId());
            } else {
                continue;
            }

            // 获取courseId
            for (int j = row.getFirstCellNum() + 1; j < row.getPhysicalNumberOfCells(); j++) {
                String courseName = row1.getCell(j).toString();
                Course course1 = new Course();
                course1.setName(courseName);
                course1.setClassId(students.get(0).getClassId());
                List<Course> courses = courseMapper.selectByNameAndClassId(course1);

                if (0 < courses.size()) {
                    score.setCourseId(courses.get(0).getSystemId());

                    //判断该门课程为考察/考试
                    if (courses.get(0).isType() == true) {
                        score.setType(true);
                        if ((row.getCell(j).toString()).equals("") || (row.getCell(j).toString()) == null) {
                            score.setExamination(null);
                        } else {
                            score.setExamination(Float.parseFloat(row.getCell(j).toString()));
                        }
                    } else if (courses.get(0).isType() == false) {
                        score.setType(false);
                        if ((row.getCell(j).toString()).equals("") || (row.getCell(j).toString()) == null) {
                            score.setInspection(null);
                        } else {
                            score.setInspection(row.getCell(j).toString());
                        }
                    }
                } else {
                    break;
                }
                scoreMapper.insertScore(score);

            }
        }
        return new RestData(true);
    }

}
