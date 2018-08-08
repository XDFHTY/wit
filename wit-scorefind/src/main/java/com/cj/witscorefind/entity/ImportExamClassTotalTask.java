package com.cj.witscorefind.entity;

import com.cj.witcommon.utils.excle.IsNeeded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 导入   班级总分任务数映射
 * Created by XD on 2018/6/20.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportExamClassTotalTask {
    @IsNeeded
    //班号
    private Integer classNumber;


    @IsNeeded
    //分数档次名称
    private String scoreName;

    @IsNeeded
    //任务数
    private BigDecimal task;


    //学校id
    private Long schoolId;
    //学段id
    private Long periodId;
    //届次
    private String thetime;
    //考试父id
    private Long examParentId;
    //班级id
    private Integer classId;

    //最高分
    private BigDecimal maxScore;
    //最低分
    private BigDecimal minScore;
    //单科分数线
    private BigDecimal score;
    //档次id
    private Long examGradeId;
}
