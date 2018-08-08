package com.cj.witscorefind.mapper;

import com.cj.witbasics.entity.PeriodDirectorThetime;
import com.cj.witbasics.entity.SchoolClass;
import com.cj.witbasics.entity.SchoolClassLevel;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.entity.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AverageMapper {

    //按照考试父id 学段id 届次 查询所有课程的平均分信息
    //List<AverageRsp> findAverageInfo(AverageReq averageReq);

    //按照考试父id 学段id 届次 查询所有课程的平均分信息  分页
    List<List<?>> findAverageInfo(Pager pager);

    //查询平均分信息 统计条数
    Integer findAverageInfoCount(Pager pager);

    //按课程查询年级平均分情况
    List<AverageRsp> findAverageByGrade(Pager pager);

    //根据届次id 学段 层次id 返回班级列表
    List<SchoolClass> findClassByLevel(Map<String, Object> map);

    //查询班级总分情况
    List<AverageRsp> averageTotal(Pager pager);

    //查询总分平均分和计数
    List<List<?>> findTotalAvg(Pager pager);

    //查询年级总分平均分情况
    List<AverageRsp> findTotalAvgByGrade(Pager pager);

    //查询平均分  按班级封装
    List<AverageInfos> findAverageInfos(Pager pager);

    //查询年级平均分的情况
    List<AverageInfos> findAveragesByGrade(Pager pager);

    //年级总分情况(选择课程时)
    List<AverageRsp> findAverageTotalGrade(Pager pager);

    //查询这次考试  总分的缺考学生的id集合
    List<ExamCome> findExamComeByTotal(Pager pager);

    //查询这次考试  各班总分的考试人数
    List<ExamCome> findExamTotal(Pager pager);

    //查询这次考试  年级总分的考试总人数
    Integer findGradeTotal(Pager pager);

    //查询这次考试  年级总分的缺考人数 和 学生id集合
    List<String> findGradeExamNotCome(Pager pager);

    //查询这次考试  各班各科的缺考学生id集合
    List<ExamCome> findSubNotComes(Pager pager);

    //查询这次考试  各班各科的考试总人数
    List<ExamCome> findSubActuallyComes(Pager pager);

    //查询年级各科考试总人数
    List<ExamCome> findAradeSubComes(Pager pager);

    //查询年级各科缺考人数
    List<ExamCome> findAradeSubNotComes(Pager pager);

    //查询每个班的层次名称
    List<SchoolClassLevel> findClassLevel(Pager pager);

    //获取所有课程名称
    String findSubjectName(Integer zfList);
}
