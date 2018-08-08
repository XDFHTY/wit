package com.cj.witcommon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 考试的班级层次
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamClassLevel {
    private long levelId;
    private String levelName;
}
