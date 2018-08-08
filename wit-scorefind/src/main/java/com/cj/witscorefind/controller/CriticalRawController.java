package com.cj.witscorefind.controller;

import com.cj.witcommon.aop.Log;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.service.CriticalRawService;
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
 * 临界生统计
 * Created by XD on 2018/6/12.
 */
@Api(tags = "临界生统计")
@RestController
@RequestMapping("/api/v1/critical")
public class CriticalRawController {
    @Autowired
    private CriticalRawService criticalRawService;

    /**
     *  功能描述：临界生统计 可输入线上线下分数，统计各班，年级  各档人数
     *  参数：必传：考试父id、学段id、届次
     *  课程id(可复选)、班级id(可复选)、班级层次id、班级类型id 线上线下分数
     *  返回：
     *  时间：16
     */
    @ApiOperation("统计各班各科年级临界生人数")
    @PostMapping("/findCriticalRaw")
    @Log(name = "成绩管理 ==> 临界生统计")
    public ApiResult findCriticalRaw(
            @ApiParam(name = "p", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
                    "  periodId=学段id（必传）,    thetime=届次（必传）,    curriculumIdList=课程id集合(必传)," +
                    "classTypeId=班级类型id（必传） classLevelId=班级层次id（必传）   classIdList=班级id集合（必传）" +
                    "offlineScore=线下分数（必传）    onlineScore=线上分数（必传）" +
                    "返回结果：集合最后一条是年级情况"
                    ,required = true)
            @RequestBody Pager p, HttpServletRequest request){

        ApiResult apiResult = criticalRawService.findCriticalRaw(p);
        return apiResult;
    }


    /**
     *  功能描述：根据查询的临界生统计 导出EXCEL
     *  参数：必传：考试父id、学段id、届次
     *       非必传：课程id(可复选)、班级id(可复选)、班级层次id、班级类型id
     *  返回：
     *  时间：8
     */
    @ApiOperation(value = "导出临界生信息(选择导出)", notes = "成功/失败")
    @PostMapping("/criticalExport")
    @Log(name = "成绩管理 ==> 导出临界生统计信息")
    public ApiResult criticalExport( @ApiParam(name = "p", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
            "  periodId=学段id（必传）,    thetime=届次（必传）,    curriculumIdList=课程id集合(必传)," +
            "classTypeId=班级类型id（必传）   classIdList=班级id集合（必传）" +
            "offlineScore=线下分数（必传）    onlineScore=线上分数（必传）" +
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
        ApiResult apiResult = this.criticalRawService.criticalExport(response,p,request);
        return apiResult;
    }
}
