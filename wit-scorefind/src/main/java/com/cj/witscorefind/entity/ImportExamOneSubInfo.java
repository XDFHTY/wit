package com.cj.witscorefind.entity;

import com.cj.witcommon.utils.excle.IsNeeded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 导入， 单科优势分数线设置  详细信息
 * Created by XD on 2018/6/19.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportExamOneSubInfo {

    @IsNeeded
    //课程名称
    private String subjectName;

    @IsNeeded
    //优势分数线
    private BigDecimal advantageScore;


    //考试父id
    private Long examParentId;

    //档次id
    private Long examGradeId;

    //考试id
    private Long examId;

    //学校id
    private Long schoolId;
    //学段id
    private Long periodId;
    //届次
    private String thetime;

    //档次类型
    private String gradeType;

    //班级id
    private Integer classId;




    //难度系数
    private String degree;

}
