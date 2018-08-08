package com.cj.witscorefind.controller;

import com.cj.witcommon.aop.Log;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.service.CriticalRawService;
import com.cj.witscorefind.service.ScorePeriodService;
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
 * 分数分段统计
 * Created by XD on 2018/6/12.
 */
@Api(tags = "分数分段统计")
@RestController
@RequestMapping("/api/v1/piecewise")
public class ScorePeriodController {

    @Autowired
    private ScorePeriodService scorePeriodService;


    /**
     *  功能描述：分数分段统计，输入各个分数段，查询各班占分数段人数，年级占比
     *  参数：必传：考试父id、学段id、届次
     *  课程id(可复选)、班级id(可复选)、班级层次id、班级类型id 各个分数段
     *  返回：
     *  时间：16
     */
    @ApiOperation("统计各班年级单科分数分段")
    @PostMapping("/findScorePeriod")
    @Log(name = "成绩管理 ==> 分数分段统计")
    public ApiResult findScorePeriod(
            @ApiParam(name = "p", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
                    "  periodId=学段id（必传）,    thetime=届次（必传）,    curriculumId=课程id(必传)," +
                    "classTypeId=班级类型id（必传） classLevelId=班级层次id（必传）   classIdList=班级id集合（必传）" +
                    "scoreList=分数分段集合（必传）    "
                    ,required = true)
            @RequestBody Pager p, HttpServletRequest request){

        ApiResult apiResult = scorePeriodService.findScorePeriod(p);
        return apiResult;
    }









    /**
     *  功能描述：根据查询的分数分段统计 导出EXCEL
     *  参数：必传：考试父id、学段id、届次
     *       非必传：课程id(可复选)、班级id(可复选)、班级层次id、班级类型id
     *  返回：
     *  时间：8
     */
    @ApiOperation(value = "导出分数分段统计(选择导出)", notes = "成功/失败")
    @PostMapping("/scorePeriodExport")
    @Log(name = "成绩管理 ==> 导出分数分段统计信息")
    public ApiResult scorePeriodExport( @ApiParam(name = "p", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
            "  periodId=学段id（必传）,    thetime=届次（必传）,    curriculumId=课程id(必传)," +
            "classTypeId=班级类型id（必传） classLevelId=班级层次id（必传）   classIdList=班级id集合（必传）" +
            "scoreList=分数分段集合（必传）    "
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
        ApiResult apiResult = this.scorePeriodService.scorePeriodExport(response,p,request);
        return apiResult;
    }
}
