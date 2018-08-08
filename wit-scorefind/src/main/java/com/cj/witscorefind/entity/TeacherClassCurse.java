package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 教学档案  教师 - 班级 - 课程
 * Created by XD on 2018/6/13.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherClassCurse {
    //教师姓名
    private String fullName;
    //教师编号
    private String staffNumber;

    //所教学段id
    private String classPeriodId;
    //所教学段
    private String classPeriod;
    //所教届次
    private String thetime;

    //所教班级id
    private Long classId;
    //所教班级
    private String className;
    //班级类型id
    private Integer classTypeId;

    //班级类型
    private String classType;
    //班级层次id
    private Integer classLevelId;
    //班级层次
    private String classLevel;

    //课程id
    private Long subjectId;
    //课程名称
    private String subjectName;
}
