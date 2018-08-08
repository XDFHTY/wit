package com.cj.witscorefind.entity;

import com.cj.witbasics.entity.StudentScore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 课程 下 的分数集合
 * Created by XD on 2018/6/8.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubScore {
    //课程id
    private Long subjectId;
    //课程名称
    private String subName;
    //分数集合
    private List<StudentScore> scores;
}
