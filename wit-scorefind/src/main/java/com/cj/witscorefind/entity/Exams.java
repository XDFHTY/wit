package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 历次考试得0分的学生学籍号
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exams {

    //考试父节点ID
    private long examParentId;

    //课程Id集合
    private List<SubjectIdByScoreEq0> subjectIdByScoreEq0s;




}
