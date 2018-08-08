package com.cj.witscorefind.entity;

import com.cj.witcommon.utils.excle.IsNeeded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 导入， 班级类型，难度系数 映射
 * Created by XD on 2018/6/19.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportExamClassType {

    //班级类型
    @IsNeeded
    private String classType;

    //难度系数
    @IsNeeded
    private String degree;

}
