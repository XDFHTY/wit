package com.cj.witscorefind.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 考试-届次 档次参数
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamThetimeGrade {

    //考试ID
    private long examParentId;

    //学段ID
    private long periodId;

    //届次
    private String thetime;

    //班级ID
    private long classId;

    //课程ID
    private long subjectId;

    //档次设置类型(1-总分，2-班级，3-科目)
    private String sgGradeType;

    //优势分数线（班级/年级）
    private BigDecimal advantageScore;

    //档次设置类型（1-分数，2名次）
    private String gGradeType;

    //档次名称
    private String gradeName;

    //最大值
    private int numMax;

    //最小值
    private int numMin;
}
