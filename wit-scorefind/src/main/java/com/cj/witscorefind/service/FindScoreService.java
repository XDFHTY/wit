package com.cj.witscorefind.service;

import com.cj.witbasics.entity.Grade;
import com.cj.witbasics.entity.PeriodDirectorThetime;
import com.cj.witbasics.entity.SchoolExamParent;
import com.cj.witbasics.entity.SchoolPeriodClassThetime;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.entity.ExamClassScore;
import com.cj.witscorefind.entity.SchoolExamParents;
import com.cj.witscorefind.entity.SubjectAvgScore;
import org.apache.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FindScoreService {



    /*
     * 查询考试-班级-成绩 统计
     */
    public List<ExamClassScore> findExamClassScoreCount(HttpSession session, Map map);

    /**
     * 本次考试总分成绩分析
     */
    public Pager findExamClassTotalScore(HttpSession session, Pager pager);

    /**
     * 班级学生姓名、学号、总分成绩、班级排名、班级进步名次（退步用负数表示）、年级排名、年级进步名次（退步用负数表示）、学科成绩、档次（A、B、C档）
     */
    public Pager findExamClassSubjectScore(HttpSession session, Pager pager);


    /**
     * 关注学生统计
     */
    public Map findFollowStudents(long newExamParentId,long oldExamParentId,long classId,int followRank);

    /**
     * 优势学科统计(班级)
     */
    public List<SubjectAvgScore> findClassSuperioritySubject(long examParentId,long periodId,String thetime,long classId);

    /**
     * 优势学科统计(学生)
     */
    public Object findStudentSuperioritySubject(long examParentId,long periodId,String thetime,String registerNumber);



    /**
     * 查询此学生参加过并且有成绩的考试
     */
    public List<SchoolExamParents> findAllExamByClassOrStudent(Long classId , String registerNumber);

    /**
     * 历次考试均分趋势图：折线图，班级均分与年级均分对比(班级总分)
     */
    public Map findAllExamClassTotalScoreChart(List<Long> examParentIds , Long classId);

    /**
     * 历次考试均分趋势图：折线图，班级均分与年级均分对比(班级各科)
     */
    public Map findAllExamClassSubjectScoreChart(List<Long> examParentIds , Long classId);


    /**
     * 历次考试班级-学生 各科成绩对比
     */

    public Map findAllExamStudentSubjectScoreChart(List<Long> examParentIds , String registerNumber);


    /**
     * 历次考试班级-学生 总分成绩对比
     */

    public Map findAllExamStudentTotalScoreChart(List<Long> examParentIds , String registerNumber);


    /**
     * 历次年级排名走势图
     */

    public Map findAllExamStudentYearRankChart(List<Long> examParentIds , String registerNumber);
    /**
     * =====================================导出=========================================
     */

    //成绩单导出
    public ApiResult exportExamReport(HttpSession session, HttpServletResponse response, Pager pager) throws Exception;

    //关注学生导出
    public void exportFollowStudents(HttpSession session,HttpServletResponse response,
                                     long newExamParentId,long oldExamParentId,long classId,int followRank) throws Exception;


    /**
     * =============================================================================
     */

//    /**
//     * 不同的角色,查询不同的成绩
//     * @return
//     */
//    ApiResult findScoreForDifferentRole(HttpServletRequest request);
//
//
//    /**
//     * 档次添加
//     */
//    ApiResult addGradeLevelInfo(Grade info);
//
//    /**
//     * 查看分数
//     */
//    List<Map> findScoreDetils(Long classId, Long subjectId);
//
//    /**
//     * 成绩导出
//     */
//    void exportScoreInfo(List<Long> classIdList, HttpServletResponse response);



}
