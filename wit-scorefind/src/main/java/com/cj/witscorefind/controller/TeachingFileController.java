package com.cj.witscorefind.controller;

import com.cj.witcommon.aop.Log;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.service.TeachingFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 教学档案
 * Created by XD on 2018/6/13.
 */
@Api(tags = "教学档案")
@RestController
@RequestMapping("/api/v1/teaching")
public class TeachingFileController {
    @Autowired
    private TeachingFileService teachingFileService;


    /**
     *  功能描述：查询单个老师的 所教班级和课程
     *  参数：必传：教职工编号
     *  返回：
     *  时间：3
     */
    @ApiOperation("查询教师所教班级和课程")
    @GetMapping("/findTeachingFile")
    @Log(name = "教学档案 ==> 查询所教班级和课程")
    @ApiImplicitParam(name = "staffNumber",value = "教职工编号",required = true,type = "String")
    public ApiResult findTeachingFile(String staffNumber, HttpServletRequest request){

        ApiResult apiResult = teachingFileService.findTeachingFile(staffNumber);
        return apiResult;
    }

    /**
     *  功能描述：查询单个班的单科每次考试平均分信息
     *  参数：必传：班级id  班级类型id 课程id 学段 届次
     *  返回：
     *  时间：8
     */
    @ApiOperation("查询单个班的单科每次考试平均分信息")
    @PostMapping("/findExamInfo")
    @Log(name = "教学档案 ==> 查询每次考试平均分")
    public ApiResult findExamInfo(
            @ApiParam(name = "p", value = "查询条件=parameters=" +"【 periodId=学段id（必传）," +
                    "    thetime=届次（必传）,    curriculumId=课程id(必传)," +
                    "classTypeId=班级类型id（必传）    classId=班级id（必传）"
                    ,required = true)
            @RequestBody Pager p, HttpServletRequest request){

        ApiResult apiResult = teachingFileService.findExamInfo(p);
        return apiResult;
    }


    /**
     *  功能描述：教师档案 导出EXCEL
     *
     *  返回：
     *  时间：4
     */
    @ApiOperation(value = "导出教学档案(选择导出)", notes = "成功/失败")
    @PostMapping("/teachingFileExport")
    @Log(name = "成绩管理 ==> 导出临界生统计信息")
    public ApiResult teachingFileExport(@ApiParam(name = "p", value = "查询条件=parameters=" +"【 periodId=学段id（必传）," +
            "    thetime=届次（必传）,    curriculumId=课程id(必传)," +
            "classTypeId=班级类型id（必传）    classId=班级id（必传）    name=教师姓名(必传)   adminId=教职工编号(必传)" +
            "     classType=班级类型名称(必传)     className=班级名称(必传)      curriculumName=课程名称(必传)   "
            ,required = true)
                                    @RequestBody Pager p, HttpServletResponse response, HttpServletRequest request){
        //用什么软件打开
        response.setHeader("content-Type", "application/vnd.ms-excel");
        //下载文件的默认名字
        try {
            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode("ShortlistedInfo.xlsx", "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiResult apiResult = this.teachingFileService.teachingFileExport(response,p,request);
        return apiResult;
    }
}
