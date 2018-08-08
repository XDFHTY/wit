package com.cj.witscorefind.controller;

import com.cj.witcommon.aop.Log;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.entity.AverageReq;
import com.cj.witscorefind.service.FindAverageService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 查询平均分
 * Created by XD on 2018/5/22.
 */
@Api(tags = "平均分查询")
@RestController
@RequestMapping("/api/v1/findAverage")
public class FindAverageController {

    @Autowired
    private FindAverageService findAverageService;

    /**
     *  功能描述：不同的班级或者课程 查询不同的平均分
     *  参数：必传：考试父id、学段id、届次
     *       非必传：课程id(可复选)、班级id(可复选)、班级层次id、班级类型id
     *  返回：
     *  时间：6
     */
   /* @ApiOperation("查询平均分")
    @PostMapping("/findAverageInfo")
    @Log(name = "成绩管理 ==> 统计平均分")
    public ApiResult findAverageInfo(
            @ApiParam(name = "averageReq", value = "params（examParentId=考试父节点id," +
                    "  periodId=学段id,    thetime=届次,    curriculumIdList=课程id集合(可不传)," +
                    "    classIdList=班级id集合（可不传）,    classLevelId=班级层次ID(可不传), " +
                    "    classTypeId=班级类型id（可不传））")
            @RequestBody AverageReq averageReq){
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(findAverageService.findAverageInfo(averageReq));
        return apiResult;
    }*/

    /**
     *  功能描述：根据查询的平均分信息 导出EXCEL
     *  参数：必传：考试父id、学段id、届次
     *       非必传：课程id(可复选)、班级id(可复选)、班级层次id、班级类型id
     *  返回：
     *  时间：8
    */
    @ApiOperation(value = "导出平均分信息(选择导出)", notes = "成功/失败")
    @PostMapping("/AverageExport")
    @Log(name = "成绩管理 ==> 导出平均分信息")
    public ApiResult AverageExport(@ApiParam(name = "p", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
            "  periodId=学段id（必传）,    thetime=届次（必传）,    curriculumIdList=课程id集合(必传)," +
            "classTypeId=班级类型id（必传） classLevelId=班级层次id（必传）   classIdList=班级id集合（必传）" +
            "返回结果：集合最后一条是年级情况"
            ,required = true)
                     @RequestBody Pager p, HttpServletResponse response,HttpServletRequest request){
        //用什么软件打开
        response.setHeader("content-Type", "application/vnd.ms-excel");
        //下载文件的默认名字
        try {
            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode("AverageInfo.xlsx", "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiResult apiResult = this.findAverageService.AverageExport(response,p,request);
        return apiResult;
    }


    /**
     *  功能描述：根据课程查询平均分柱状图 所需数据(年级平均分、各班级平均分)
     *  参数：考试父节点ID、课程id、届次、学段id
     *  返回：
     *  时间：4
     */
    @ApiOperation(value = "平均分柱状图信息(课程)")
    /*@GetMapping("/AverageBarChart")*/
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examParentId",value = "考试父节点ID",required = true,dataType = "Long"),
            @ApiImplicitParam(name = "periodId",value = "学段ID",required = true,dataType = "Long"),
            @ApiImplicitParam(name = "thetime",value = "届次",required = true),
            @ApiImplicitParam(name = "curriculumId",value = "课程ID",required = true,dataType = "Long"),
    })
    @Log(name = "成绩管理 ==> 课程平均分柱状图信息")
    public ApiResult AverageBarChart(Long examParentId, Long periodId, String thetime,
                                   Long curriculumId){

        return null;
    }



    /**
     *  功能描述：不同的班级或者课程 查询不同的平均分,查询年级的平均分情况，分页
     *  参数：必传：考试父id、学段id、届次
     *  课程id(可复选)、班级id(可复选)、班级层次id、班级类型id
     *  返回：
     *  时间：6
     */
    @ApiOperation("查询年级班级平均分")
    //@PostMapping("/findAverageInfo")
    @Log(name = "成绩管理 ==> 统计平均分")
    public ApiResult findAverageInfo(
            @ApiParam(name = "p", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
                    "  periodId=学段id（必传）,    thetime=届次（必传）,    curriculumIdList=课程id集合(必传)," +
                    "classTypeId=班级类型id（必传） classLevelId=班级层次id（必传）   classIdList=班级id集合（必传）" +
                    "返回结果=list(0):基础信息，list(1):年级情况，list(2):总分信息",required = true)
            @RequestBody Pager p, HttpServletRequest request){

        ApiResult apiResult = findAverageService.findAverageInfo(p,request);
        return apiResult;
    }


