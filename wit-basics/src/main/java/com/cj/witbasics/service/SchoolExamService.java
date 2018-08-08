package com.cj.witbasics.service;


import com.cj.witbasics.entity.SchoolExam;
import com.cj.witbasics.entity.SchoolExamParent;
import com.cj.witcommon.entity.*;
import com.cj.witcommon.utils.entity.other.Pager;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public interface SchoolExamService {


    /**
     * 查询届次
     */
    List<Map> findAllGradeName(Long schoolId);


    /**
     * 返回班级信息
     */
    List findAllUnderGradeClass(List<PeriodAndGrade> param);


    /**
     * 返回班级下的科目
     */
    List findAllSubjectInfo(List<Long> classId);


    /**
     * 新增考试
     */
    ApiResult addSchoolExamInfo(ExamParam examInfo, Long adminId);

    /**
     * 查询科目名称
     */
    List<Map> findExamName(Long schoolId);

    /**
     * 模糊查询考试信息
     */
    Pager findExamOfVague(String examName, String vague, Pager pager);


    /**
     * ===================================================================
     * 根据届次查询考试集合
     */

    public List<SchoolExam> findAllSchoolExamByThetime(String thetime);

    /**
     * 模糊查询考试集合
     */
    public List<SchoolExamParent> findAllSchoolExamParentByParameter(Pager p);

    /**
     * 根据考试父节点ID查询此次考试所有的届次
     */
    public List<ExamPeriod> findAllSchoolExamThetimeBySchoolExamParent(Long examParentId,HttpSession session);

    /**
     * 根据考试父节点ID和考试届次和学段ID 查询此次考试的所有班级及课程信息
     */
    public List<ExamClassPeriod> findAllSchoolExamClassByExamParentIdAndThetime(Map map);

    /**
     * 根据考试父节点ID和考试届次和学段ID 查询此次考试的所有班级及课程信息
     */
    public List<ExamClassPeriod> findAllSchoolExamThetimeAndSubjectByExamParentIdAndThetime(Map map);


    //根据考试父ID、学段、届次、班级类型查询班级层次 查询班级、课程
    public List<ExamClass> findClassAndSubjectByCondition(Map map,HttpSession session);

    //分页查询考试列表信息
    public Pager findAllExamMsg(Pager pager);




}
