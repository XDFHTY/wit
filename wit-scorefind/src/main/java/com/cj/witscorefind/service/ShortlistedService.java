package com.cj.witscorefind.service;

import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 入围统计
 * Created by XD on 2018/6/8.
 */
public interface ShortlistedService {
    //统计各班各科年级入围信息
    ApiResult findFindShortlisted(Pager p);

    //导出
    ApiResult shortlistedExport(HttpServletResponse response, Pager p, HttpServletRequest request);
}