    /**
     *  功能描述：根据学段id 届次  层次id 返回层次下的班级列表
     *  参数：必传：考试父id、学段id、届次
     *  返回：班级列表
     *  时间：4
     */
    @ApiOperation("返回同类型同层次班级列表")
    //@PostMapping("/findClassByLevel")
    @Log(name = "成绩管理 ==> 查询同层次班级列表")

    public ApiResult findClassByLevel(@ApiParam(name = "map",
            value = "查询条件=periodId=学段id(必传),  thetime=届次(必传)," +
            " classTypeId=班级类型id(必传) classLevelId=班级层次id（必传）" ,required = true)
                         @RequestBody Map<String,Object> map,HttpServletRequest request){
        ApiResult apiResult = this.findAverageService.findClassByLevel(map);
        return apiResult;
    }




    /**
     *  功能描述：不同的班级或者课程 查询不同的平均分,查询单个班级的平均分情况，分页
     *  参数：必传：考试父id、学段id、届次
     *  课程id(可复选)、班级id、班级层次id、班级类型id
     *  返回：
     *  时间：6
     */
    @ApiOperation("查询单个班级平均分")
    //@PostMapping("/findAverageInfoByOne")
    @Log(name = "成绩管理 ==> 统计平均分")
    public ApiResult findAverageInfoByOne(
            @ApiParam(name = "p", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
                    "  periodId=学段id（必传）,    thetime=届次（必传）,    curriculumIdList=课程id集合(必传)," +
                    "classTypeId=班级类型id（必传） classLevelId=班级层次id（必传）   classId=班级id（必传）" +
                    "返回结果=list(0):基础信息，list(1):年级情况，list(2):总分信息",required = true)
            @RequestBody Pager p, HttpServletRequest request){

        ApiResult apiResult = findAverageService.findAverageInfo(p,request);
        return apiResult;
    }



    /**
     *  功能描述：不同的班级或者课程 查询不同的平均分,查询总分平均分情况，分页
     *  参数：必传：考试父id、学段id、届次
     *  班级id集合、班级层次id、班级类型id
     *  返回：
     *  时间：6
     */
    @ApiOperation("查询同层次各个班级总分平均分")
    //@PostMapping("/findTotalAvg")
    @Log(name = "成绩管理 ==> 统计总分平均分")
    public ApiResult findTotalAvg(
            @ApiParam(name = "p", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
                    "  periodId=学段id（必传）,    thetime=届次（必传）,    " +
                    "classTypeId=班级类型id（必传） classLevelId=班级层次id（必传）   classIdList=班级id集合（必传）"
                    ,required = true)
            @RequestBody Pager p, HttpServletRequest request){

        ApiResult apiResult = findAverageService.findTotalAvg(p,request);
        return apiResult;
    }





    /**
     *  功能描述：不同的班级或者课程 查询不同的平均分,查询年级的平均分情况，封装数据
     *  参数：必传：考试父id、学段id、届次
     *  课程id(可复选)、班级id(可复选)、班级层次id、班级类型id
     *  返回：
     *  时间：6
     */
    @ApiOperation("查询年级班级平均分，按班级封装")
    @PostMapping("/findAverageInfos")
    @Log(name = "成绩管理 ==> 统计平均分")
    public ApiResult findAverageInfos(
            @ApiParam(name = "p", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
                    "  periodId=学段id（必传）,    thetime=届次（必传）,    curriculumIdList=课程id集合(必传)," +
                    "classTypeId=班级类型id（必传） classLevelId=班级层次id（必传）   classIdList=班级id集合（必传）" +
                    "返回结果：集合最后一条是年级情况"
                    ,required = true)
            @RequestBody Pager p, HttpServletRequest request){

        ApiResult apiResult = findAverageService.findAverageInfos(p,request);
        return apiResult;
    }
}
