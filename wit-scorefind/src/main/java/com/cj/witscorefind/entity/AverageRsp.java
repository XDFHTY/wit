package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 平均分 返回用实体类
 * Created by XD on 2018/5/22.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AverageRsp {
    //考试父ID
    private Long examParentId;
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
    //班号
    private Integer classNumber;
    //班级层次id
    private int classLevelId;
    //班级层次
    private String classLevel;
    //班级类型id
    private int classTypeId;
    //班级类型
    private String classType;
    //班主任名
    private String classHeadmaster;


    //课程ID
    private Long subjectId;
    //课程
    private String subjectName;
    //学科老师
    private String fullName;
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
    //难度系数
    private String degree;
}
