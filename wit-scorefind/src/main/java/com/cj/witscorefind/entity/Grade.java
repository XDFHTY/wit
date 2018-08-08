package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * grade表实体类
 * Created by XD on 2018/6/4.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    //档次信息表
    private Long gradeId;

    //pk 考试等级信息表
    private Long examGradeId;

    //档次名称
    private String gradeName;

    //档次设置类型（1-分数，2名次）
    private String gradeType;

    //最大值
    private Integer numMax;

    //最小值
    private Integer numMin;



    //分数线
    private BigDecimal numScore;

    //任务数
    private BigDecimal numTask;
}
