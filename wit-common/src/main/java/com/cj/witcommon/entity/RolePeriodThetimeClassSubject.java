package com.cj.witcommon.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户角色管理的学段-届次-班级-课程
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePeriodThetimeClassSubject {

    private Long periodId;
    private String thetime;
    private Long classTypeId;
    private Long classLevelId;

    private Long classId;
    private Long subjectId;

}
