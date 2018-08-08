package com.cj.witscorefind.service;

import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 名次分段统计
 * Created by XD on 2018/6/14.
 */
public interface RankPeriodService {


    //名次分段统计
    ApiResult findRankPeriod(Pager p);

    //导出
    ApiResult rankPeriodExport(HttpServletResponse response, Pager p, HttpServletRequest request);
}
