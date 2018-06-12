/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.service.impl;

import cn.itgardener.xkp.core.mapper.*;
import cn.itgardener.xkp.core.model.*;
import cn.itgardener.xkp.core.model.Class;
import cn.itgardener.xkp.service.BenchmarkService;
import cn.itgardener.xkp.service.StudentService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Hunter-Yi on 17-9-13 上午11:57
 */
@Service
public class BenchmarkServiceImpl implements BenchmarkService {

    private final StudentService studentService;
    private final ScoreMapper scoreMapper;
    private final CourseMapper courseMapper;
    private final FreeMarkerConfigurer freeMarkerConfigurer;
    private final ClassMapper classMapper;
    private final SpecialtyMapper specialtyMapper;
    private final AcademyMapper academyMapper;

    @Autowired
    public BenchmarkServiceImpl(StudentService studentService, ScoreMapper scoreMapper, CourseMapper courseMapper, FreeMarkerConfigurer freeMarkerConfigurer, ClassMapper classMapper, SpecialtyMapper specialtyMapper, AcademyMapper academyMapper) {
        this.studentService = studentService;
        this.scoreMapper = scoreMapper;
        this.courseMapper = courseMapper;
        this.freeMarkerConfigurer = freeMarkerConfigurer;
        this.classMapper = classMapper;
        this.specialtyMapper = specialtyMapper;
        this.academyMapper = academyMapper;
    }

    @Override
    public List<Benchmark> getBenchmarkByClassId(int classId) {
        List<Benchmark> rtv = new ArrayList<>();
        Student studentContition = new Student();
        studentContition.setClassId(classId);
        List<Student> students = studentService.getBaseScore(studentContition);
        List<Sort> scoreSorts = new ArrayList<>();
        List<Sort> totalSorts = new ArrayList<>();
        for (Student student : students) {
            Benchmark benchmark = new Benchmark();
            benchmark.setStudentNumber(student.getStudentNumber());
            benchmark.setName(student.getName());
            Score scoreCondition = new Score();
            scoreCondition.setStudentId(student.getSystemId());
            List<Score> scores = scoreMapper.selectByCondition(scoreCondition);
            // 各科成绩
            benchmark.setMarks(scores);
            // 学术分
            benchmark.setAcademic(student.getAcademic());
            // 学术说明
            benchmark.setAcademicDesc(student.getAcademicDesc());

            Course courseCondition = new Course();
            courseCondition.setClassId(classId);
            List<Course> courses = courseMapper.selectByCondition(courseCondition);
            float[] pointScore = getScoreFromMarks(benchmark.getMarks(), courses);
            // 绩点
            benchmark.setPoint(getDecimal(pointScore[0]));
            // 操评
            benchmark.setBehavior(student.getBehavior());
            // 德育
            float moral = (float) ((student.getMoral() + 50) * 0.2);
            benchmark.setMoral(getDecimal(moral));
            // 文体
            float activity = (student.getActivity() + 50) / 10;
            benchmark.setActivity(getDecimal(activity));
            // 其他
            float other = 20 + student.getDuty();
            if ("优".equals(student.getBehavior())) {
                other += 30;
            } else if ("良".equals(student.getBehavior())) {
                other += 20;
            } else if ("中".equals(student.getBehavior())) {
                other += 10;
            }
            other = (float) (other / 20 - 0.25);
            benchmark.setOther(getDecimal(other));
            // 职务
            benchmark.setDutyDesc(student.getDutyDesc());
            // 智育
            float score = pointScore[1] + student.getAcademic();
            benchmark.setScore(getDecimal(score));
            // 综合
            float total = (float) (benchmark.getMoral() + benchmark.getScore() * 0.65 + benchmark.getActivity() + benchmark.getOther());
            benchmark.setTotal(getDecimal(total));
            rtv.add(benchmark);
            Sort scoreSort = new Sort();
            scoreSort.setStudentNumber(benchmark.getStudentNumber());
            scoreSort.setNumber(benchmark.getScore());
            scoreSorts.add(scoreSort);

            Sort totalSort = new Sort();
            totalSort.setStudentNumber(benchmark.getStudentNumber());
            totalSort.setNumber(benchmark.getTotal());
            totalSorts.add(totalSort);
        }
        // 添加排名
        setRank(rtv, scoreSorts, totalSorts);
        return rtv;
    }

