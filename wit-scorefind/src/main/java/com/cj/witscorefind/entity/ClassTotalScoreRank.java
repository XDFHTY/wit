package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 班级考试总分排名
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassTotalScoreRank {

    //考试父ID
    private long examParentId;
    //学生姓名
    private String fullName;
    //学籍号
    private String registerNumber;
    //总分
    private BigDecimal totalScore;
    //班级排名
    private int classRank;
    //班级进步名次
    private int classRankRaise;
}
