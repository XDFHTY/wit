package com.cj.witscorefind.controller;


import com.cj.witbasics.entity.Grade;
import com.cj.witbasics.entity.PeriodDirectorThetime;
import com.cj.witbasics.entity.SchoolPeriodClassThetime;
import com.cj.witcommon.aop.Log;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.entity.ApiResultUtil;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.service.FindScoreService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.*;
import org.apache.http.HttpResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成绩查询类
 *
 */
@Api(tags = "成绩查询")
@RestController
@RequestMapping("/api/v1/findScore")
public class FindScoreController {

    @Autowired
    private FindScoreService findScoreService;

    Gson gson = new Gson();

    /**
     *  功能描述：不同的角色,查询不同的成绩
     *  参数：
     *  返回：
     */
//    @ApiOperation("查询考试-班级-成绩 统计")
//    @GetMapping("/findExamScoreCount")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "examParentId",value = "考试父节点ID",required = true)
//    })
//    @Log(name = "成绩查询 ==> 查询考试班级成绩统计")
//    public ApiResult findExamClassScoreCount(HttpSession session, Long examParentId, String thetime){
//        //返回对象
//        Map map = new HashMap();
//        map.put("examParentId",examParentId);
//        ApiResult apiResult = new ApiResult();
//        apiResult.setCode(ApiCode.SUCCESS);
//        apiResult.setMsg(ApiCode.SUCCESS_MSG);
//        apiResult.setData(findScoreService.findExamClassScoreCount(session,map));
//        return apiResult;
//    }


    /**
     * 根据考试父ID、学段ID、届次、班级类型ID查询此次考试的所有班级
     */

    /**
     * 分页、条件查询 班级-成绩分析
     */
    @ApiOperation(value = "分页查询 本次考试-成绩单（年级）")
    @Log(name = "成绩查询 ==> 考试-年级/班级/课程/个人 成绩单")
    @PostMapping("/findExamTranscripts")
    public ApiResult findYearExamClassTotalScore(HttpSession session,
                                                 @ApiParam(name = "pager",value = "【" +
                                                         "{\n" +
                                                         "\"parameters\": {\"oldExamParentId\":16,\"newExamParentId\":14,\"periodId\":9,\"thetime\":\"2019\",\"classTypeId\":5,\"classId\":375,\"subjectId\":70,\"msg\":\"张\"},\n" +
                                                         "\"currentPage\":1,\n" +
                                                         "\"pageSize\":15\n" +
                                                         "}" +
                                                         "】",required = true)
                                                 @RequestBody Pager pager){

        Map map = pager.getParameters();
        pager.setParameters(map);
        ApiResult apiResult = new ApiResult(); //返回对象
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(findScoreService.findExamClassTotalScore(session,pager));

        return apiResult;
    }



//    /**
//     *  功能描述：分页、条件 查询班级-单科考试分数
//     *  参数：
//     *  返回：
//     */
//    @ApiOperation(value = "分页查询本次考试-单科-成绩分析（年级）")
//    @Log(name = "成绩查询 ==> 考试-单科-成绩分析")
//    @PostMapping("/findYearExamClassSubjectScore")
//    public ApiResult findYearExamClassSubjectScore(HttpSession session,
//                                               @ApiParam(name = "pager",value = "【" +
//                                                       "{\n" +
//                                                       "\"parameters\": {\"oldExamParentId\":16,\"newExamParentId\":14,\"periodId\":9,\"thetime\":\"2019\",\"classTypeId\":5,\"subjectId\":70,\"msg\":\"张\"},\n" +
//                                                       "\"currentPage\":1,\n" +
//                                                       "\"pageSize\":15\n" +
//                                                       "}" +
//                                                       "】",required = true)
//                                               @RequestBody Pager pager){
//
//        pager = findScoreService.findExamClassSubjectScore(session,pager);
//
//
//
//        ApiResult apiResult = new ApiResult(); //返回对象
//        apiResult.setCode(ApiCode.SUCCESS);
//        apiResult.setMsg(ApiCode.SUCCESS_MSG);
//        apiResult.setData(pager);
//
//        return apiResult;
//    }

//    /**
//     * 分页、条件查询 班级-总分成绩分析
//     */
//    @ApiOperation(value = "分页查询 本次考试-总分-各科-成绩分析（班级）")
//    @Log(name = "成绩查询 ==> 考试-总分-各科-成绩分析")
//    @PostMapping("/findClassExamClassTotalScore")
//    public ApiResult findClassExamClassTotalScore(HttpSession session,
//                                             @ApiParam(name = "pager",value = "【" +
//                                                     "{\n" +
//                                                     "\"parameters\": {\"oldExamParentId\":16,\"newExamParentId\":14,\"periodId\":9,\"thetime\":\"2019\",\"classTypeId\":5,\"classId\":375,\"msg\":\"张\"},\n" +
//                                                     "\"currentPage\":1,\n" +
//                                                     "\"pageSize\":15\n" +
//                                                     "}" +
//                                                     "】",required = true)
//                                             @RequestBody Pager pager){
//
//        ApiResult apiResult = new ApiResult(); //返回对象
//        apiResult.setCode(ApiCode.SUCCESS);
//        apiResult.setMsg(ApiCode.SUCCESS_MSG);
//        apiResult.setData(findScoreService.findExamClassTotalScore(session,pager));
//
//        return apiResult;
//    }



//    /**
//     *  功能描述：分页、条件 查询班级-单科考试分数
//     *  参数：
//     *  返回：
//     */
//    @ApiOperation(value = "分页查询本次考试-单科-成绩分析（班级）")
//    @Log(name = "成绩查询 ==> 考试-单科-成绩分析")
//    @PostMapping("/findClassExamClassSubjectScore")
//    public ApiResult findClassExamClassSubjectScore(HttpSession session,
//                                               @ApiParam(name = "pager",value = "【" +
//                                                       "{\n" +
//                                                       "\"parameters\": {\"oldExamParentId\":16,\"newExamParentId\":14,\"periodId\":9,\"thetime\":\"2019\",\"classTypeId\":5,\"classId\":375,\"subjectId\":70,\"msg\":\"张\"},\n" +
//                                                       "\"currentPage\":1,\n" +
//                                                       "\"pageSize\":15\n" +
//                                                       "}" +
//                                                       "】",required = true)
//                                               @RequestBody Pager pager){
//
//        pager = findScoreService.findExamClassSubjectScore(session,pager);
//
//
//
//        ApiResult apiResult = new ApiResult(); //返回对象
//        apiResult.setCode(ApiCode.SUCCESS);
//        apiResult.setMsg(ApiCode.SUCCESS_MSG);
//        apiResult.setData(pager);
//
//        return apiResult;
//    }


