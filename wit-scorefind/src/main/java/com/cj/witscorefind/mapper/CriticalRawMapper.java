package com.cj.witscorefind.mapper;

import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.entity.ShortlistedResp;
import com.cj.witscorefind.entity.ShortlistedScore;

import java.util.List;

/**
 * 临界生统计
 * Created by XD on 2018/6/12.
 */
public interface CriticalRawMapper {



    //查询各班每个人的总分
    List<ShortlistedScore> findTotalScores(Pager pager);

    ////查询各班、各科、各班总分 各档入围分数线
    List<ShortlistedResp> findFindShortlisted(Pager pager);

    //查询各班各学科每个人的成绩
    List<ShortlistedScore> findScores(Pager pager);

    //查询各班信息+总分信息 各档入围分数线
    List<ShortlistedResp> findFindShortlistedByTotal(Pager pager);
}
