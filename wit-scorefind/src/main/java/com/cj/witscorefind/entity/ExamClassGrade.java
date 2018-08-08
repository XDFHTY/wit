package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 班级-档次
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamClassGrade {

    //档次名称
    private String gradeName;

    //档次人数
    private int gradeNameNum;

    //本档次学生成绩集合
    private List<ClassStudentTotalScore> classStudentTotalScores;

    //成活率信息
    private List<ExamClassGradeSurvive>  examClassGradeSurvives;
}
