package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * 档次下的任务数 贡献率
 * Created by XD on 2018/6/8.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeShort implements Comparator {

    //档次名称
    private String gradeName;

    //单 入围任务数
    private BigDecimal dTask;
    //单 入围人数
    private Integer dShortNum;
    //单 排名
    private Integer dRank;
    //单入围分数线
    private BigDecimal dNumScore;
    //单入围 差异
    private BigDecimal dDifferences;

    //双 入围任务数
    private BigDecimal sTask;
    //双 入围分数线
    private BigDecimal sNumScore;
    //双 入围人数
    private Integer sShortNum;
    //双 排名
    private Integer sRank;
    //双入围 差异
    private BigDecimal sDifferences;

    //入围贡献率
    private String Contribution;

    //总分 差异
    private BigDecimal zfDifferences;
    //总分入围任务数
    private BigDecimal task;
    //总分入围分数线
    private BigDecimal totalScore;
    //总分入围人数
    private Integer shortNum;
    //总分差异排名
    private Integer zfRank;

    //线上人数
    private Integer onlineNum;
    //线下人数
    private Integer offlineNum;


    //各班总分 差异数排名
    @Override
    public int compare(Object o1, Object o2) {
        GradeShort gs1 = (GradeShort) o1;
        GradeShort gs2 = (GradeShort) o2;
        int i = 0;
        if (gs2.getZfDifferences()!=null && gs1.getZfDifferences()!=null){
             i = gs2.getZfDifferences().compareTo(gs1.getZfDifferences());
        }
        return i;
    }
}
