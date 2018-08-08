package com.cj.witbasics.controller;

import com.cj.witbasics.entity.StudentOsaas;
import com.cj.witbasics.service.StudentManagementService;
import com.cj.witcommon.aop.Log;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.entity.ApiResultUtil;
import com.cj.witcommon.utils.common.FileUtil;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witcommon.utils.excle.ImportExeclUtil;
import com.cj.witcommon.utils.excle.StudentBaseInfo;
import com.cj.witcommon.utils.excle.StudentStatistics;
import io.swagger.annotations.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 学生管理
 */
@RestController
@Api(tags = {"学生管理"})
@RequestMapping("/api/v1/studentManagement")
public class StudentManagementController {

    @Autowired
    private StudentManagementService studentManagementService;



    /**
     * 功能：模糊查询学生信息
     * 参数：查询的信息String
     * 返回：student_osaas对象结果集 包含:学籍基本信息表ID,姓名，学号，班级，性别，市内外，本校升入，备注
     * 完成时间:1
     */
    @PostMapping("/selectsStudentInfo")
    @ApiOperation("模糊查询学生信息")
    @Log(name = "学生 ==> 模糊查询信息")
    public ApiResult selectsStudentInfo(@ApiParam(name = "p",value = "分页参数，初始页码1，初始条数10，可为空," +
            "parameters=【periodId=学段ID,thetime=届次,classId=班级ID,isCity=（区县内、省市内、省市外），isSchool=是否本校升入（0-否，1-是）】parameter=姓名、性别、学籍号",required = false)
                                                   @RequestBody Pager p){

        ApiResult apiResult=new ApiResult();
        p = studentManagementService.findStudentsByCondition(p);

        return ApiResultUtil.fastResult(apiResult,p);
    }

    /**
     * 生成学号
     * 参数：无
     * 返回：学籍号
     * 时间：0.5
     */
    @GetMapping("/getStudentNumber")
    @ApiOperation("生成学籍号")
    @Log(name = "学生 ==> 生成学籍号")
    public ApiResult getStudentNumber(){
        return ApiResultUtil.fastResult(new ApiResult(),studentManagementService.getStudentNumber());

    }




    /**
     * 功能：新增学生
     * 参数：student_osaas对象
     * 返回：成功/失败
     * 完成时间：4
     */
    @ApiOperation(value = "添加学生信息", notes = "成功/失败")
    @PostMapping("/addStudentInfo")
    @Log(name = "学生 ==> 添加学生")
    public ApiResult addStudentInfo(
            @ApiParam(name = "studentossa", value = "新增学生信息对象", required = true)
            @RequestBody StudentOsaas studentossa,
            HttpServletRequest request) throws Exception {

        int j = studentManagementService.addStudentOsaasinfo(studentossa,request);
        ApiResult apiResult = new ApiResult();
        if(j == 1054){
            //班级不存在
            apiResult.setCode(ApiCode.class_error);
            apiResult.setMsg(ApiCode.class_error_MSG);

        }else if(j == 1053){
            //届次不存在
            apiResult.setCode(ApiCode.thetime_error);
            apiResult.setMsg(ApiCode.thetime_error_MSG);
        }else if(j == 1052){
            //年级不存在
            apiResult.setCode(ApiCode.grade_error);
            apiResult.setMsg(ApiCode.grade_error_MSG);
        }else if(j == 1051){
            //学段不存在
            apiResult.setCode(ApiCode.period_error);
            apiResult.setMsg(ApiCode.period_error_MSG);

        }else if(j == 0){
            apiResult.setCode(ApiCode.error_create_failed);
            apiResult.setMsg(ApiCode.error_create_failed_MSG);

        }else if(j > 0){
            //添加成功
            apiResult.setCode(ApiCode.SUCCESS);
            apiResult.setMsg(ApiCode.SUCCESS_MSG);

        }


        return apiResult;
    }



