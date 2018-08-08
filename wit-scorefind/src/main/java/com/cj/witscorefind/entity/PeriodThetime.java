package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 学段下的届次信息
 * Created by XD on 2018/6/5.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodThetime {
    /**
     * 学校学段信息表
     */
    private Long periodId;
    /**
     * 学段名称
     */
    private String periodName;
    /**
     * 届次
     */
    private List<String> thetimes;
}
