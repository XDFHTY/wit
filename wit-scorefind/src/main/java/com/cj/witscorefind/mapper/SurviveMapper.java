package com.cj.witscorefind.mapper;

import com.cj.witscorefind.entity.ExamClass;
import com.cj.witscorefind.entity.ScoreGrade;

import java.util.List;
import java.util.Map;

/**
 * 成活率
 */
public interface SurviveMapper {

    //查询分数档次
    public List<ScoreGrade> findScoreGrade(Map map);

    //查询学生总分成绩
    public List<ExamClass> findTotalScore(Map map);

    //查询学校名称
    public String findSchoolName(String schoolId);
}
