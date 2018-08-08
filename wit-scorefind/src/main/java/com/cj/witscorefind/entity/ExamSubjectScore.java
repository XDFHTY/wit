package com.cj.witscorefind.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 学生单科成绩
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSubjectScore {

    //学籍号
    private String registerNumber;
    //课程ID
    private long subjectId;
    //课程名
    private String subjectName;
    //分数
    private BigDecimal score;
}
