package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 考试总分平均分   按考试父id封装
 * Created by XD on 2018/6/28.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamAvgTotalScoreList {
    //考试父节点ID(班级-学生)
    private long examParentId;

    //考试名称
    private String examName;

    //考试总分平均分集合
    List<ExamAvgTotalScore> examAvgTotalScoreList;
}
