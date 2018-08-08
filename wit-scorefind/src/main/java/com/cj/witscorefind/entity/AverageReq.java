package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 接收查询平均分信息的实体类
 * Created by XD on 2018/5/23.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AverageReq {
    //考试父节点id
    private Long examParentId;
    //学段id
    private Long periodId;
    //届次
    private String thetime;
    //课程id
    private List<Long> curriculumIdList;
    //班级id
    private List<Long> classIdList;
    //班级层次ID
    private Integer classLevelId;
    //班级类型id
    private Integer classTypeId;
}
