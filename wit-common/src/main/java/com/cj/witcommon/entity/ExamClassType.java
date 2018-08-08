package com.cj.witcommon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 考试的班级类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamClassType {
    private long classTypeId;
    private String classType;

    //考试的班级层次
    private List<ExamClassLevel> classLevels;
}
