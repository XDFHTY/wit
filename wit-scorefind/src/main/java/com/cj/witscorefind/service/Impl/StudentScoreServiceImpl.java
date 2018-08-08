package com.cj.witscorefind.service.Impl;

import com.cj.witbasics.entity.SchoolExam;
import com.cj.witbasics.entity.StudentOsaas;
import com.cj.witbasics.entity.StudentScore;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witscorefind.entity.CheckExcle;
import com.cj.witbasics.mapper.*;
import com.cj.witbasics.service.Impl.SchoolClassServiceImpl;
import com.cj.witcommon.utils.TimeToString;
import com.cj.witcommon.utils.common.StringHandler;
import com.cj.witcommon.utils.excle.ImportExeclUtil;
import com.cj.witcommon.utils.excle.ScoreModelTwoInfo;
import com.cj.witcommon.utils.excle.StudentScoreInfo;
import com.cj.witscorefind.service.StudentScoreService;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Service
public class StudentScoreServiceImpl implements StudentScoreService {

    //日志
    private static final Logger log = LoggerFactory.getLogger(SchoolClassServiceImpl.class);

    @Autowired(required = false)
    protected StudentScoreMapper scoreMapper;

    @Autowired(required = false)
    private SchoolSubjectMapper subjectMapper;

    @Autowired(required = false)
    private StudentOsaasMapper osaasMapper;

    @Autowired(required = false)
    private SchoolExamMapper examMapper;

    @Autowired(required = false)
    private SchoolExamParentMapper examPMapper;

    @Value("${school_id}")
    private String schoolId;

    @Autowired
    private SchoolPeriodMapper schoolPeriodMapper;

    @Autowired
    private SchoolExamMapper schoolExamMapper;




    /**
     * 转为Long
     * @return
     */
    private Long toLong(){
        return Long.valueOf(this.schoolId);
    }


    //验证excle sheet2中参数是否正确
    @Override
    public Map checkExcle(MultipartFile multipartFile) {

        Map map = new HashMap();
        //获取文件名字
        String fileName = multipartFile.getOriginalFilename();
        //创建工作薄
        Workbook excel = null;
        try {
            excel = ImportExeclUtil.chooseWorkbook(fileName, multipartFile.getInputStream());



        //获取sheet数量
            int sheets = excel.getNumberOfSheets();
            if(sheets>1){
                //创建实体类
                CheckExcle checkExcle = new CheckExcle();
                List<CheckExcle> checkExcles = null;
                //读取最后一个sheet中的参数
                try {
                    checkExcles = ImportExeclUtil.readDateListT(excel, checkExcle, 1, 0, 1);
                } catch (Exception e) {
                    log.error("checkExcle====数据解析失败");
                    e.printStackTrace();
                }

                //参数存在
                if(checkExcles.size()>0){
                    checkExcle = checkExcles.get(0);
                    //获取考试名称
                    Long examParentId = examPMapper.findExamParentId(checkExcle.getExamName());
                    if(examParentId != null){
                        //获取学段
                        Long periodId = schoolPeriodMapper.selectPeriodIdByPeriodName(checkExcle.getPeriodName()).getPeriodId();
                        if(periodId != null){
                            //根据考试父ID和学段ID和届次查询此次考试的班级

                            map.put("examParentId",examParentId);
                            map.put("periodId",periodId);
                            map.put("thetime",checkExcle.getThetime());

                            List<Long> classIds = schoolExamMapper.findClassIds(map);

                            if(classIds.size() == 0){
                                map.put("msg",checkExcle.getExamName()+checkExcle.getPeriodName()+checkExcle.getThetime()+"届没有班级参加考试");
                                return map;
                            }else {
                                map.put("modelNumber",checkExcle.getModelNumber());
                            }
                        }else {
                            map.put("msg","学段不存在");
                            return map;
                        }
                    }else {
                        map.put("msg","考试不存在");
                        return map;
                    }
                }
            }

        } catch (IOException e) {
            log.error("checkExcle=====获取文件流失败");
            e.printStackTrace();
        }

        if (map.containsKey("examParentId") && map.containsKey("periodId") && map.containsKey("thetime")){

        }else  if (map.containsKey("msg")){
        }else {

            map.put("msg","模板里面数据不对，使用传入的数据");
        }

        return map;
    }

