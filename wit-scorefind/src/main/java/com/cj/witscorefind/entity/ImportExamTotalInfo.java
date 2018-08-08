package com.cj.witscorefind.entity;

import com.cj.witcommon.utils.excle.IsNeeded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 导入， 总分设置详细信息
 * Created by XD on 2018/6/19.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportExamTotalInfo {

    @IsNeeded
    //名次档次名称
    private String mcName;

    @IsNeeded
    //最高名次
    private Integer maxMc;

    @IsNeeded
    //最低名次
    private Integer minMc;

    @IsNeeded
    //分数档次名称
    private String scoreName;

    @IsNeeded
    //最高分
    private BigDecimal maxScore;

    @IsNeeded
    //最低分
    private BigDecimal minScore;

    @IsNeeded
    //总分分数线
    private BigDecimal score;


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

    //课程名称
    private String subjectName;

    //优势分数线（单科）
    private BigDecimal advantageScore;

    //难度系数
    private String degree;

    //班级类型Id
    private Integer classTypeId;

}
