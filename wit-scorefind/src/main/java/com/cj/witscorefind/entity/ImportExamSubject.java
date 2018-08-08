package com.cj.witscorefind.entity;

import com.cj.witcommon.utils.excle.IsNeeded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 导入， 考试课程，优势分数线，难度系数 映射
 * Created by XD on 2018/6/19.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportExamSubject {

    //课程名称
    @IsNeeded
    private String subjectName;

    //优势分数线（单科）
    @IsNeeded
    private BigDecimal advantageScore;

    //难度系数
    @IsNeeded
    private String degree;

}
