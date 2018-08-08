package com.cj.witscorefind.service;

import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.entity.ShortlistedScore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 临界生统计
 * Created by XD on 2018/6/12.
 */
public interface CriticalRawService {
    //统计临界生
    ApiResult findCriticalRaw(Pager p);


    //导出
    ApiResult criticalExport(HttpServletResponse response, Pager p, HttpServletRequest request);
}
