package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 分数档次数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreGrade {

    //考试父节点ID
    private long examParentId;

    //学段ID
    private long periodId;

    //届次
    private String thetime;

    //班级类型ID
    private long classTypeId;

    //档次名称
    private String gradeName;

    //本档次最高分
    private BigDecimal numMax;

    //本档次最低分
    private BigDecimal numMin;

    //本档次分数线
    private BigDecimal numScore;
}
