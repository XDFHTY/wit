package com.cj.witscorefind.controller;

import com.cj.witcommon.aop.Log;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.entity.ApiResultUtil;
import com.cj.witcommon.utils.common.FileUtil;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.entity.ExamSettingReq;
import com.cj.witscorefind.service.ExamSettingsService;
import com.google.gson.Gson;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 考试参数设置
 * Created by XD on 2018/6/2.
 */
@Api(tags = "考试参数设置")
@RestController
@RequestMapping("/api/v1/examSettings")
public class ExamSettingsController {

    @Autowired
    private ExamSettingsService examSettingsService;


    @Value("${web.upload-path}")
    private String path; //文件下载路径，配置文件

    /**
     * 查询所有考试名称和id
     * @return
     */
    @ApiOperation(value = "获取全部考试名称和id")
    @GetMapping("/findExamName")
    @Log(name = "考试参数设置 ==>查询所有考试名称 ")
    public ApiResult findExamName(){
        ApiResult apiResult = new ApiResult();
        apiResult = this.examSettingsService.findExamName();
        return apiResult;
    }


    /**
     *  功能描述：查询所有考试信息,或带模糊条件
     *  参数：学校ID，考试类别
     *  返回：成功/失败
     *  时间：5小时
     */
    @ApiOperation(value = "查询考试", notes = "返回成功或失败")
    @Log(name = "考试参数设置 ===> 查询考试")
    @PostMapping("/findExamById")
    public ApiResult findExamById(
            @ApiParam(name = "p", value = "查询条件=parameters=" +"【examParentId=考试父节点id（可不传）," +
            "  vague=模糊条件（可不传）】", required = true)
            @RequestBody Pager p,HttpServletRequest request){
        ApiResult apiResult = new ApiResult();
        apiResult = this.examSettingsService.findExamByVague(p);
        return apiResult;
    }


    /**
     *  功能描述：查询这次考试的班级和考试课程
     *  参数：学校ID，考试类别
     *  返回：成功/失败
     *  时间：5小时
     */
    @ApiOperation(value = "查询这次考试的班级和考试课程", notes = "返回成功或失败")
    @Log(name = "考试参数设置 ===> 查询班级和课程")
    @PostMapping("/findExamClass")
    public ApiResult findExamClass(
            @ApiParam(name = "p", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
                    "  thetime=届次（必传）2019 ，periodId=学段id（必传），classTypeId=班级类型id（非必传），" +
                    "classLevelId=班级层次id（非必传）】", required = true)
            @RequestBody Pager p,HttpServletRequest request){
        ApiResult apiResult = new ApiResult();
        apiResult = this.examSettingsService.findExamClass(p);
        return apiResult;
    }

    /**
     *  功能描述：选择多个班级为一个课程设置分数档次、名次档次、难度系数、优势分数线、单科分数线、任务数
     *  返回：成功/失败
     *  时间：10小时
     */
    @ApiOperation(value = "各班单科参数设置", notes = "返回成功或失败")
    @Log(name = "考试参数设置 ===> 单科参数设置")
    @PostMapping("/addParametersBySubject")
    public ApiResult addParametersBySubject(
            @ApiParam(name = "ExamSettingReq", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
                    "  thetime=届次（必传）2019 ，periodId=学段id（必传），subjectName=课程名称(必传),classIds=班级id集合(必传)" +
                    "，degree=难度系数（必传），advantageScore=优势分数线（必传），gradeList=档次设置（必传），taskList=任务数设置（必传）】"
                    , required = true)
            @RequestBody ExamSettingReq e, HttpServletRequest request){

        //Gson gson = new Gson();
        //ExamSettingReq e = gson.fromJson(s,ExamSettingReq.class);
        ApiResult apiResult = new ApiResult();
        apiResult = this.examSettingsService.addParametersBySubject(e,request);
        return apiResult;
    }


    /**
     *  功能描述：总分设置,整个年级的文科或理科的总分，可以设置难度系数，档次设置，总分分数线
     *  参数：examParentId，periodId，classTypeId，thetime，degree，gradeList
     *  返回：成功/失败
     *  时间：8小时
     */
    @ApiOperation(value = "总分参数设置", notes = "返回成功或失败")
    @Log(name = "考试参数设置 ===> 总分参数设置")
    @PostMapping("/addParametersByTotal")
    public ApiResult addParametersByTotal(
            @ApiParam(name = "ExamSettingReq", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
                    "  thetime=届次（必传）2019 ，periodId=学段id（必传），classTypeId=班级类型id（必传），" +
                    "degree=难度系数（必传），gradeList=档次设置（必传）】"
                    , required = true)
            @RequestBody ExamSettingReq e, HttpServletRequest request){
        ApiResult apiResult = new ApiResult();
        apiResult = this.examSettingsService.addParametersByTotal(e,request);
        return apiResult;
    }


