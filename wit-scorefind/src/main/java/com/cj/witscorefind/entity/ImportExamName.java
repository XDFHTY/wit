package com.cj.witscorefind.entity;

import com.cj.witcommon.utils.excle.IsNeeded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 导入， 考试名称映射
 * Created by XD on 2018/6/19.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportExamName {
    @IsNeeded
    //考试名称
    private String examName;

    @IsNeeded
    //学段名称
    private String periodName;

    @IsNeeded
    //届次
    private String thetime;
}