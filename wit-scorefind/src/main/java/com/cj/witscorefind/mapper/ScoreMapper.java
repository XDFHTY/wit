package com.cj.witscorefind.mapper;

import com.cj.witbasics.entity.SchoolExamParent;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.entity.*;

import java.util.List;
import java.util.Map;

public interface ScoreMapper {

    //根据 考试 查询 学段-届次（年级）-班级-课程 成绩统计-排名
    public List<ExamClassScore> findAllGradeClassByExamAndthetime(Map map);

    //查询年级主任学段、届次信息
    public Map findPeriodIdAndThetimeByAdminId(Long adminId);

    //查询学段、届次、班级信息
    public Map findPeriodIdAndThetimeAndClassIdByAdminId(Long adminId);

    //查询课程ID、班级ID集合
    public List<Map> findSubjectIdAndClassIdsByAdminId(Long adminId);

    //分页查询总分考试统计
    public List<List<?>> findExamClassTotalScore(Pager pager);

    //查询学生单科成绩
    public List<ExamSubjectScore> findExamSubjectScore(Pager pager);

    //查询对比考试 总分统计
    public List<ExamClassSubjectScore> findOldExamClassTotalScore(Pager pager);

    //分页查询单科 考试统计
    public List<List<?>> findExamClassSubjectScore(Pager pager);

    //查询对比考试单科 考试统计
    public List<ExamClassSubjectScore> findOldExamClassSubjectScore(Pager pager);

    //查询本次考试档次参数信息
    public List<ExamThetimeGrade> findExamThetimeSubjectGrade(Map map);


    //查询考试班级总分及排名
    public List<ClassTotalScoreRank> findClassTotalScoreRank(Map map);

    //查询此次考试年级-班级 单科平均分
    public List<SubjectAvgScore> findSubjectAvgScore(Map map);

    //查询此学生参加过并且有成绩的考试
    public List<SchoolExamParents> findAllExamByClassOrStudent(Map map);

    //根据classId查询届次-班级类型
    public Map findThetimeAndclassTypeIdByClassId(Long classId);

    //查询得0分的学生学籍号
    public List<Exams> findAllRegisterNumberByScoreEq0(Map map1);

    //查询N次考试总分平均分
    public List<ExamAvgTotalScore> findExamAvgTotalScore(Map map);

    //查询N次考试各科平均分
    public List<ExamAvgTotalScore> findExamAvgSubjectScore(Map map);


    //查询学生班级ID
    public long findClassIdByRegisterNumber(String registerNumber);

    //查询历次考试学生各科成绩
    public List<ExamAvgTotalScore> findExamsSubjectScore(Map map2);

    //查询历次考试学生总分
    public List<ExamAvgTotalScore> findExamTotalScore(Map map2);


     //历次年级排名(同班级类型)走势图
    public List<ExamAvgTotalScore> findAllExamStudentYearRankChart(Map map);


    /**
     * ===================================导出=========================================
     */
    //查询可欧式名称
    public List<String> findExamNames(Map map);
}
