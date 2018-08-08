package com.cj.witbasics.service;

import com.cj.witcommon.entity.ApiResult;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by XD on 2018/5/19.
 */
public interface AdminInfoService {

    /**
     * 人事信息导出（全部导出）
     */
    ApiResult exportAdminInfo(HttpServletResponse response);

    /**
     * 角色信息导出（全部导出）
     * @param response
     */
    ApiResult exportAdminRole(HttpServletResponse response);

    /**
     * 学生信息导出（全部导出）
     * @param response
     * @return
     */
    ApiResult exportStudentOsaas(HttpServletResponse response);
}
