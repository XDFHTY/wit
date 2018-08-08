package com.cj.witscorefind.service;

import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.entity.AverageReq;
import com.cj.witscorefind.entity.AverageRsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by XD on 2018/5/23.
 */
public interface FindAverageService {
    //按条件查询平均分
    //List<AverageRsp> findAverageInfo(AverageReq averageReq);

    //按条件查询平均分 分页
    ApiResult findAverageInfo(Pager pager, HttpServletRequest request);

    //根据届次id 学段 层次id 返回班级列表
    ApiResult findClassByLevel(Map<String, Object> map);

    //查询同层次各个班级总分平均分
    ApiResult findTotalAvg(Pager p, HttpServletRequest request);

    //查询年级班级平均分，按班级封装
    ApiResult findAverageInfos(Pager p, HttpServletRequest request);

    //平均分导出
    ApiResult AverageExport(HttpServletResponse response,Pager pager,HttpServletRequest request);
}
