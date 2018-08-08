package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 入围统计 返回实体类
 * Created by XD on 2018/6/8.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortlistedResp {
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
    private Integer classLevelId;
    //班级层次
    private String classLevel;
    //班级类型id
    private int classTypeId;
    //班级类型
    private String classType;
    //班主任名
    private String classHeadmaster;

    //各班各科各档任务数信息
    List<Shortlisted> shortlistedList;

    //各班总分各档任务数信息
    List<GradeShort> totalList;

    //分数占比  单科信息
    private Shortlisted shortlisted;
}
