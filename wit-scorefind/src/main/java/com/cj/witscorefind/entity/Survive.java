package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 成活率
 * Created by XD on 2018/6/14.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Survive {
    //班级ID
    private Long classId;
    //班号
    private Integer classNumber;
    //班级类型
    private String classType;
    //班级层次
    private String classLevel;
    //班主任名
    private String classHeadmaster;

    //各个成活率档次
    private List<SurviveGrade> surviveGradeList;
}