    /**
     * 关注学生统计
     */
    @ApiOperation("关注学生统计（班级）")
    @Log(name = "成绩查询 ==> 关注学生统计")
    @GetMapping("/findFollowStudents")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newExamParentId",value = "考试ID",required = true,defaultValue = "14"),
            @ApiImplicitParam(name = "oldExamParentId",value = "对比考试ID",required = true,defaultValue = "16"),
            @ApiImplicitParam(name = "classId",value = "班级ID",required = true,defaultValue = "368"),
            @ApiImplicitParam(name = "followRank",value = "关注名次",required = true,defaultValue = "10")

    })
    public ApiResult findFollowStudents(long newExamParentId,long oldExamParentId,long classId,int followRank){

        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(findScoreService.findFollowStudents(newExamParentId,oldExamParentId,classId,followRank));
        return apiResult;
    }

    /**
     * 优势学科统计
     */
    @ApiOperation("优势学科统计（班级）")
    @Log(name = "成绩查询 ==> 优势学科统计")
    @GetMapping("/findClassSuperioritySubject")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examParentId",value = "考试ID",required = true,defaultValue = "14"),
            @ApiImplicitParam(name = "periodId",value = "学段ID",required = true,defaultValue = "9"),
            @ApiImplicitParam(name = "thetime",value = "届次",required = true,defaultValue = "2019"),
            @ApiImplicitParam(name = "classId",value = "班级ID",required = true,defaultValue = "368"),
    })
    public ApiResult findClassSuperioritySubject(long examParentId,long periodId,String thetime,long classId){
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(findScoreService.findClassSuperioritySubject(examParentId,periodId,thetime,classId));
        return apiResult;
    }

    /**
     * 优势学科统计
     */
    @ApiOperation("优势学科统计（学生）")
    @Log(name = "成绩查询 ==> 优势学科统计")
    @GetMapping("/findStudentSuperioritySubject")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "examParentId",value = "考试ID",required = true,defaultValue = "14"),
            @ApiImplicitParam(name = "periodId",value = "学段ID",required = true,defaultValue = "9"),
            @ApiImplicitParam(name = "thetime",value = "届次",required = true,defaultValue = "2019"),
            @ApiImplicitParam(name = "registerNumber",value = "学生学籍号",required = true,defaultValue = "212910102")
    })
    public ApiResult findStudentSuperioritySubject(long examParentId,long periodId,String thetime,String registerNumber){

        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(findScoreService.findStudentSuperioritySubject(examParentId,periodId,thetime,registerNumber));
        return apiResult;
    }


    /**
     * 查询 班级或学生 参加过的考试
     */
    @ApiOperation("查询班级或学生参加过并且有成绩的考试")
    @Log(name = "成绩查询 ==> 查询班级或学生参加过并且有成绩的考试")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "classId",value = "班级ID",required = false,defaultValue = "368"),
            @ApiImplicitParam(name = "registerNumber",value = "学生学籍号",required = false,defaultValue = "212910102")
    })
    @GetMapping("/findAllExamByClassOrStudent")
    public ApiResult findAllExamByClassOrStudent(Long classId , String registerNumber){
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(findScoreService.findAllExamByClassOrStudent(classId,registerNumber));
        return apiResult;
    }



    /**
     * 	历次考试均分趋势图：折线图，班级均分与年级均分对比(班级总分)
     */
    @ApiOperation("历次考试均分趋势图-(班级总分)")
    @Log(name = "成绩查询 ==> 历次考试均分趋势图-(班级总分)")
    @GetMapping("/findAllExamClassTotalScoreChart")
    @ApiImplicitParam(name = "json",value = "json=考试父ID集合JSON字符串,classId=班级ID,测试参数=【" +
            "{examParentIds:[50,51,14,16,17,18],classId:368}" +
            "】",required = true,defaultValue = "{examParentIds:[50,51,14,16,17,18],classId:368}")
    public ApiResult findAllExamClassTotalScoreChart(String json){

        Map map = gson.fromJson(json,new TypeToken<Map>(){}.getType());
        List<Long> examParentIds = (List<Long>) map.get("examParentIds");
        Long classId = Math.round((Double) map.get("classId"));

        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(findScoreService.findAllExamClassTotalScoreChart(examParentIds,classId));
        return apiResult;
    }

    /**
     * 	历次考试均分趋势图：折线图，班级均分与年级均分对比(班级各科)
     */
    @ApiOperation("历次考试均分趋势图-(班级各科)")
    @Log(name = "成绩查询 ==> 历次考试均分趋势图-(班级各科)")
    @GetMapping("/findAllExamClassSubjectScoreChart")
    @ApiImplicitParam(name = "json",value = "json=考试父ID集合JSON字符串,classId=班级ID,测试参数=【" +
            "{examParentIds:[50,51,14,16,17,18],classId:368}" +
            "】",required = true,defaultValue = "{examParentIds:[50,51,14,16,17,18],classId:368}")
    public ApiResult findAllExamClassSubjectScoreChart(String json){

        Map map = gson.fromJson(json,new TypeToken<Map>(){}.getType());
        List<Long> examParentIds = (List<Long>) map.get("examParentIds");
        Long classId = Math.round((Double) map.get("classId"));

        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(findScoreService.findAllExamClassSubjectScoreChart(examParentIds,classId));
        return apiResult;
    }

    /**
     * 历次考试各科成绩班级平均分 与学生成绩对比，折线图（学生）
     */
    @ApiOperation("历次考试各科成绩班级平均分 与学生成绩对比，折线图（学生）")
    @Log(name = "成绩查询 ==> 历次考试各科成绩（班级-学生）对比折线图（学生）")
    @GetMapping("/findAllExamStudentSubjectScoreChart")
    @ApiImplicitParam(name = "json",value = "json=考试父ID集合JSON字符串,registerNumber=学籍号，测试参数=【" +
            "{examParentIds:[50,14,16,17],registerNumber:\"212910103\"}" +
            "】",required = true,defaultValue = "{examParentIds:[50,14,16,17],registerNumber:\"212910103\"}")
    public ApiResult findAllExamStudentSubjectScoreChart(String json){
        Map map = gson.fromJson(json,new TypeToken<Map>(){}.getType());
        List<Long> examParentIds = (List<Long>) map.get("examParentIds");
        String registerNumber = (String) map.get("registerNumber");

        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(findScoreService.findAllExamStudentSubjectScoreChart(examParentIds,registerNumber));
        return apiResult;
    }
    /**
     * 历次考试总分班级平均分与学生总分对比，折线图（学生）
     */
    @ApiOperation("历次考试总分班级平均分与学生总分对比，折线图（学生）")
    @Log(name = "成绩查询 ==> 历次考试总分（班级-学生）对比折线图（学生）")
    @GetMapping("/findAllExamStudentTotalScoreChart")
    @ApiImplicitParam(name = "json",value = "json=考试父ID集合JSON字符串,registerNumber=学籍号，测试参数=【" +
            "{examParentIds:[50,14,16,17],registerNumber:\"212910103\"}" +
            "】",required = true,defaultValue = "{examParentIds:[50,14,16,17],registerNumber:\"212910103\"}")
    public ApiResult findAllExamStudentTotalScoreChart(String json){

        Map map = gson.fromJson(json,new TypeToken<Map>(){}.getType());
        List<Long> examParentIds = (List<Long>) map.get("examParentIds");
        String registerNumber = (String) map.get("registerNumber");

        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(findScoreService.findAllExamStudentTotalScoreChart(examParentIds,registerNumber));
        return apiResult;
    }

    /**
     * 历次考试年级排名走势图（学生）
     */
    @ApiOperation("历次考试年级排名走势图（学生）")
    @Log(name = "成绩查询 ==> 历次考试年级排名走势图（学生）")
    @GetMapping("/findAllExamStudentYearRankChart")
    @ApiImplicitParam(name = "json",value = "json=考试父ID集合JSON字符串,registerNumber=学籍号，测试参数=【" +
            "{examParentIds:[50,14,16,17],registerNumber:\"212910103\"}" +
            "】",required = true,defaultValue = "{examParentIds:[50,14,16,17],registerNumber:\"212910103\"}")
    public ApiResult findAllExamStudentYearRankChart(String json){
        Map map = gson.fromJson(json,new TypeToken<Map>(){}.getType());
        List<Long> examParentIds = (List<Long>) map.get("examParentIds");
        String registerNumber = (String) map.get("registerNumber");

        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(findScoreService.findAllExamStudentYearRankChart(examParentIds,registerNumber));
        return apiResult;
    }

    /**
     * ===================================================================================================
     *  功能描述：导出
     *  参数：班级ID
     *  返回：
     */
