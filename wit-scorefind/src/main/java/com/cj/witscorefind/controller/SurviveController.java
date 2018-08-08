package com.cj.witscorefind.controller;

import com.cj.witcommon.aop.Log;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witscorefind.service.SurviveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@RequestMapping("/api/v1/survive")
@Api(tags = "成活率")
@RestController
public class SurviveController {

    @Autowired
    private SurviveService surviveService;


    @GetMapping("/findSurvive")
    @ApiOperation("成活率统计")
    @Log(name = "成活率 ==> 成活率统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newExamParentId",value = "考试父节点ID",required = true,defaultValue = "14"),
            @ApiImplicitParam(name = "oldExamParentId",value = "对比考试父节点ID",required = true,defaultValue = "16"),
            @ApiImplicitParam(name = "periodId",value = "学段ID",required = true,defaultValue = "9"),
            @ApiImplicitParam(name = "thetime",value = "届次",required = true,defaultValue = "2019"),
            @ApiImplicitParam(name = "classTypeId",value = "班级类型ID",required = true,defaultValue = "5"),
            @ApiImplicitParam(name = "upScore",value = "线上N分",required = true,defaultValue = "10"),
            @ApiImplicitParam(name = "downScore",value = "线下N分",required = true,defaultValue = "10")
    })
    public ApiResult findSurvive(long newExamParentId, long oldExamParentId,
                                 long periodId, String thetime,
                                 long classTypeId,
                                 BigDecimal upScore, BigDecimal downScore) {

        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(surviveService.findSurvive(newExamParentId,oldExamParentId,periodId,thetime,classTypeId,upScore,downScore));


        return apiResult;
    }

    @GetMapping("/exportSurvive")
    @ApiOperation("成活率导出")
    @Log(name = "成活率 ==> 成活率导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newExamParentId",value = "考试父节点ID",required = true,defaultValue = "14"),
            @ApiImplicitParam(name = "oldExamParentId",value = "对比考试父节点ID",required = true,defaultValue = "16"),
            @ApiImplicitParam(name = "periodId",value = "学段ID",required = true,defaultValue = "9"),
            @ApiImplicitParam(name = "thetime",value = "届次",required = true,defaultValue = "2019"),
            @ApiImplicitParam(name = "classTypeId",value = "班级类型ID",required = true,defaultValue = "5"),
            @ApiImplicitParam(name = "upScore",value = "线上N分",required = true,defaultValue = "10"),
            @ApiImplicitParam(name = "downScore",value = "线下N分",required = true,defaultValue = "10")
    })
    public void exportSurvive(long newExamParentId, long oldExamParentId,
                              long periodId, String thetime,
                              long classTypeId,
                              BigDecimal upScore, BigDecimal downScore,
                              HttpServletResponse response) throws Exception {

        surviveService.exportSurvive(newExamParentId,oldExamParentId,periodId,thetime,classTypeId,upScore,downScore,response);

    }
 }
