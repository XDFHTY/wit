package com.cj.witbasics.controller;

import com.cj.witbasics.entity.Admin;
import com.cj.witbasics.service.AdminInfoService;
import com.cj.witcommon.aop.Log;
import com.cj.witcommon.entity.ApiResult;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * 人事信息导出
 * Created by XD on 2018/5/19.
 */
@RestController
@RequestMapping("/api/v1/basic")
@Api(tags = {"基础信息导出"})
public class AdminInfoController {

    @Autowired
    private AdminInfoService adminInfoService;


    /**
     * 人事信息导出
     * @param response
     * @return
     */
    @ApiOperation(value = "人事导出信息(全部导出)", notes = "成功/失败")
    @GetMapping("/exportAdminInfo")
    @Log(name = "基础信息导出 ==> 导出人事信息(选择导出)")
    public ApiResult exportAdminInfo(
            HttpServletResponse response){
        //TODO:获取导出路径
        //TODO:获取输入文件名
        //用什么软件打开
        response.setHeader("content-Type", "application/vnd.ms-excel");
        //下载文件的默认名字
        try {
            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode("adminInfo.xlsx", "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiResult apiResult = this.adminInfoService.exportAdminInfo(response);
        return apiResult;
    }

    /**
     * 学生信息导出
     * @param response
     * @return
     */
    @ApiOperation(value = "学生导出信息(全部导出)", notes = "成功/失败")
    @GetMapping("/exportStudentInfo")
    @Log(name = "基础信息导出 ==> 导出学生信息(选择导出)")
    public ApiResult exportStudentOsaas(
            HttpServletResponse response){
        //TODO:获取导出路径
        //TODO:获取输入文件名
        //用什么软件打开
        response.setHeader("content-Type", "application/vnd.ms-excel");
        //下载文件的默认名字
        try {
            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode("StudentOsaas.xlsx", "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiResult apiResult = this.adminInfoService.exportStudentOsaas(response);
        return apiResult;
    }



//    /**
//     * 导出角色信息
//     */
//    @ApiOperation(value = "角色导出信息(全部导出)", notes = "成功/失败")
//    @GetMapping("/exportAdminRole")
//    @Log(name = "角色管理 ==> 导出信息(全部导出)")
//    public ApiResult exportAdminRole(
//            HttpServletResponse response){
//        //用什么软件打开
//        response.setHeader("content-Type", "application/vnd.ms-excel");
//        //下载文件的默认名字
//        try {
//            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode("adminRoleInfo.xlsx", "utf-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        ApiResult apiResult = this.adminInfoService.exportAdminRole(response);
//        return apiResult;
//    }



}
