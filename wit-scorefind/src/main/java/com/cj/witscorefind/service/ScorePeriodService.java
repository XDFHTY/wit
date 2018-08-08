package com.cj.witscorefind.service;

import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 分数分段统计
 * Created by XD on 2018/6/12.
 */
public interface ScorePeriodService {


    //分数分段统计
    ApiResult findScorePeriod(Pager p);

    //导出
    ApiResult scorePeriodExport(HttpServletResponse response, Pager p, HttpServletRequest request);
}
