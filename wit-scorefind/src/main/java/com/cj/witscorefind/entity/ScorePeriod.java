package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 分数分段统计
 * Created by XD on 2018/6/12.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScorePeriod {


    //分数段名称
    private String periodName;

    //最大分数
    private BigDecimal maxScore;

    //最小分数
    private BigDecimal minScore;

    //分数段人数
    private Integer periodNum;

    //分数段年级占比
    private String Accounted;

}
