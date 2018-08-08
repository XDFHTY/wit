package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 考试-班级-成绩实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamClassScore {


    //学段ID
    private Long classPeriodId;
    //学段
    private String classPeriod;
    //届次
    private String thetime;
    //班级ID
    private Long classId;
    //班级
    private String className;
    //课程ID
    private Long subjectId;
    //课程
    private String subjectName;
    //实考人数
    private Integer actuallyCome;
    //缺考人数
    private Integer notCome;
    //平均分
    private BigDecimal avgScore;
    //最低分
    private BigDecimal minScore;
    //最高分
    private BigDecimal maxScore;
    //考试年级排名
    private Integer examSort;
}
