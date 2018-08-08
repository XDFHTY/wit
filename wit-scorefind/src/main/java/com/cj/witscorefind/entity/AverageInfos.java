package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 平均分返回
 * Created by XD on 2018/6/1.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AverageInfos {
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

    //课程信息
    private List<AverageRsp> averageRspList;

    //总分信息
    private AverageRsp averageTotal;
}
