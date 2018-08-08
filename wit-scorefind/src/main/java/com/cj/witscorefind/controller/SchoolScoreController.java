package com.cj.witscorefind.controller;


import com.cj.witcommon.aop.Log;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.entity.ApiResultUtil;
import com.cj.witcommon.utils.common.FileUtil;
import com.cj.witscorefind.service.StudentScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 班级信息相关业务
 */
@Api(tags = "成绩导入")
@RestController
@RequestMapping("/api/v1/schoolscore")
public class SchoolScoreController {

    @Autowired(required = false)
    private StudentScoreService scoreService;

    @Value("${web.upload-path}")
    private String path; //文件下载路径，配置文件

    /**
     *  功能描述：模版下载
     *  参数：response,request
     *  返回：对应结果集
     *  时间：5小时
     */
    @ApiOperation(value = "模版下载", notes = "返回指定路径内的Excel模版文件")
    @Log(name = "成绩导入 ===> 模版下载")
    @GetMapping("/downLoadExcel")
    @ApiImplicitParam(name = "template",value = "模板编号",required = true)
    public void downLoadExcel(HttpServletResponse response, HttpServletRequest request,int template){
        //TODO:获取文件名
        //TODO:判断文件后缀
        String fileName = "";
        if(template==1){
            fileName = "score1.xls";
        }else if(template==2){
            fileName = "score2.xls";
        }
        try {
            new FileUtil().download(request, response, this.path, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *  功能描述：excel导入
     *  参数：
     *  返回：对应结果集
     *  时间：5小时
     */
    @ApiOperation("导入成绩信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schoolStageId", value = "学期(前端单选)", required = true, paramType = "form"),
            @ApiImplicitParam(name = "periodId", value = "学段", required = false, paramType = "form"),
            @ApiImplicitParam(name = "thetime", value = "届次", required = false, paramType = "form"),
//            @ApiImplicitParam(name = "classId", value = "班级ID", required = true, paramType = "form"),
            @ApiImplicitParam(name = "examParentId", value = "考试父Id", required = false, paramType = "form"),
            @ApiImplicitParam(name = "modelNumber", value = "模版编号：1 ， 2", required = false, paramType = "form")
    })
    @Log(name = "成绩导入 ===> 导入成绩")
    @PostMapping("/bathImportInfo")
    public ApiResult bathImportScoreInfo(String schoolStageId, Long examParentId,Long periodId,  String thetime,Integer modelNumber,
                                        MultipartFile file,  HttpServletRequest request){

        //返回对象
        ApiResult apiResult = new ApiResult();
        //TODO:获取操作人ID
        Long operatorId = (Long)request.getSession().getAttribute("adminId");

        //TODO:校验文件是否为空
        if(file == null) {
            ApiResultUtil.fastResultHandler(apiResult, false, ApiCode.FAIL, ApiCode.FAIL_MSG, null); //处理失败
            return apiResult;
        }

        Map<String, Object> params = new HashMap<String, Object>();

        //读取并验证excle中的参数是否正确
        Map map = scoreService.checkExcle(file);


        if(map.containsKey("msg")){
            //存在msg,没有上传参数

            if(examParentId == null){
                apiResult.setCode(ApiCode.FAIL);
                apiResult.setMsg("考试不能为空");
                return apiResult;

            }
            if(periodId == null){
                apiResult.setCode(ApiCode.FAIL);
                apiResult.setMsg("学段不能为空");
                return apiResult;

            }
            if(thetime == null){
                apiResult.setCode(ApiCode.FAIL);
                apiResult.setMsg("届次不能为空");
                return apiResult;

            }
            if(modelNumber == null){
                apiResult.setCode(ApiCode.FAIL);
                apiResult.setMsg("模板不能为空");
                return apiResult;

            }
            //使用传入的参数
            thetime += "-7-1";
            //参数封装
            params.put("schoolStageId", schoolStageId="1");
            params.put("thetime", thetime);
            params.put("examParentId", examParentId);
        }else {

            //sheet2的参数校验通过，使用sheet2的参数
            thetime = map.get("thetime")+"-7-1";
            //参数封装
            params.put("schoolStageId", schoolStageId="1");
            params.put("thetime", thetime);
            params.put("examParentId", map.get("examParentId"));

            modelNumber = (Integer) map.get("modelNumber");


        }

        System.out.println("父Id： " + examParentId);
        try{
            //获取文件流
            InputStream in = file.getInputStream();
            apiResult = this.scoreService.bathImportInfo(file, in, params, operatorId, modelNumber);
            /*if(result){
                ApiResultUtil.fastResultHandler(apiResult, true, null, null, null);
            }else{
                ApiResultUtil.fastResultHandler(apiResult, false, ApiCode.FAIL, ApiCode.FAIL_MSG, null); //处理失败
            }*/
        }catch (Exception e){ //异常处理
            ApiResultUtil.fastResultHandler(apiResult, false,
                    ApiCode.FAIL, ApiCode.error_unknown_database_operation_MSG, null);
            e.printStackTrace();
        }
        return apiResult;
    }






}
