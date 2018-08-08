package com.cj.witscorefind.mapper;

import com.cj.witbasics.entity.SchoolExam;
import com.cj.witbasics.entity.SchoolExamParent;
import com.cj.witbasics.entity.SchoolPeriod;
import com.cj.witbasics.entity.SchoolPeriodClassThetime;
import com.cj.witcommon.entity.SchoolClassInfo;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.entity.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 考试参数设置
 * Created by XD on 2018/6/2.
 */
public interface ExamSettingsMapper {
    //查询考试名称及id
    List<SchoolExamParent> findExamName();

    //模糊查询考试信息
    List findExamByVague(Pager p);

    //模糊查询考试信息 计数
    int findExamByVagueTotal(Pager p);

    //查询这次考试的班级和课程
    List<SchoolClassInfo> findExamClass(Pager p);

    //查询这次考试的班级和课程  计数
    int findExamClassTotal(Pager p);

    //根据id 查询 examId classTypeId subjectId
    Map<String,Object> findClassInfo(SchoolExamGrade seg);

    //插入参数基础信息(school_exam_grade表)  主键返回
    int insertExamGrade(SchoolExamGrade seg);

    ////插入grade信息
    int insertGrade(Grade grade);

    //查重
    SchoolExamGrade findSchoolExamGrade(@Param("e") ExamSettingReq e,@Param("classId")Long classId);

    //更新（school_exam_grade表）
    int updateSchoolExamGrade(SchoolExamGrade schoolExamGrade);

    //根据id查询grade表信息
    List<Grade> findGradeById(SchoolExamGrade schoolExamGrade);

    //更新grade表
    void updateGrade(Grade grade);

    //根据id删除grade表信息
    void deleteGradeById(SchoolExamGrade schoolExamGrade);

    //总分设置，添加grade表信息
    void insertGradeByMap(Map map);

    //总分参数设置查重
    SchoolExamGrade findExamByTotal(ExamSettingReq e);

    //查询总分 档次信息
    List<Grade> findGradeByTotal(ExamSettingReq e);

    //查询这次考试的学段和届次
    List<PeriodThetime> findThetimeByExam(ExamSettingReq e);

    //班级总分任务数 查重
    SchoolExamGrade findExamByClass(@Param("e") ExamSettingReq e,@Param("classId")Long classId);

    //班级单科分数线查重
    SchoolExamGrade findExamByScore(SchoolExamGrade schoolExamGrade);

    //根据考试名称查询考试父节点信息
    SchoolExamParent findExamParentByName(String examName);



    //根据学段名称查询学段信息
    SchoolPeriod findPeriodByName(@Param("periodName") String periodName,@Param("schoolId") Long schoolId);

    //查询学段下是否包含此届次
    SchoolPeriodClassThetime findThetimeByPeriodId(@Param("periodId") Long periodId,@Param("schoolId") Long schoolId,
                                                   @Param("thetime") String thetime);

    //查询本次考试当前学段是否参加
    SchoolExam findExamBySubName(@Param("periodId") Long periodId,@Param("thetime")  String thetime,
                                 @Param("schoolId") Long schoolId, @Param("examParentId") Long examParentId);

    //根据班号查询班级id
    Integer findClassIdByNum(ImportExamSubInfo info);

    //查询本次考试这个班级 考没有考这门课程
    Long findExamIdByClassId(ImportExamSubInfo info);

    //导入单科参数设置查重（school_exam_grade表）
    SchoolExamGrade findSchoolExamGradeImport(ImportExamSubInfo info);

    //查询classTypeId  subjectId
    Map<String,Object> findClassIdAndSubjectId(ImportExamSubInfo info);

    //添加grade表
    int insertGradeByImportMc(ImportExamSubInfo info);

    //添加grade表  分数信息
    void insertGradeByImportScore(ImportExamSubInfo info);

    //grade表查重
    Long findGrade(ImportExamSubInfo info);

    //查询exam_grade_id表 主键
    Long findExamGradeId(ImportExamSubInfo info);

    //判断班级类型   获取班级类型id
    Integer findClassTypeIdByName(@Param("schoolId") Long schoolId, @Param("classType") String classType);

    //总分查重
    SchoolExamGrade findExamByTotalImpl(ImportExamTotalInfo importExamTotalInfo);

    //添加grade表  名次信息
    void insertGradeByImportTotalMc(ImportExamTotalInfo info);

    //添加grade表  分数信息
    void insertGradeByImportTotalScore(ImportExamTotalInfo info);

    //根据班号查询班级id
    Integer findClassIdByTotal(ImportExamClassTotalTask info);

    //获取班级类型id
    Integer findClassTypeIdByClassId(@Param("schoolId") Long schoolId, @Param("classId") Integer classId);

    //查询这个班级的年级总分信息
    List<Grade> findGradeByTotalImport(Map map);

    //根据类型id 查询类型名称
    String findClassTypeNameById(@Param("schoolId") Long schoolId, @Param("classTypeId") Integer classTypeId);

    //班级总分任务数 查重
    SchoolExamGrade findExamByClassImport(Map map);

    //插入grade信息
    int insertGradeImport(ImportExamClassTotalTask info);

    //判断这次考试有没有这个课程  获取课程id
    Long findSubjectIdByName(ImportExamOneSubInfo info);
}