    @Override
    public void downLoadBenchmarkDocxByClassId(int classId, HttpServletResponse response) {

        String[] benchmarkNames = getBenchmarkNames(classId);
        // 设置文件MIME类型
        response.setContentType("application/ms-word");
        // 设置Content-Disposition
        response.setHeader("Content-Disposition", "attachment;filename=" + benchmarkNames[0] + ".doc");
        freeMarkerConfigurer.getConfiguration().setClassForTemplateLoading(getClass(), "/");
        Template template = null;
        try {
            template = freeMarkerConfigurer.getConfiguration().getTemplate("benchmark.ftl");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Course courseCondition = new Course();
        courseCondition.setClassId(classId);
        List<Course> courses = courseMapper.selectByCondition(courseCondition);
        List<String> courseNames = new ArrayList<>();
        for (Course course : courses) {
            courseNames.add(course.getName() + "(" + course.getCredit() + "分)");
        }
        Map<String, Object> root = new HashMap<>(8);
        root.put("title", getTitle());
        root.put("secondTitle", benchmarkNames[1]);
        root.put("courseNames", courseNames);
        List<Benchmark> benchmarks = getBenchmarks(classId, courses);
        root.put("benchmarks", benchmarks);
        root.put("behaviorNumber", getBehaviorNumber(benchmarks));
        root.put("studentTotal", benchmarks.size());
        root.put("notes", getNotes(benchmarks));
        root.put("date", getDate());
        try {
            if (template != null) {
                template.process(root, new OutputStreamWriter(response.getOutputStream()));
            }
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void downLoadBenchmarkXlsxByClassId(int classId, HttpServletResponse response) {
        // todo
    }

    private float getDecimal(float source) {
        BigDecimal bigDecimal = new BigDecimal(source);
        return bigDecimal.setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    private float[] getScoreFromMarks(List<Score> marks, List<Course> courses) {
        float[] rtv = new float[2];
        if (null != marks) {
            Map<Integer, Float> courseCredit = new HashMap<>();
            if (null != courses) {
                for (Course course : courses) {
                    if (course.isType()) {
                        courseCredit.put(course.getSystemId(), course.getCredit());
                    }
                }
            }
            float scoreSum = 0;
            float creditSum = 0;
            float pointSum = 0;
            float credit;
            for (Score score : marks) {
                if (score.isType() && null != score.getExamination()) {
                    if (null == courseCredit.get(score.getCourseId())) {
                        continue;
                    }
                    credit = courseCredit.get(score.getCourseId());
                    scoreSum = scoreSum + score.getExamination() * credit;
                    pointSum = pointSum + getPointByExamination(score.getExamination()) * credit;
                    creditSum = creditSum + credit;
                }
            }
            if (0 != creditSum) {
                rtv[0] = pointSum / creditSum;
                rtv[1] = scoreSum / creditSum;
            }
        }
        return rtv;
    }

    private float getPointByExamination(float examination) {
        double point;
        if (95 <= examination) {
            point = 4.5;
        } else if (90 <= examination) {
            point = 4;
        } else if (85 <= examination) {
            point = 3.5;
        } else if (80 <= examination) {
            point = 3;
        } else if (75 <= examination) {
            point = 2.5;
        } else if (70 <= examination) {
            point = 2;
        } else if (65 <= examination) {
            point = 1.5;
        } else if (60 <= examination) {
            point = 1;
        } else {
            point = 0;
        }
        return (float) point;
    }

    private void setRank(List<Benchmark> benchmarks, List<Sort> scoreSorts, List<Sort> totalSorts) {
        Map<String, Integer> scoreRankMap = new HashMap<>();
        Map<String, Integer> totalRankMap = new HashMap<>();
        Collections.sort(scoreSorts);
        int rank = 1;
        int n = 1;
        float last = 0;
        for (Sort sort : scoreSorts) {
            if (last != sort.getNumber()) {
                rank = n;
            }
            scoreRankMap.put(sort.getStudentNumber(), rank);
            n++;
            last = sort.getNumber();
        }
        Collections.sort(totalSorts);
        rank = n = 1;
        last = 0;
        for (Sort sort : totalSorts) {
            if (last != sort.getNumber()) {
                rank = n;
            }
            totalRankMap.put(sort.getStudentNumber(), rank);
            n++;
            last = sort.getNumber();
        }
        for (Benchmark benchmark : benchmarks) {
            benchmark.setScoreRank(scoreRankMap.get(benchmark.getStudentNumber()));
            benchmark.setComplexRank(totalRankMap.get(benchmark.getStudentNumber()));
        }
    }

    private List<Benchmark> getBenchmarks(int classId, List<Course> courses) {
        List<Benchmark> benchmarks = getBenchmarkByClassId(classId);
        for (Benchmark benchmark : benchmarks) {
            if (null == benchmark.getDutyDesc()) {
                benchmark.setDutyDesc("");
            }
            Map<Integer, String> courseScoreMap = new HashMap<>();
            for (Score score : benchmark.getMarks()) {
                String mark;
                if (score.isType()) {
                    if (null != score.getExamination()) {
                        mark = String.valueOf(score.getExamination());
                    } else {
                        mark = "";
                    }
                } else {
                    mark = score.getInspection();
                }
                if (null == mark) {
                    mark = "";
                }
                courseScoreMap.put(score.getCourseId(), mark);
            }

            List<Score> ftlScores = new ArrayList<>();
            for (Course course : courses) {
                Score score = new Score();
                String inspection = courseScoreMap.get(course.getSystemId());
                if (null == inspection) {
                    inspection = "";
                }
                score.setInspection(inspection);
                ftlScores.add(score);
            }
            benchmark.setMarks(ftlScores);
        }


        return benchmarks;
    }

    private String getTitle() {
        String title = "东北林业大学%d-%d年度%s学期学生素质综合考评监督统计表";
        int yearLeft;
        int yearRight;
        String part;
        LocalDate localDate = LocalDate.now();
        int month = localDate.getMonthValue();
        if (6 < month) {
            part = "下";
        } else {
            part = "上";
        }
        yearRight = localDate.getYear();
        yearLeft = yearRight - 1;
        return String.format(title, yearLeft, yearRight, part);
    }

    private String[] getBenchmarkNames(int classId) {
        String[] names = new String[2];
        Class xkpClass = classMapper.selectBySystemId(classId);
        Specialty specialty = specialtyMapper.selectBySystemId(xkpClass.getSpecialtyId());
        Academy academy = academyMapper.selectAcademyById(specialty.getAcademyId());
        names[0] = specialty.getName() + "-" + xkpClass.getGrade() + "级-" + xkpClass.getName();
        names[1] = academy.getName() + "-" + names[0];
        try {
            names[0] = new String(names[0].getBytes(), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return names;
    }

    private List<String> getNotes(List<Benchmark> benchmarks) {
        List<String> notes = new ArrayList<>();
        for (Benchmark benchmark : benchmarks) {
            if (null != benchmark.getAcademic() && 0 < benchmark.getAcademic() && null != benchmark.getAcademicDesc()) {
                notes.add(benchmark.getName() + " : " + benchmark.getAcademicDesc());
            }
        }
        return notes;
    }

    private String getBehaviorNumber(List<Benchmark> benchmarks) {
        String behaviorNumber = "优:%d 良:%d 中:%d 差:%d";
        int best = 0;
        int better = 0;
        int general = 0;
        int bad = 0;

        for (Benchmark benchmark : benchmarks) {
            if ("优".equals(benchmark.getBehavior())) {
                best++;
            } else if ("良".equals(benchmark.getBehavior())) {
                better++;
            } else if ("中".equals(benchmark.getBehavior())) {
                general++;
            } else {
                bad++;
            }
        }

        return String.format(behaviorNumber, best, better, general, bad);
    }

    private String getDate() {
        LocalDate localDate = LocalDate.now();
        return localDate.toString();
    }
}
