package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 考试参数实体类
 * Created by XD on 2018/6/2.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolExamGrade {
    //考试成绩等级表
    private Long examGradeId;
    //学校id
    private Long schoolId;
    //考试父节点ID
    private Long examParentId;
    //考试ID
    private Long examId;
    //学段ID
    private Long classPeriodId;
    //届次
    private String thetime;
    //班级类型ID
    private Integer classTypeId;
    //班级ID
    private Long classId;
    //考试课程ID
    private Long examSubjectId;
    //考试课程
    private String examSubject;
    //档次设置方式，1-总分，2-班级-单科，3-年级-单科
    private String gradeType;
    //创建人ID
    private Long founderId;
    //创建时间
    private Date createTime;
    //操作员ID
    private Long operatorId;
    //删除时间
    private Date deleteTime;
    //0-已删除，1-正常，默认为1
    private String state;
    //难度系数
    private String degree;
    //优势分数线（单科）
    private BigDecimal advantageScore;
}
