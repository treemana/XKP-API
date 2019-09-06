/*
 * Copyright (c) 2014-2019 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.service.impl;

import cn.itgardener.xkp.core.mapper.*;
import cn.itgardener.xkp.core.model.Class;
import cn.itgardener.xkp.core.model.*;
import cn.itgardener.xkp.service.BenchmarkService;
import cn.itgardener.xkp.service.StudentService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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
            float moral = (float)((student.getMoral() + 50) * 0.2);
            benchmark.setMoral(getDecimal(moral));
            // 文体
            float activity = (float)((student.getActivity() + 50) * 0.1);
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
            other = other / 20;
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
        Course courseCondition = new Course();
        courseCondition.setClassId(classId);
        List<Course> courses = courseMapper.selectByCondition(courseCondition);
        List<String> courseNames = new ArrayList<>();
        for (Course course : courses) {
            courseNames.add(course.getName() + "(" + course.getCredit() + "分)");
        }
        // 定义表头
        List<String> leftTitles = Arrays.asList("学号", "姓名");
        List<String> rightTitles = Arrays.asList("学术科研与素质教育", "平均绩点", "操行评等", "德育", "文体",
                "其他", "职务", "智育", "总分", "综合排名", "智育排名", "本人签字");
        List<String> tableTitles = new ArrayList<>();
        tableTitles.addAll(leftTitles);
        tableTitles.addAll(courseNames);
        tableTitles.addAll(rightTitles);

        // 总列数
        int cellNum = tableTitles.size();

        String[] benchmarkNames = getBenchmarkNames(classId);

        // 设置文件MIME类型
        response.setContentType("application/ms-excel");
        // 设置Content-Disposition
        response.setHeader("Content-Disposition", "attachment;filename=" + benchmarkNames[0] + ".xlsx");

        // 创建excel工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();

        // 设置文档作者
        XSSFExcelExtractor extractor = new XSSFExcelExtractor(workbook);
        POIXMLProperties.CoreProperties coreProps = extractor.getCoreProperties();
        coreProps.setCreator("www.itgardener.cn");

        // 创建工作表sheet
        XSSFSheet sheet = workbook.createSheet("成绩大表");
        // 垂直居中
        sheet.setVerticallyCenter(true);
        // 水平居中
        sheet.setHorizontallyCenter(true);

        //页边距
        sheet.setMargin(Sheet.TopMargin, 0.5D);
        sheet.setMargin(Sheet.BottomMargin, 0.5D);
        sheet.setMargin(Sheet.LeftMargin, 0.25D);
        sheet.setMargin(Sheet.RightMargin, 0.25D);

        // 打印设置
        XSSFPrintSetup xssfPrintSetup = sheet.getPrintSetup();
        // A3
        xssfPrintSetup.setPaperSize(XSSFPrintSetup.A3_PAPERSIZE);
        // 打印方向:横向
        xssfPrintSetup.setLandscape(true);

        // 学号列宽
        sheet.setColumnWidth(0, 256 * 11);
        // 姓名列宽
        sheet.setColumnWidth(1, 256 * 7);

        // 各科列宽
        for (int i = 2; i < cellNum - rightTitles.size(); i++) {
            sheet.setColumnWidth(i, 256 * 109 / courseNames.size());
        }

        // 学术科研与素质教育列宽
        sheet.setColumnWidth(cellNum - 12, 256 * 4);
        // 平均绩点列宽
        sheet.setColumnWidth(cellNum - 11, 256 * 9);
        // 操行评等列宽
        sheet.setColumnWidth(cellNum - 10, 256 * 3);
        // 德育列宽
        sheet.setColumnWidth(cellNum - 9, 256 * 5);
        // 文体列宽
        sheet.setColumnWidth(cellNum - 8, 256 * 5);
        // 其他列宽
        sheet.setColumnWidth(cellNum - 7, 256 * 5);
        // 职务列宽
        sheet.setColumnWidth(cellNum - 6, 256 * 9);
        // 智育列宽
        sheet.setColumnWidth(cellNum - 5, 256 * 8);
        // 总分列宽
        sheet.setColumnWidth(cellNum - 4, 256 * 8);
        // 综合排名列宽
        sheet.setColumnWidth(cellNum - 3, 256 * 3);
        // 智育排名列宽
        sheet.setColumnWidth(cellNum - 2, 256 * 3);
        // 本人签字
        sheet.setColumnWidth(cellNum - 1, 256 * 9);

        // 合并title行使用的列
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, tableTitles.size() - 1));
        // 合并subTitle行使用的列
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, tableTitles.size() - 1));

        // 创建行
        XSSFRow row;
        // 创建列
        XSSFCell cell;

        // 标题
        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue(getTitle());
        // 设置格式
        XSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 字体
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);
        cell.setCellStyle(titleStyle);

        // 副标题
        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue(benchmarkNames[1]);
        // 设置格式
        XSSFCellStyle subTitleStyle = workbook.createCellStyle();
        subTitleStyle.setAlignment(HorizontalAlignment.CENTER);
        subTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cell.setCellStyle(subTitleStyle);

        // 插入第一行数据的表头
        row = sheet.createRow(2);

        for (int i = 0; i < cellNum; i++) {
            XSSFCellStyle xssfCellStyle = workbook.createCellStyle();
            // 自动换行
            xssfCellStyle.setWrapText(true);
            // 水平居中
            xssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
            // 垂直居中
            xssfCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // 上下左右边框
            xssfCellStyle.setBorderTop(BorderStyle.THIN);
            xssfCellStyle.setBorderBottom(BorderStyle.THIN);
            xssfCellStyle.setBorderLeft(BorderStyle.THIN);
            xssfCellStyle.setBorderRight(BorderStyle.THIN);

            cell = row.createCell(i);
            // 科目名称列需要根据长度调字号
            if (2 <= i && (cellNum - rightTitles.size()) >= i && 4 < tableTitles.get(i).length()) {
                // 设置字号
                Font tableHeadFont = workbook.createFont();
                tableHeadFont.setFontHeightInPoints((short) 8);
                xssfCellStyle.setFont(tableHeadFont);
            }
            cell.setCellStyle(xssfCellStyle);
            cell.setCellValue(tableTitles.get(i));
        }

        List<Benchmark> benchmarks = getBenchmarks(classId, courses);

        // 写入数据
        int rowIndex = 3;
        int cellIndex;
        for (Benchmark benchmark : benchmarks) {
            row = sheet.createRow(rowIndex);
            rowIndex++;
            cellIndex = 0;

            XSSFCellStyle xssfCellStyle = workbook.createCellStyle();
            // 自动换行
            xssfCellStyle.setWrapText(true);
            // 水平居中
            xssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
            // 垂直居中
            xssfCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // 上写左右边框
            xssfCellStyle.setBorderTop(BorderStyle.THIN);
            xssfCellStyle.setBorderBottom(BorderStyle.THIN);
            xssfCellStyle.setBorderLeft(BorderStyle.THIN);
            xssfCellStyle.setBorderRight(BorderStyle.THIN);

            // 学号
            cell = row.createCell(cellIndex++);
            cell.setCellStyle(xssfCellStyle);
            cell.setCellValue(benchmark.getStudentNumber());

            // 姓名
            cell = row.createCell(cellIndex++);
            if (3 < benchmark.getName().length()) {
                XSSFCellStyle nameStyle = workbook.createCellStyle();
                // 自动换行
                nameStyle.setWrapText(true);
                // 水平居中
                nameStyle.setAlignment(HorizontalAlignment.CENTER);
                // 垂直居中
                nameStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                // 上写左右边框
                nameStyle.setBorderTop(BorderStyle.THIN);
                nameStyle.setBorderBottom(BorderStyle.THIN);
                nameStyle.setBorderLeft(BorderStyle.THIN);
                nameStyle.setBorderRight(BorderStyle.THIN);

                // 设置字号
                Font nameFont = workbook.createFont();
                nameFont.setFontHeightInPoints((short) 8);
                nameStyle.setFont(nameFont);
                cell.setCellStyle(nameStyle);
            } else {
                cell.setCellStyle(xssfCellStyle);
            }
            cell.setCellValue(benchmark.getName());

            // 各科成绩
            for (Score score : benchmark.getMarks()) {
                cell = row.createCell(cellIndex++);
                cell.setCellStyle(xssfCellStyle);
                cell.setCellValue(score.getInspection());
            }

            // 学术科研与素质教育
            cell = row.createCell(cellIndex++);
            cell.setCellStyle(xssfCellStyle);
            cell.setCellValue(benchmark.getAcademic());

            // 平均绩点
            cell = row.createCell(cellIndex++);
            cell.setCellStyle(xssfCellStyle);
            cell.setCellValue(benchmark.getPoint());

            // 操行评等
            cell = row.createCell(cellIndex++);
            cell.setCellStyle(xssfCellStyle);
            cell.setCellValue(benchmark.getBehavior());

            // 德育
            cell = row.createCell(cellIndex++);
            cell.setCellStyle(xssfCellStyle);
            cell.setCellValue(benchmark.getMoral());

            // 文体
            cell = row.createCell(cellIndex++);
            cell.setCellStyle(xssfCellStyle);
            cell.setCellValue(benchmark.getActivity());

            // 其他
            cell = row.createCell(cellIndex++);
            cell.setCellStyle(xssfCellStyle);
            cell.setCellValue(benchmark.getOther());

            // 职务描述
            cell = row.createCell(cellIndex++);
            XSSFCellStyle dutyStyle = workbook.createCellStyle();
            // 自动换行
            dutyStyle.setWrapText(true);
            // 水平居中
            dutyStyle.setAlignment(HorizontalAlignment.CENTER);
            // 垂直居中
            dutyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // 上写左右边框
            dutyStyle.setBorderTop(BorderStyle.THIN);
            dutyStyle.setBorderBottom(BorderStyle.THIN);
            dutyStyle.setBorderLeft(BorderStyle.THIN);
            dutyStyle.setBorderRight(BorderStyle.THIN);
            // 设置字号
            Font dutyDescFont = workbook.createFont();
            if (4 >= benchmark.getDutyDesc().length()) {
                cell.setCellStyle(xssfCellStyle);
            } else if (4 < benchmark.getDutyDesc().length() && 10 >= benchmark.getDutyDesc().length()) {
                dutyDescFont.setFontHeightInPoints((short) 9);
                dutyStyle.setFont(dutyDescFont);
                cell.setCellStyle(dutyStyle);
            } else {
                dutyDescFont.setFontHeightInPoints((short) 6);
                dutyStyle.setFont(dutyDescFont);
                cell.setCellStyle(dutyStyle);
            }
            cell.setCellValue(benchmark.getDutyDesc());

            // 智育
            cell = row.createCell(cellIndex++);
            cell.setCellStyle(xssfCellStyle);
            cell.setCellValue(benchmark.getScore());

            // 总分
            cell = row.createCell(cellIndex++);
            cell.setCellStyle(xssfCellStyle);
            cell.setCellValue(benchmark.getTotal());

            // 综合排名
            cell = row.createCell(cellIndex++);
            cell.setCellStyle(xssfCellStyle);
            cell.setCellValue(benchmark.getComplexRank());

            // 智育排名
            cell = row.createCell(cellIndex++);
            cell.setCellStyle(xssfCellStyle);
            cell.setCellValue(benchmark.getScoreRank());

            // 本人签字
            cell = row.createCell(cellIndex);
            cell.setCellStyle(xssfCellStyle);
            cell.setCellValue("         ");
        }

        // 空单元格加右边框
        XSSFCellStyle rightBorderStyle = workbook.createCellStyle();
        rightBorderStyle.setBorderRight(BorderStyle.THIN);

        // 文字左对齐
        XSSFCellStyle leftValueStyle = workbook.createCellStyle();
        leftValueStyle.setAlignment(HorizontalAlignment.LEFT);
        leftValueStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 空单元格加左边框
        XSSFCellStyle leftBorderStyle = workbook.createCellStyle();
        leftBorderStyle.setBorderLeft(BorderStyle.THIN);

        // 表格下部操行评等列表统计
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, cellNum - 1));
        row = sheet.createRow(rowIndex++);
        cell = row.createCell(0);
        // 设置格式
        XSSFCellStyle leftStyle = workbook.createCellStyle();
        leftStyle.setAlignment(HorizontalAlignment.LEFT);
        leftStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        leftStyle.setBorderLeft(BorderStyle.THIN);
        cell.setCellStyle(leftStyle);
        cell.setCellValue("操行评等(人) : " + getBehaviorNumber(benchmarks));
        row.createCell(cellNum - 1).setCellStyle(rightBorderStyle);

        // 备注
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, cellNum - 1));
        row = sheet.createRow(rowIndex++);
        cell = row.createCell(0);
        cell.setCellStyle(leftStyle);
        cell.setCellValue("备注 : ");
        row.createCell(cellNum - 1).setCellStyle(rightBorderStyle);

        // 学术加分备注
        for (String note : getNotes(benchmarks)) {
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, cellNum - 1));
            row = sheet.createRow(rowIndex++);

            cell = row.createCell(1);
            cell.setCellStyle(leftValueStyle);
            cell.setCellValue(note);

            row.createCell(0).setCellStyle(leftBorderStyle);
            row.createCell(cellNum - 1).setCellStyle(rightBorderStyle);
        }

        // 班级人数
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, cellNum - 1));
        row = sheet.createRow(rowIndex++);
        row.createCell(cellNum - 1).setCellStyle(rightBorderStyle);
        // 设置格式
        XSSFCellStyle rightValuStyle = workbook.createCellStyle();
        rightValuStyle.setAlignment(HorizontalAlignment.RIGHT);
        rightValuStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        rightValuStyle.setBorderLeft(BorderStyle.THIN);
        cell = row.createCell(0);
        cell.setCellStyle(rightValuStyle);
        cell.setCellValue("班级人数 : " + benchmarks.size() + " 人 ");

        // 统计人
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, cellNum - 1));
        row = sheet.createRow(rowIndex++);
        row.createCell(cellNum - 1).setCellStyle(rightBorderStyle);
        cell = row.createCell(0);
        cell.setCellStyle(rightValuStyle);
        cell.setCellValue("统计人 : __________ ");

        // 下标日期
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, cellNum - 1));
        row = sheet.createRow(rowIndex);
        XSSFCellStyle dateRightBorderStyle = workbook.createCellStyle();
        dateRightBorderStyle.setBorderRight(BorderStyle.THIN);
        dateRightBorderStyle.setBorderBottom(BorderStyle.THIN);
        row.createCell(cellNum - 1).setCellStyle(dateRightBorderStyle);

        XSSFCellStyle dateRightValuStyle = workbook.createCellStyle();
        dateRightValuStyle.setAlignment(HorizontalAlignment.RIGHT);
        dateRightValuStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dateRightValuStyle.setBorderLeft(BorderStyle.THIN);
        dateRightValuStyle.setBorderBottom(BorderStyle.THIN);
        cell = row.createCell(0);
        cell.setCellStyle(dateRightValuStyle);
        cell.setCellValue(getDate() + " ");
        // 最后一行要设置底边框
        XSSFCellStyle bottomBorderStyle = workbook.createCellStyle();
        bottomBorderStyle.setBorderBottom(BorderStyle.THIN);
        for (int i = 1; cellNum - 1 > i; i++) {
            row.createCell(i).setCellStyle(bottomBorderStyle);
        }

        // 创建excel文件
        // 将excel写入
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if(100 == examination) {
            point = 5;
        } else if (95 <= examination) {
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
        Map<String, Integer> scoreRankMap = new HashMap<>(scoreSorts.size());
        Map<String, Integer> totalRankMap = new HashMap<>(totalSorts.size());
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
            Map<Integer, String> courseScoreMap = new HashMap<>(benchmark.getMarks().size());
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
        // 半年分隔月
        int halfYearFlag = 6;
        int yearLeft;
        int yearRight;
        String part;
        LocalDate localDate = LocalDate.now();
        int month = localDate.getMonthValue();
        if (halfYearFlag < month) {
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

        // 文件名
        names[0] = specialty.getName() + "-" + xkpClass.getGrade() + "级-" + xkpClass.getName();
        // 副标题
        names[1] = academy.getName() + "-" + names[0];
        names[0] = new String(names[0].getBytes(), StandardCharsets.ISO_8859_1);
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
