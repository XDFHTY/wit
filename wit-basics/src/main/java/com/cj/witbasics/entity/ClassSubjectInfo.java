package com.cj.witbasics.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassSubjectInfo {
    /**
     * 班级-科目-管理员信息(查询)
     */
    private Long clsSubId;

    /**
     * 班级信息ID
     */
    private Long classId;

    /**
     * 科目信息ID
     */
    private Long subjectId;

    /**
     * 管理员(教师)ID
     */
    private Long adminId;

    private String state;

}