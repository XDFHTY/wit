package com.cj.witcommon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 考试的学段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamPeriod {
    private long classPeriodId;
    private String classPeriod;

    //届次
    private List<ExamThetime> thetimes;

}
