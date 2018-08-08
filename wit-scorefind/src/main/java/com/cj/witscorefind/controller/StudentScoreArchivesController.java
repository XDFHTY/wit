package com.cj.witscorefind.controller;

import com.cj.witcommon.aop.Log;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witscorefind.service.StudentScoreArchivesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 学生成绩档案
 */
@Api(tags = "学生成绩档案")
@RestController
@RequestMapping("/api/v1/studentScoreArchives")
public class StudentScoreArchivesController {


    @Autowired
    private StudentScoreArchivesService studentScoreArchivesService;


    @ApiOperation("查询学生成绩档案")
    @Log(name = "学生成绩档案 ==> 查询学生成绩档案")
    @GetMapping("/findAllExamScore")
    @ApiImplicitParam(name = "registerNumber",value = "学籍号",required = true,defaultValue = "212910103")
    public ApiResult findAllExamScore(String registerNumber){

        ApiResult apiResult = new ApiResult();

        try {
            apiResult.setData(studentScoreArchivesService.findAllExamScore(registerNumber));
            apiResult.setCode(ApiCode.SUCCESS);
            apiResult.setMsg(ApiCode.SUCCESS_MSG);
        }catch (Exception e){
            apiResult.setCode(ApiCode.error_search_failed);
            apiResult.setMsg(ApiCode.error_search_failed_MSG);
        }


        return apiResult;
    }

    @ApiOperation("导出学生成绩档案")
    @Log(name = "学生成绩档案 ==> 导出学生成绩档案")
    @GetMapping("/exportAllExamScore")
    @ApiImplicitParam(name = "registerNumber",value = "学籍号",required = true,defaultValue = "212910103")
    public void exportAllExamScore(HttpSession session , HttpServletResponse response, String registerNumber) throws Exception {
        studentScoreArchivesService.exportAllExamScore(session,response,registerNumber);

    }

}