    /**
     * 导入成绩信息
     */
    @Override
    @Transactional
    public ApiResult bathImportInfo(MultipartFile file, InputStream in, Map params, Long operatorId, Integer modelNumber) {
        ApiResult apiResult = new ApiResult();
        if (modelNumber == 1) {
            //模版一
            return modelOne(file, in, params, operatorId);
        } else if (modelNumber == 2) {
            //模版二
            return modelTwo(file, in, params, operatorId);
        } else {

        }
        apiResult.setCode(ApiCode.import_failed);
        apiResult.setData(modelNumber);
        apiResult.setMsg("模板编号错误" + modelNumber);
        return apiResult;
    }
    //检查考试/学籍号/课程 是否存在成绩 参数
    Map map = new HashMap();

    ////////////////////////////////////模版一 /////////////////////////////////////
    private ApiResult  modelOne(MultipartFile file, InputStream in, Map params, Long operatorId){
        ApiResult apiResult = new ApiResult();
        //获取文件名字
        String fileName = file.getOriginalFilename();
        //创建工作薄
        Workbook excel = null;
        //届次
        Date tempThetime = TimeToString.StrToDate2((String) params.get("thetime"));
        //父节点
        System.out.println("届次：  " + tempThetime);
        try {
            excel = ImportExeclUtil.chooseWorkbook(fileName, in);
        } catch (IOException e) {
            log.error("score1===读取文件失败");
            e.printStackTrace();
            apiResult.setCode(ApiCode.import_failed);
            apiResult.setMsg("读取文件失败，无法导入");
            return apiResult;
        }
        int sheets = excel.getNumberOfSheets();
//        for(int i = 0; i < sheets; i++){
            //创建导入实体
            StudentScoreInfo score = new StudentScoreInfo();
            List<StudentScoreInfo> scoreInfo = null;
            try {
                scoreInfo = ImportExeclUtil.readDateListT(excel, score, 1, 0, 0);
            } catch (Exception e) {
                log.error("score1===读取数据失败");
                e.printStackTrace();
                apiResult.setCode(ApiCode.import_failed);
                apiResult.setMsg("读取文件失败，无法导入");
                return apiResult;
            }
            System.out.println(scoreInfo.toString());
            //创建时间
            Date time = new Date();
            //保存标题
            StudentScoreInfo title = scoreInfo.get(0);
            //分值上限处理
//            boolean isRight = StringHandler.isRight(scoreInfo, title);
            //程序开始时间
            Long start = System.currentTimeMillis();
//System.out.println(isRight == true ? "存在分数违法" : "所有分数正常");
            //暂存科目名
            List<String> subList = StringHandler.returnSubjectName(title);
            System.out.println(subList);
            //创建时间
            Date createTime = new Date();
            //判断标志
            int successAdd = 0;
            int successUpdate = 0;
            //插入集合
            List<StudentScore> listScoreAdd = new ArrayList<StudentScore>();
            //更新集合
            List<StudentScore> listScoreUpdate = new ArrayList<StudentScore>();

            //excle类循环
            for(int k = 1, len_info = scoreInfo.size(); k < len_info; k++){
                System.out.println("============================k======"+k+" ============================================");
                //科目分数
                List<BigDecimal> subjectScore = StringHandler.saveSubjectScore(scoreInfo.get(k));
                //科目循环
                for(int j = 0, len_sub = subList.size(); j < len_sub; j++){ //涉及科目ID,无科目分数
                    //科目名
                    Long subjectId = this.subjectMapper.selectBySubNameReturnId(subList.get(j));
System.out.println("科目ID " + subjectId + "科目名： " + subList.get(j));
                    //科目查重,即存在该科目成绩,无法导入
//                        System.out.println("科目ID： " + subjectId + " 学籍号" + scoreInfo.get(k).getRegisterNumber());

                    //查询参数设置
                    map.put("examParentId",(Long)params.get("examParentId"));
                    map.put("registerNumber",scoreInfo.get(k).getRegisterNumber());
                    map.put("subjectId",subjectId);
                    int isCopy = this.scoreMapper.selectByCountScoreId(map);


                    StudentOsaas info = this.osaasMapper.selectByRegisterNumber(scoreInfo.get(k).getRegisterNumber());
                    if (info == null){
                        apiResult.setCode(ApiCode.import_failed);
                        apiResult.setMsg("学籍号：" + scoreInfo.get(k).getRegisterNumber() + "  该数据有误！");
                        return apiResult;
                    }

                    SchoolExam exam = this.examMapper.selectByParentIdAndSubjectName(
                            (Long)params.get("examParentId"), subList.get(j), info.getClassId());
                    if(exam == null) {
                        apiResult.setCode(ApiCode.import_failed);
                        apiResult.setData(info);
                        apiResult.setMsg("无法导入，" +  "班级:" + info.getPeriodIdName() + info.getBelongToYear() +
                                "届" + info.getClassName()  + "  学籍号：" + info.getRegisterNumber() + "  姓名：" +
                                info.getFullName() + "  请检查该数据是否正确");
                        return apiResult;
                    }
//System.out.println(isCopy > 0 ? "重复数据" : "可以插入");
                    if(isCopy > 0){   //更新
                        //创建分数对象
                        StudentScore stuScore = new StudentScore();
                        stuScore.setSchoolId(toLong());
                        //TODO:前台获取,考试ID
                        stuScore.setExamId(exam.getExamId());
                        stuScore.setExamParentId((Long)params.get("examParentId"));
                        stuScore.setStudentName(scoreInfo.get(k).getStudentName());
                        stuScore.setRegisterNumber(scoreInfo.get(k).getRegisterNumber());
                        //TODO:学期前端单选
                        stuScore.setSchoolStageId((String)params.get("schoolStageId"));
                        stuScore.setSchoolSubjectId(subjectId);
                        //提取总分
                        stuScore.setScore(subjectScore.get(j));
                        //创建时间
                        stuScore.setCreateTime(createTime);
                        stuScore.setFounderId(operatorId);
                        //班级ID
//                        stuScore.setClassId((Long)params.get("classId"));
                        stuScore.setClassId(info.getClassId());
                        //设置届次
                        stuScore.setThetime(tempThetime);
                        stuScore.setOperatorId(operatorId);
                        try {
                            successUpdate = this.scoreMapper.updateByPrimaryBySome(stuScore);
                            if (successUpdate == 0){
                                apiResult.setCode(ApiCode.import_failed);
                                apiResult.setData(info);
                                apiResult.setMsg("无法导入，学籍号："+ info.getRegisterNumber() +"  请检查该数据是否正确");
                                return apiResult;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            apiResult.setCode(ApiCode.import_failed);
                            apiResult.setData(info);
                            apiResult.setMsg("无法导入，学籍号："+ info.getRegisterNumber() +"  请检查该数据是否正确");
                            return apiResult;
                        }
System.out.println(stuScore.toString());
                    }else{   //插入
                        //创建分数对象
                        StudentScore stuScore = new StudentScore();
                        stuScore.setSchoolId(toLong());
                        //TODO:前台获取,考试ID
//                        stuScore.setExamId((Long)params.get("eaxmId"));
                        if(exam != null) stuScore.setExamId(exam.getExamId());
//                        SchoolExamParent parent = this.examPMapper.selectByPrimaryKey((Long)params.get("examParentId"));
                        stuScore.setExamParentId((Long)params.get("examParentId"));
                        stuScore.setStudentName(scoreInfo.get(k).getStudentName());
                        stuScore.setRegisterNumber(scoreInfo.get(k).getRegisterNumber());
                        //TODO:学期前端单选
                        stuScore.setSchoolStageId((String)params.get("schoolStageId"));
//System.out.println("subList.get(j) ： " + subjectId);
                        stuScore.setSchoolSubjectId(subjectId);
                        //提取总分
                        stuScore.setScore(subjectScore.get(j));
                        //创建时间
                        stuScore.setCreateTime(createTime);
                        stuScore.setFounderId(operatorId);

                        //班级ID
//                        stuScore.setClassId((Long)params.get("classId"));
                        stuScore.setClassId(info.getClassId());
                        //设置届次
                        stuScore.setThetime(tempThetime);
                        stuScore.setFounderId(operatorId);
                        listScoreAdd.add(stuScore);
                    }
                }
            }

            //考试父节点
            //更新
//            if(!listScoreUpdate.isEmpty()){
//                successUpdate = this.scoreMapper.updateBatchInfo(listScoreUpdate);
//            }
//            //插入
            if(!listScoreAdd.isEmpty()){
                successAdd = this.scoreMapper.insertBathInfo(listScoreAdd);
            }
            if(successUpdate > 0 || successAdd > 0) {
                apiResult.setCode(ApiCode.import_success);
                apiResult.setMsg(ApiCode.import_success_MSG);
                return apiResult;
            }
//        }

        apiResult.setCode(ApiCode.import_failed);
        apiResult.setMsg(ApiCode.import_failed_MSG);
        return apiResult;
    }
    ////////////////////////////////////模版一 /////////////////////////////////////


    ////////////////////////////////////模版二 /////////////////////////////////////
    private ApiResult modelTwo(MultipartFile file, InputStream in, Map params, Long operatorId) {
        ApiResult apiResult = new ApiResult();
        //获取文件名字
        String fileName = file.getOriginalFilename();
        //创建工作薄
        Workbook excel = null;
        //届次
        Date tempThetime = TimeToString.StrToDate2((String) params.get("thetime"));
        //父节点
        System.out.println("届次：  " + tempThetime);
        try {
            excel = ImportExeclUtil.chooseWorkbook(fileName, in);
        } catch (IOException e) {
            log.error("读取文件失败");
            e.printStackTrace();
            apiResult.setCode(ApiCode.import_failed);
            apiResult.setMsg("读取文件失败，无法导入");
            return apiResult;
        }
        int sheets = excel.getNumberOfSheets();

//        for(int i = 0; i < sheets; i++){
        //创建导入实体
        ScoreModelTwoInfo score = new ScoreModelTwoInfo();
        List<ScoreModelTwoInfo> scoreInfo = null;
        try {
            scoreInfo = ImportExeclUtil.readDateListT(excel, score, 1, 0, 0);
        } catch (Exception e) {
            log.error("读取数据失败");
            e.printStackTrace();
            apiResult.setCode(ApiCode.import_failed);
            apiResult.setMsg("读取文件失败，无法导入");
            return apiResult;
        }

        //判断标志
        int successAdd = 0;
        int successUpdate = 0;
        //标题
        ScoreModelTwoInfo title = scoreInfo.get(0);
        //科目名字
        List<String> subjectName = StringHandler.returnSubjectName2(title);
        //插入集合
        List<StudentScore> listScoreAdd = new ArrayList<StudentScore>();
        for (int j = 2; j < scoreInfo.size(); j++) {
            System.out.println(scoreInfo.get(j).toString());
            List<BigDecimal> subjectScore = StringHandler.saveSubjectScore2(scoreInfo.get(j));
            for (int k = 0; k < subjectName.size(); k++) {
                //科目名
                Long subjectId = this.subjectMapper.selectBySubNameReturnId(subjectName.get(k));
                System.out.println("信息：" + scoreInfo.get(j).toString());
                StudentOsaas info = this.osaasMapper.selectByRegisterNumber(scoreInfo.get(j).getRegisterNumber());
                System.out.println("学生信息：" + info.toString());
                System.out.println("参数：" + (Long) params.get("examParentId") + "  " + subjectName.get(k) + "  " + info.getClassId());
                SchoolExam exam = this.examMapper.selectByParentIdAndSubjectName((Long) params.get("examParentId")
                        , subjectName.get(k), info.getClassId());

                //查询参数设置
                map.put("examParentId",(Long)params.get("examParentId"));
                map.put("registerNumber",scoreInfo.get(k).getRegisterNumber());
                map.put("subjectId",subjectId);
                int isCopy = this.scoreMapper.selectByCountScoreId(map);
                if (exam == null) {
                    apiResult.setCode(ApiCode.import_failed);
                    apiResult.setData(info);
                    apiResult.setMsg("无法导入，" +  "班级:" + info.getPeriodIdName() + info.getBelongToYear() + "届" +
                            info.getClassName()  + "  学籍号：" + info.getRegisterNumber() + "  姓名：" +
                            info.getFullName() + "  请检查该数据是否正确");
                    return apiResult;
                }
                if (isCopy > 0) { //更新
                    //创建分数对象
                    StudentScore stuScore = new StudentScore();
                    stuScore.setSchoolId(toLong());
                    stuScore.setExamParentId((Long) params.get("examParentId"));
                    stuScore.setStudentName(scoreInfo.get(j).getStudentName());
                    stuScore.setRegisterNumber(scoreInfo.get(j).getRegisterNumber());
                    stuScore.setSchoolStageId((String) params.get("schoolStageId"));
                    stuScore.setSchoolSubjectId(subjectId);
                    stuScore.setExamId(exam.getExamId());
                    //提取总分
                    System.out.println("科目ID " + subjectId + "科目名： " + subjectName.get(k) + "科目分数： " + subjectScore.get(k));
                    stuScore.setScore(subjectScore.get(k));
                    //创建时间
                    stuScore.setCreateTime(new Date());
                    stuScore.setFounderId(operatorId);
                    System.out.println("班级ID：" + (Long) params.get("classId"));
                    //班级ID
                    stuScore.setClassId(info.getClassId());
                    //设置届次
                    stuScore.setThetime(tempThetime);
                    stuScore.setOperatorId(operatorId);
                    try {
                        successUpdate = this.scoreMapper.updateByPrimaryBySome(stuScore);
                        if (successUpdate == 0){
                            apiResult.setCode(ApiCode.import_failed);
                            apiResult.setData(info);
                            apiResult.setMsg("无法导入，学籍号："+ info.getRegisterNumber() +"  请检查该数据是否正确");
                            return apiResult;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        apiResult.setCode(ApiCode.import_failed);
                        apiResult.setData(info);
                        apiResult.setMsg("无法导入，学籍号："+ info.getRegisterNumber() +"  请检查该数据是否正确");
                        return apiResult;
                    }
                } else { //插入
                    //创建分数对象
                    StudentScore stuScore = new StudentScore();
                    stuScore.setSchoolId(toLong());
                    //考试ID
                    stuScore.setExamId(exam.getExamId());
                    stuScore.setStudentName(scoreInfo.get(j).getStudentName());
                    stuScore.setRegisterNumber(scoreInfo.get(j).getRegisterNumber());
                    stuScore.setSchoolStageId((String) params.get("schoolStageId"));
                    stuScore.setSchoolSubjectId(subjectId);
                    //提取总分
                    System.out.println("科目ID " + subjectId + "科目名： " + subjectName.get(k) + "科目分数： " + subjectScore.get(k));
                    stuScore.setScore(subjectScore.get(k));
                    //创建时间
                    stuScore.setCreateTime(new Date());
                    stuScore.setFounderId(operatorId);
                    //班级ID
                    System.out.println("班级ID：" + (Long) params.get("classId"));
                    stuScore.setClassId(info.getClassId());
                    //设置届次
                    stuScore.setThetime(tempThetime);
                    stuScore.setOperatorId(operatorId);
                    System.out.println(stuScore.toString());
                    listScoreAdd.add(stuScore);
                }
            }
        }

        if (!listScoreAdd.isEmpty()) {
            successAdd = this.scoreMapper.insertBathInfo(listScoreAdd);
        }
        if (successUpdate > 0 || successAdd > 0){
            apiResult.setCode(ApiCode.import_success);
            apiResult.setMsg(ApiCode.import_success_MSG);
            return apiResult;
        }
//         }
        apiResult.setCode(ApiCode.import_failed);
        apiResult.setMsg(ApiCode.import_failed_MSG);
        return apiResult;
    }
    ////////////////////////////////////模版二 /////////////////////////////////////


}
