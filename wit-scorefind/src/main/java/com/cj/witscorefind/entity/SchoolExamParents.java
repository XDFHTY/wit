package com.cj.witscorefind.entity;

import com.cj.witbasics.entity.SchoolExamParent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolExamParents {
    /**
     * 考试信息父节点
     */
    private Long examParentId;

    /**
     * 考试名称
     */
    private String examName;

    /**
     * 创建时间
     */
    private Date createTime;
    //学籍号
    private String registerNumber;

    //学段
    private String periodName;

    //班级
    private String className;

    //课程名
    private String subjectName;

    //总分
    private BigDecimal totalScore;

    //各科成绩
    private List<ExamSubjectScore> examSubjectScores;
}
