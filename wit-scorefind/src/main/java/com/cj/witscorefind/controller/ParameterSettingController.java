//package com.cj.witscorefind.controller;
//
//
//import com.cj.witbasics.entity.SchoolExamGrade;
//import com.cj.witcommon.aop.Log;
//import com.cj.witcommon.entity.ApiCode;
//import com.cj.witcommon.entity.ApiResult;
//import com.cj.witcommon.utils.entity.other.Pager;
//import com.cj.witscorefind.service.ParameterSettingService;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import springfox.documentation.annotations.ApiIgnore;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/scorefind")
//@Api(tags = "参数设置")
//@ApiIgnore
//public class ParameterSettingController {
//
//
//
//    @Autowired
//    private ParameterSettingService parameterSettingService;
//
//    /**
//     * 档次设置
//     * 班主任设置本班
//     * 年级主任设置本年级
//     * 参数封装：考试父节点ID、学段ID、届次、班级类型ID、班级ID、课程ID/名称
//     */
//    @ApiOperation("新增考试档次")
//    @PostMapping("/addScoreGrade")
//    @Log(name = "参数设置 ==> 新增考试档次")
//    public ApiResult addScoreGrade(HttpSession session,
//                                   @ApiParam(name = "schoolExamGrades",value = "档次设置信息",required = true)
//                                   @RequestBody List<SchoolExamGrade> schoolExamGrades){
//
////        Gson gson = new Gson();
////        List<SchoolExamGrade> schoolExamGrades = gson.fromJson(s, new TypeToken<List<SchoolExamGrade>>() {}.getType());
//        int i = parameterSettingService.addExamGrade(schoolExamGrades,session);
//
//        ApiResult apiResult = new ApiResult();
//
//        if(i == 401){
//            apiResult.setCode(ApiCode.http_status_unauthorized);
//            apiResult.setMsg(ApiCode.http_status_unauthorized_MSG);
//        }else if(i == 0){
//            apiResult.setCode(ApiCode.FAIL);
//            apiResult.setMsg(ApiCode.FAIL_MSG);
//        }else if(i == 7) {
//            apiResult.setCode(ApiCode.error_create_failed);
//            apiResult.setMsg(ApiCode.error_create_failed_MSG+",档次参数已存在");
//        }else {
//            apiResult.setCode(ApiCode.SUCCESS);
//            apiResult.setMsg(ApiCode.SUCCESS_MSG);
//        }
//
//        return apiResult;
//
//    }
//
//
//    /**
//     * 查询考试档次设置
//     * 参数：考试父节点ID、考试ID、学段ID、届次、班级类型ID（选）、班级ID（选）
//     * 查询 课程ID/名称 及档次设置详情
//     */
//    @ApiOperation("查询考试档次设置")
//    @PostMapping("/findScoreGrade")
//    @Log(name = "参数设置 ==> 查询考试档次设置")
//    public ApiResult findScoreGrade(@ApiParam(name = "p",value = "查询条件=parameters" +
//            "【examParentId=考试父节点ID,examId=考试ID,classPeriodId=学段ID,thetime=届次" +
//            "classTypeId=班级类型ID(非必填),classId=班级ID(非必填)】",required = true)
//                                    @RequestBody Pager p){
//
//
//        ApiResult apiResult = new ApiResult();
//        apiResult.setCode(ApiCode.SUCCESS);
//        apiResult.setMsg(ApiCode.SUCCESS_MSG);
//        apiResult.setData(parameterSettingService.findScoreGrade(p));
//        return apiResult;
//    }
//
//
//    /**
//     * 批量修改考试档次信息
//     */
//    @ApiOperation("批量修改考试档次信息,参数同新增")
//    @Log(name = "参数设置 ==> 批量修改考试档次信息")
//    @PutMapping("/updateScoreGrade")
//    public ApiResult updateScoreGrade(HttpSession session,
//                                      @ApiParam(name = "schoolExamGrades",value = "档次设置信息",required = true)
//                                      @RequestBody List<SchoolExamGrade> schoolExamGrades){
//
//        int i = parameterSettingService.updateScoreGrade(schoolExamGrades,session);
//
//        ApiResult apiResult = new ApiResult();
//
//        if(i == 401){
//            apiResult.setCode(ApiCode.http_status_unauthorized);
//            apiResult.setMsg(ApiCode.http_status_unauthorized_MSG);
//        }else if(i == 0){
//            apiResult.setCode(ApiCode.FAIL);
//            apiResult.setMsg(ApiCode.FAIL_MSG);
//        }else {
//            apiResult.setCode(ApiCode.SUCCESS);
//            apiResult.setMsg(ApiCode.SUCCESS_MSG);
//        }
//
//        return apiResult;
//    }
//
//
//}
