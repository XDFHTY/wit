package com.cj.witscorefind.service;

import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.entity.ExamSettingReq;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 * 考试参数设置
 * Created by XD on 2018/6/2.
 */
public interface ExamSettingsService {
    //查询所有考试名称和id
    ApiResult findExamName();

    //查询所有考试信息（模糊条件）
    ApiResult findExamByVague(Pager p);

    //查询这次考试的班级和考试课程
    ApiResult findExamClass(Pager p);

    //各班单科参数设置
    ApiResult addParametersBySubject(ExamSettingReq e, HttpServletRequest request);

    //总分参数设置
    ApiResult addParametersByTotal(ExamSettingReq e, HttpServletRequest request);

    //功能描述：按照年级（分类型）查询总分档次设置信息
    ApiResult findGradeByTotal(ExamSettingReq e, HttpServletRequest request);

    //查询这次考试的学段和届次
    ApiResult findThetimeByExam(ExamSettingReq e, HttpServletRequest request);

    //班级总分各档任务数设置
    ApiResult addParametersByTask(ExamSettingReq e, HttpServletRequest request);

    //设置年级单科分数线
    ApiResult addParametersScore(ExamSettingReq e, HttpServletRequest request);

    //考试参数导入
    ApiResult examImportInfo(MultipartFile file, InputStream in, Long operatorId);
}
