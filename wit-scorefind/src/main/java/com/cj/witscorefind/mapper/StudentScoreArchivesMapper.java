package com.cj.witscorefind.mapper;

import com.cj.witscorefind.entity.SchoolExamParents;

import java.util.List;
import java.util.Map;

/**
 * 学生成绩档案
 */
public interface StudentScoreArchivesMapper {


    //查询学生历次考试总分成绩
    public List<SchoolExamParents> findExamsStudentTotalScore(Map map);

    //查询学生历次考试各科成绩
    public List<SchoolExamParents> findExamsStudentSubjectScore(Map map);


    //根据学籍号查询学生姓名
    public String findFullNameByRegisterNumber(String registerNumber);

    //查询该生 学段 班级
    Map<String,String> findPeriodNameAndClassName(Map map1);
}
