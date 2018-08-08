package com.cj.witscorefind.controller;

import com.cj.witcommon.aop.Log;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.service.ShortlistedService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 入围统计
 * Created by XD on 2018/6/8.
 */
@Api(tags = "入围统计")
@RestController
@RequestMapping("/api/v1/shortlisted")
public class ShortlistedController {
    @Autowired
    private ShortlistedService shortlistedService;

    /**
     *  功能描述：入围统计 可查看各班 总分 各科 年级情况的入围统计
     *  参数：必传：考试父id、学段id、届次
     *  课程id(可复选)、班级id(可复选)、班级层次id、班级类型id
     *  返回：
     *  时间：16
     */
    @ApiOperation("统计各班各科年级入围信息")
    @PostMapping("/findShortlisted")
    @Log(name = "成绩管理 ==> 入围统计")
    public ApiResult findShortlisted(
            @ApiParam(name = "p", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
                    "  periodId=学段id（必传）,    thetime=届次（必传）,    curriculumIdList=课程id集合(必传)," +
                    "classTypeId=班级类型id（必传） classLevelId=班级层次id（必传）   classIdList=班级id集合（必传）" +
                    "返回结果：集合最后一条是年级情况"
                    ,required = true)
            @RequestBody Pager p, HttpServletRequest request){

        ApiResult apiResult = shortlistedService.findFindShortlisted(p);
        return apiResult;
    }


    /**
     *  功能描述：根据查询的入围统计 导出EXCEL
     *  参数：必传：考试父id、学段id、届次
     *       非必传：课程id(可复选)、班级id(可复选)、班级层次id、班级类型id
     *  返回：
     *  时间：8
     */
    @ApiOperation(value = "导出入围统计信息(选择导出)", notes = "成功/失败")
    @PostMapping("/shortlistedExport")
    @Log(name = "成绩管理 ==> 导出入围统计信息")
    public ApiResult shortlistedExport(@ApiParam(name = "p", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
            "  periodId=学段id（必传）,    thetime=届次（必传）,    curriculumIdList=课程id集合(必传)," +
            "classTypeId=班级类型id（必传） classLevelId=班级层次id（必传）   classIdList=班级id集合（必传）" +
            "返回结果：集合最后一条是年级情况"
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
        ApiResult apiResult = this.shortlistedService.shortlistedExport(response,p,request);
        return apiResult;
    }

}
