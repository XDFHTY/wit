package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 教学档案
 * 考试 - 班级 - 课程
 * Created by XD on 2018/6/13.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamClassSubject {
    //考试父id
    private Long examParentId;
    //考试名称
    private String examName;
    //考试时间
    private String examTime;
    //平均分
    private BigDecimal avgScore;
    //最高分
    private BigDecimal maxScore;
    //最低分
    private BigDecimal minScore;
    //同类型排名
    private Integer rank;
}
