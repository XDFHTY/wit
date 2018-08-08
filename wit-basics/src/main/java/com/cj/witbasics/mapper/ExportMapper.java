package com.cj.witbasics.mapper;

import com.cj.witbasics.entity.AdminInfo;
import com.cj.witbasics.entity.AdminInfoExport;
import com.cj.witbasics.entity.AdminRoleExport;
import com.cj.witbasics.entity.StudentExport;

import java.util.List;

/**
 * 人事信息导出mapper
 * Created by XD on 2018/5/19.
 */
public interface ExportMapper {
    /**
     * 查询所有人事信息
     * @return
     */
    List<AdminInfoExport> selectAdminInfo();

    /**
     * 查询所有角色信息
     * @return
     */
    List<AdminRoleExport> selectAdminRole();

    /**
     * 查询所有学生信息
     * @return
     */
    List<StudentExport> selectStudentOsaas();
}
