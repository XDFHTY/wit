package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 考试参数设置 实体类
 * Created by XD on 2018/6/2.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSettingReq {
    //考试父节点id
    private Long examParentId;
    //学校id
    private Long schoolId;
    //学段id
    private Long periodId;
    //届次
    private String thetime;
    //班级id 集合
    private List<Long> classIds;
    //课程
    private String subjectName;
    //班级类型
    private Integer classTypeId;
    //难度系数
    private String degree;
    //优势分数线
    private BigDecimal advantageScore;


    //档次
    private List<Map<String,String>> gradeList;

    //任务数
    private List<Map<String,Object>> taskList;
}
