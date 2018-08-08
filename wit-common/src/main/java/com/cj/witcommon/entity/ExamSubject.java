package com.cj.witcommon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 2.根据考试、学段、届次、班级类型、班级层次筛选的 课程
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamSubject {

    private Long examSubjectId;
    private String examSubject;
}
