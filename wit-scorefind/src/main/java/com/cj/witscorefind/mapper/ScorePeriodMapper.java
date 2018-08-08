package com.cj.witscorefind.mapper;

import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.entity.ShortlistedResp;
import com.cj.witscorefind.entity.ShortlistedScore;

import java.util.List;

/**
 * 分数分段统计
 * Created by XD on 2018/6/12.
 */
public interface ScorePeriodMapper {


    //查询各班每个人的总分
    List<ShortlistedScore> findTotalScores(Pager pager);

    //查询各班单学科每个人的成绩
    List<ShortlistedScore> findScores(Pager pager);
}
