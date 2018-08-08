package com.cj.witcommon.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;


/**
 * 组合类：
 * 班级信息
 * 返回差查询的班级信息
 */

@ToString
@Setter
@Getter
public class SchoolClassInfo {

    private Long schoolId;
    private Long classId;
    private String className; //班级名称
    private Long classHeadmasterId; //班主任Id
    private String classHeadmaster; //班主任名字
    private int classNumber; //班号
    private String classAbbreviation; //班级简称
    private String classType; //班级类型
    private Integer classTypeId;
    private String classLevel; //班级层次
    private Integer classLevelId;
    private String classYear; //入学时间
    private Date thetime; //届次
    private String classCampus;
    private int classPeriodId; //学段ID
    private String classPeriod; //学段ID

//    private SubjectForTea subject;
    private List<SubjectForTea> subject; //科目-教师
}