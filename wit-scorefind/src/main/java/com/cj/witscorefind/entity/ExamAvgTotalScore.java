package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 考试总分平均分
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamAvgTotalScore {

    //考试父节点ID(班级-学生)
    private long examParentId;

    //考试名称
    private String examName;

    //考试时间(班级-学生)
    private Date examTime;
    //届次(班级-学生)
    private String thetime;
    //班级ID(班级-学生)
    private long classId;

    //学籍号(学生)
    private String registerNumber;

    //课程ID(学生)
    private long subjectId;

    //课程名称(学生)
    private String subjectName;

    //单科成绩(学生)
    private BigDecimal score;


    //总分平均分(班级-学生)
    private BigDecimal avgTotalScore;

    //年级排名(学生)
    private int yearRank;

    //各科平均成绩(班级-学生)
    private List<ExamAvgTotalScore> examAvgTotalScores;

}
