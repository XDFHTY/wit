package com.cj.witscorefind.entity;

import com.cj.witbasics.entity.StudentScore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 各班各学科每个人的成绩
 * Created by XD on 2018/6/8.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortlistedScore {

    //班级ID
    private Long classId;
    //班号
    private Integer classNumber;
    //课程集合
    private List<SubScore> subScores;
    //总分集合
    private List<StudentScore> scores;
}
