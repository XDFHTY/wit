package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 成活率档次
 * Created by XD on 2018/6/14.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurviveGrade {
    //档次名称
    private String gradeName;

    //市内成活率
    private String inCity;
    //市内学生学籍号集合
    private List<String> inCityNumList;

    //市外成活率
    private String outCity;
    //市外学生学籍号集合
    private List<String> outCityNumList;

    //本校升入成活率
    private String theSchool;
    //本校升入学生学籍号集合
    private List<String> theSchoolCityNumList;

    //临界生成活率
    private String criticalRaw;
    //临界生学生学籍号集合
    private List<String> criticalRawCityNumList;
}