    /**
     *  功能描述：按照年级（分类型）查询总分档次设置信息
     *  参数：examParentId，periodId，classTypeId，thetime
     *  返回：成功/失败
     *  时间：4小时
     */
    @ApiOperation(value = "查询总分档次信息")
    @Log(name = "考试参数查询 ===> 查询总分档次信息")
    @PostMapping("/findGradeByTotal")
    public ApiResult findGradeByTotal(
            @ApiParam(name = "ExamSettingReq", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
                    "  thetime=届次（必传）2019 ，periodId=学段id（必传），classTypeId=班级类型id（必传）】"
                    , required = true)
            @RequestBody ExamSettingReq e, HttpServletRequest request){
        ApiResult apiResult = new ApiResult();
        apiResult = this.examSettingsService.findGradeByTotal(e,request);
        return apiResult;
    }


    /**
     * 查询这次考试的学段和届次
     */
    @ApiOperation(value = "查询这次考试的学段和届次")
    @Log(name = "届次查询 ===> 查询这次考试的学段和届次")
    @PostMapping("/findThetimeByExam")
    public ApiResult findThetimeByExam(
            @ApiParam(name = "ExamSettingReq", value = "查询条件=考试父id=examParentId（必传）】", required = true)
            @RequestBody ExamSettingReq e, HttpServletRequest request){
        ApiResult apiResult = new ApiResult();
        apiResult = this.examSettingsService.findThetimeByExam(e,request);
        return apiResult;
    }


    /**
     *  功能描述：班级总分各档任务数设置
     *  参数：examParentId，periodId，classTypeId，thetime，classIds，taskList
     *  返回：成功/失败
     *  时间：8小时
     */
    @ApiOperation(value = "班级总分格挡任务数设置", notes = "返回成功或失败")
    @Log(name = "考试参数设置 ===> 任务数设置")
    @PostMapping("/addParametersByTask")
    public ApiResult addParametersByTask(
            @ApiParam(name = "ExamSettingReq", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
                    "  thetime=届次（必传）2019 ，periodId=学段id（必传），classTypeId=班级类型id（必传），" +
                    "classIds=班级id集合（必传），taskList=任务数设置（必传）】"
                    , required = true)
            @RequestBody ExamSettingReq e, HttpServletRequest request){
        ApiResult apiResult = new ApiResult();
        apiResult = this.examSettingsService.addParametersByTask(e,request);
        return apiResult;
    }


    /**
     *  功能描述：设置年级单科分数线
     *  参数：examParentId，periodId，classTypeId，thetime，gradeList（type，subjectId，subjectName，advantageScore）
     *  返回：成功/失败
     *  时间：8小时
     */
    @ApiOperation(value = "设置年级单科分数线", notes = "返回成功或失败")
    @Log(name = "考试参数设置 ===> 年级单科分数线")
    @PostMapping("/addParametersScore")
    public ApiResult addParametersScore(
            @ApiParam(name = "ExamSettingReq", value = "查询条件=parameters=" +"【examParentId=考试父节点id（必传）," +
                    "  thetime=届次（必传）2019 ，periodId=学段id（必传），classTypeId=班级类型id（必传），" +
                    "gradeList=单科分数线信息（必传）】"
                    , required = true)
            @RequestBody ExamSettingReq e, HttpServletRequest request){
        ApiResult apiResult = new ApiResult();
        apiResult = this.examSettingsService.addParametersScore(e,request);
        return apiResult;
    }





    /**
     *  功能描述：Excel导入考试参数信息(上传文件，文件解析，数据的插入)
     *  参数：Excel文件
     *  返回：导入成功或失败
     *  时间：16
     */
    @ApiOperation("导入考试参数")
    @Log(name = "考试管理 ==> 导入参数")
    @ApiParam(value = "考试参数导入（选择文件）",required = true)
    @PostMapping("/examImportInfo")
    public ApiResult examImportInfo(MultipartFile file,HttpServletRequest request){


        Long operatorId = (Long) request.getSession().getAttribute("adminId");
        //返回对象
        ApiResult apiResult = new ApiResult();

        if(file == null) {
            ApiResultUtil.fastResultHandler(apiResult, false, ApiCode.FAIL, ApiCode.FAIL_MSG, null); //处理失败
            return apiResult;
        }
        try{
            //获取文件流
            InputStream in = file.getInputStream();
            apiResult = this.examSettingsService.examImportInfo(file, in, operatorId);
        }catch (Exception e){ //异常处理
            ApiResultUtil.fastResultHandler(apiResult, false,
                    ApiCode.FAIL, ApiCode.error_unknown_database_operation_MSG, null);
            e.printStackTrace();
        }
        return apiResult;
    }




    /**
     *  功能描述：模版下载(excel)
     *  参数：HttpServletResponse response
     *  返回：指定路径内的Excel模版文件
     *  时间：1
     */
    @ApiOperation(value = "模版下载", notes = "返回指定路径内的Excel模版文件")
    @Log(name = "考试参数设置 ==> 导入模版下载")
    @GetMapping("/downLoadExcel")
    public void downLoadExcel(HttpServletResponse response, HttpServletRequest request){
        //TODO:获取文件名
        //TODO:判断文件后缀
        String fileName = "examSettings.xlsx";
        try {
            new FileUtil().download(request, response, this.path, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
