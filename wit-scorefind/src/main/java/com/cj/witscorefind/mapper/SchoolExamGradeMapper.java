package com.cj.witscorefind.mapper;

import com.cj.witbasics.entity.PeriodDirectorThetime;
import com.cj.witbasics.entity.SchoolExamGrade;
import com.cj.witbasics.entity.SchoolPeriodClassThetime;
import com.cj.witcommon.utils.entity.other.Pager;

import java.util.List;
import java.util.Map;

public interface SchoolExamGradeMapper {
    /**
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long examGradeId);

    /**
     *
     * @mbggenerated
     */
    int insert(SchoolExamGrade record);

    /**
     *
     * @mbggenerated
     */
    int insertSelective(SchoolExamGrade record);

    /**
     *
     * @mbggenerated
     */
    SchoolExamGrade selectByPrimaryKey(Long examGradeId);

    /**
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(SchoolExamGrade record);

    /**
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(SchoolExamGrade record);

    //查询年级主任信息
    public PeriodDirectorThetime findPeriodDirectorThetimeByAdminId(Long adminId);

    //查询班级主任信息
    public SchoolPeriodClassThetime findSchoolPeriodClassThetimeByAdminId(Long adminId);

    //查询考试参数设置总条数
    public Integer findScoreGradeTotal(Pager p);

    //查询考试参数设置
    public List<SchoolExamGrade> findScoreGrade(Pager p);

    //判断这场 考试ID-课程 参数是否已存在，存在则禁止插入数据
    public SchoolExamGrade findSchoolExamGradeByExamSubject(SchoolExamGrade schoolExamGrade);



}