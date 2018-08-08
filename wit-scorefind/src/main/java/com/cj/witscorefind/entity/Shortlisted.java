package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 课程 - 档次
 * Created by XD on 2018/6/8.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shortlisted {
    //课程ID
    private Long subjectId;
    //课程
    private String subjectName;
    //教师姓名
    private String fullName;
    //档次
    private List<GradeShort> gradeShorts;

    //各个分数分段信息
    private List<ScorePeriod> scorePeriods;

}
