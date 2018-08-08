package com.cj.witcommon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 考试的届次
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamThetime {
    private String thetime;
    //考试的班级类型
    private List<ExamClassType> classTypes;
}
