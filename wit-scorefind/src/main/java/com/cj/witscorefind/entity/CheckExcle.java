package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 校验excle导入参数实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckExcle {
    //考试名称
    private String examName;
    //学段名称
    private String periodName;
    //届次
    private String thetime;

    //模板编号
    private Integer modelNumber;
}
