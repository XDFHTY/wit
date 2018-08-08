package com.cj.witscorefind.service;

import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 教学档案
 * Created by XD on 2018/6/13.
 */
public interface TeachingFileService {
    //根据教职工编号查询所教班级和课程
    ApiResult findTeachingFile(String staffNumber);

    //查询单个班的单科每次考试平均分信息
    ApiResult findExamInfo(Pager pager);

    //导出
    ApiResult teachingFileExport(HttpServletResponse response, Pager p, HttpServletRequest request);
}