//    @ApiOperation(value = "导出信息(选择导出)", notes = "成功/失败")
//    @Log(name = "成绩查询 ==> 导出信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "classIdList", value = "班级ID集合"),
//    })
////    @PostMapping("/exportScoreInfo")
//    public void exportScoreInfo(
//            @RequestBody List<Long> classIdList, HttpServletResponse response){
//        response.setHeader("content-Type", "application/vnd.ms-excel");
//        //下载文件的默认名字
//        try {
//            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode("scoreDetails", "utf-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        System.out.println("进入！");
//        for(Long val : classIdList){
//            System.out.println(val);
//        }
//        this.findScoreService.exportScoreInfo(classIdList, response);
//    }


    @ApiOperation("导出成绩单")
    @Log(name = "成绩查询 ==> 导出成绩单")
    @PostMapping("/exportExamReport")
    public ApiResult exportExamReport(HttpSession session, HttpServletResponse response,
                                 @ApiParam(name = "pager",
                                         value = "{\n" +
                                                 "\"parameters\": {\"oldExamParentId\":16,\"newExamParentId\":14,\"periodId\":9,\"thetime\":\"2019\",\"classTypeId\":5,\"classId\":375},\n" +
                                                 "\"currentPage\":1,\n" +
                                                 "\"pageSize\":50\n" +
                                                 "}",
                                         required = true)
                                 @RequestBody Pager pager) throws Exception {

        ApiResult apiResult = findScoreService.exportExamReport(session,response,pager);
        return apiResult;

    }

    @ApiOperation("导出关注学生")
    @Log(name = "成绩查询 ==> 导出关注学生")
    @GetMapping("/exportFollowStudents")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "newExamParentId",value = "考试ID",required = true,defaultValue = "14"),
            @ApiImplicitParam(name = "oldExamParentId",value = "对比考试ID",required = true,defaultValue = "16"),
            @ApiImplicitParam(name = "classId",value = "班级ID",required = true,defaultValue = "368"),
            @ApiImplicitParam(name = "followRank",value = "关注名次",required = true,defaultValue = "10")

    })
    public void exportFollowStudents(HttpSession session,HttpServletResponse response,
                                     long newExamParentId,long oldExamParentId,long classId,int followRank) throws Exception {

        findScoreService.exportFollowStudents(session,response,newExamParentId,oldExamParentId,classId,followRank);
    }


}
