package cn.itgardener.xkp.service.impl;

import cn.itgardener.xkp.core.mapper.ScoreMapper;
import cn.itgardener.xkp.core.mapper.StudentMapper;
import cn.itgardener.xkp.core.model.Score;
import cn.itgardener.xkp.core.model.Student;
import cn.itgardener.xkp.core.model.vo.ScoreVo;
import cn.itgardener.xkp.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    public ScoreServiceImpl(ScoreMapper scoreMapper, StudentMapper studentMapper) {
        this.scoreMapper = scoreMapper;
        this.studentMapper = studentMapper;
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
}