    /**
     * 功能：学生信息模板下载
     * 参数:
     * 返回：
     * 完成时间：1
     */
    @ApiOperation("学生信息模板下载")
    @GetMapping("/downloadStudentTemplate")
    @Log(name = "学生 ==> 模板下载")
    public void downloadStudentTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        studentManagementService.downloadStudengTemplate(request,response);

    }

//    @ApiOperation("文件下载")
//    @GetMapping(value = "/media")
//    public ResponseEntity<FileSystemResource> downloadFile()
//            throws IOException {
//        String filename = "测试Eexle导出.xlsx";
//        return export(new File("D:/file/"+filename));
//
//    }

//    public ResponseEntity<FileSystemResource> export(File file) {
//        if (file == null) {
//            return null;
//        }
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Content-Disposition", "attachment; filename=" + System.currentTimeMillis() + ".xls");
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//        return ResponseEntity
//                .ok()
//                .headers(headers)
//                .contentLength(file.length())
//                .contentType(MediaType.parseMediaType("application/octet-stream"))
//                .body(new FileSystemResource(file));
//    }

    /**
     * 功能：学生信息批量导入
     * 参数：TODO:
     * 返回：成功/失败
     * 完成时间：4
     */
    @ApiOperation("学生信息批量导入")
    @PostMapping("/importStucents")
    @Log(name = "学生 ==> 信息导入")
    public ApiResult importStucents(
            @ApiParam(value = "学生信息批量导入（选择文件）",required = true) MultipartFile file,
            HttpServletRequest request) throws Exception {
        System.out.println("文件名====>>"+file.getOriginalFilename());

        Map map = studentManagementService.importStucents(file,request);

        ApiResult apiResult = new ApiResult();
        int nums = (Integer) map.get("nums");
        List msgList = (ArrayList)map.get("msgList");
        if(nums > 0){
            apiResult.setCode(ApiCode.import_success);
            apiResult.setMsg(ApiCode.import_success_MSG+nums+"条数据");
            apiResult.setData(msgList);
        }else {
            apiResult.setCode(ApiCode.import_failed);
            apiResult.setMsg(ApiCode.import_failed_MSG);
            apiResult.setData(msgList);

        }

        return apiResult;
    }





    /**
     * 功能：编辑信息前查询信息(通过admin_id查询学生信息详情)
     * 参数：adminId
     * 返回：StudentOsaas 对象
     * 完成时间：1
     */
    @GetMapping("/selectStudentOsaas")
    @ApiOperation("通过 adminId 查询学生信息详情")
    @Log(name = "学生 ==> 查询学生信息详情")
    public ApiResult selectStudentOsaas(@ApiParam(name = "adminId",value = "账户ID",required = true) Long adminId){
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(studentManagementService.selectStudentOsaas(adminId));

        return apiResult;


    }

    /**
     * 功能：编辑学生信息
     * 参数：StudentOsaas对象
     * 返回：成功/失败
     * 完成时间：1
     */
    @ApiOperation("编辑学生信息")
    @PutMapping("/updateStaffInfo")
    @Log(name = "学生 ==> 编辑信息")
    public ApiResult updateStaffInfo(@ApiParam(name = "studentOsaas",value = "学生信息",required = true)
                                         @RequestBody StudentOsaas studentOsaas){

        int j = studentManagementService.updateStaffInfo(studentOsaas);
        ApiResult apiResult = new ApiResult();
        if(j > 0){
            //修改
            apiResult.setCode(ApiCode.SUCCESS);
            apiResult.setMsg(ApiCode.SUCCESS_MSG);

        }else {
            apiResult.setCode(ApiCode.error_create_failed);
            apiResult.setMsg(ApiCode.error_create_failed_MSG);
        }

        return apiResult;

    }


    @GetMapping("/findAllPeriodAndThetimeAndClassBySchoolId")
    @ApiOperation("查询本校所有学段、届次、班级信息")
    @Log(name = "学生 ==> 查询本校所有学段、届次、班级信息")
    public ApiResult findAllPeriodAndThetimeAndClassBySchoolId(){

        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(studentManagementService.findAllPeriodAndThetimeAndClassBySchoolId());


        return apiResult;
    }


}
