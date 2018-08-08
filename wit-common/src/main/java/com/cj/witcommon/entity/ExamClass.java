package com.cj.witcommon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 根据考试、学段、届次、班级类型、班级层次筛选的 班级
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamClass {

    //班级ID
    private long classId;
    //班级名称
    private String className;
    //班级类型ID
    private long classTypeId;
    //班级类型名称
    private String classTypeName;
    //班级层次ID
    private long classLevelId;
    //班级层次名称
    private String classLevelName;

    private List<ExamSubject> subjects;

}
