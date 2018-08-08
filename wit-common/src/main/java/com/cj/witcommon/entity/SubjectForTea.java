package com.cj.witcommon.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SubjectForTea {

    private Long classId;//班级id
    private Long adminId; //教师ID
    private String fullName; //教师名
    private Long subjectId; //课程ID
    private String subjectName; //课程名
    private String staffNumber;//教职工编号
}