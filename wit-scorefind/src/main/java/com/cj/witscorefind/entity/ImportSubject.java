package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 平均分信息  课程导出实体类
 * Created by XD on 2018/6/22.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportSubject {
    //教师
    private String fullName;
    //实考人数
    private String actuallyCome;
    //缺考人数
    private String notCome;
    //平均分
    private String avgScore;
    //排名
    private String examSort;
    //难度系数
    private String degree;
    //最高分
    private String maxScore;
    //最低分
    private String minScore;
}
