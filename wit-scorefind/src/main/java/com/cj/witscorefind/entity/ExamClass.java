package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 考试班级
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamClass {

    //考试父节点ID
    private long examParentId;

    //学段ID
    private long periodId;

    //届次
    private String thetime;

    //班级类型ID
    private long classTypeId;


    //班级ID
    private long classId;

    //班号
    private Integer classNumber;

    //班级类型
    private String classType;

    //班级层次
    private String classLevel;

    //班主任名
    private String classHeadmaster;

    //学生成绩集合
    private List<ClassStudentTotalScore> classStudentTotalScores;

    //班级成活率信息
    List<ExamClassGrade>  examClassGrades;
}
