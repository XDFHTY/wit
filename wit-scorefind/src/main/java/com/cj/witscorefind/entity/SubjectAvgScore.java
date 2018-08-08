package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

//此次考试年级单科平均分
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectAvgScore {

    //考试父ID
    private long examParentId;
    //课程ID
    private long subjectId;
    //课程名称
    private String subjectName;
    //单科平均分
    private BigDecimal subjectAvgScore;
}
