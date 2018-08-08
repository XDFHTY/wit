package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 历次考试0分的课程-学籍号集合
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectIdByScoreEq0 {

    //课程ID
    private long subjectId;

    //学籍号集合
    private List<RegisterNumberByScoreEq0> registerNumberByScoreEq0s;

}
