package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 班级学生总分
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassStudentTotalScore {

    //学籍号
    private String registerNumber;

    //总分
    private BigDecimal totalScore;


    //学生来源（区县内-省市内-省市外）
    private String studentsComeIn;

    //原学校名称
    private String originalSchool;


    //档次名称
    private String gradeName;


    //是否临界生 1-是，0-否
    private int criticalScore;
}
