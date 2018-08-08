package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 考试缺考学生 实体类
 * Created by XD on 2018/6/6.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamCome {
    //班级ID
    private Long classId;
    //缺考人数
    private Integer notCome;
    //缺考学生id集合
    private List<String> registerNumbers;
    //总人数
    private Integer total;
    //实考人数
    private Integer actuallyCome;
    //课程ID
    private Long subjectId;

}
