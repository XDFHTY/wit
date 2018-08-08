package com.cj.witscorefind.mapper;

import com.cj.witbasics.entity.StudentScore;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.entity.ShortlistedResp;
import com.cj.witscorefind.entity.ShortlistedScore;

import java.util.List;

/**
 * 入围统计
 * Created by XD on 2018/6/8.
 */
public interface ShortlistedMapper {
    //查询各班、各科、各班总分 入围信息
    List<ShortlistedResp> findFindShortlisted(Pager pager);

    //查询各班各学科每个人的成绩
    List<ShortlistedScore> findScores(Pager pager);

    //查询各班每个人的总分
    List<ShortlistedScore> findTotalScores(Pager pager);

    //查询该类型下 所有班级id
    List<Long> findClassIdByType(Pager pager);

    //获取该类型下所有的层次id
    List<Integer> findClassLevelIdList(Pager pager);
}
