package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 考试、班级、课程、成绩 统计
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamClassSubjectScore {

    //学生ID
    private long osaasId;

    //学生姓名
    private String fullName;

    //学籍号
    private String registerNumber;

    //考试父ID
    private long examParentId;



    //学段ID
    private long periodId;

    //届次
    private String thetime;

    //班级ID
    private long classId;

    //班级类型ID
    private long classtypeId;

    //班级类型
    private String classtype;

    //课程ID
    private long subjectId;

    //课程名
    private String subjectName;



    //总分
    private BigDecimal totalScore;

    //年级排名
    private int yearRank;

    //年级进步名次（退步用负数表示）
    private int yearRankRaise;

    //班级排名
    private int classRank;

    //班级进步名次（退步用负数表示）
    private int classRankRaise;


    //课程成绩
    private BigDecimal score;

    //分数档次（A、B、C档）
    private String scoreGrade;

    //名次档次（A、B、C档）
    private String rankGrade;

    //TODO:处理单科成绩-排名
    private List<ExamClassSubjectScore> examClassSubjectScores;
}
