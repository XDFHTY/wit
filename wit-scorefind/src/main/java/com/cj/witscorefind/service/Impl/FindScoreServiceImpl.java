package com.cj.witscorefind.service.Impl;

import com.cj.witbasics.entity.*;
import com.cj.witbasics.entity.Grade;
import com.cj.witbasics.mapper.*;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.entity.ApiResultUtil;
import com.cj.witcommon.entity.ClassGradeInfo;
import com.cj.witcommon.utils.TimeToString;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witcommon.utils.excle.exportExcelUtil;
import com.cj.witscorefind.entity.*;
import com.cj.witscorefind.mapper.ScoreMapper;
import com.cj.witscorefind.service.FindScoreService;
import com.cj.witscorefind.sort.ListUtils;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class FindScoreServiceImpl implements FindScoreService {

    private static final Logger log = LoggerFactory.getLogger(FindScoreServiceImpl.class);

    @Autowired(required = false)
    private StudentScoreMapper studentScoreMapper;

    @Autowired(required = false)
    private ClassSubjectInfoMapper clsInfo;

    @Autowired(required = false)
    private AdminInfoMapper adminInfoMapper;

    @Autowired(required = false)
    private PeriodDirectorThetimeMapper directorTimeMapper;

    @Autowired(required = false)
    private SchoolPeriodClassThetimeMapper classThetimeMapper;

    @Autowired(required = false)
    private SchoolClassMapper classMapper;

    /**
     * =========================================
     */
    @Autowired
    private ScoreMapper scoreMapper;

    Gson gson = new Gson();
    /***
     * 查询考试-班级-成绩 统计
     */
    @Override
    public List<ExamClassScore> findExamClassScoreCount(HttpSession session,Map map){
        Long adminId = (Long)session.getAttribute("adminId");
        Long roleId = (Long)session.getAttribute("roleId");
        List<ExamClassScore> examClassScores = new ArrayList<>();  //结果集
        Long periodId;  //学段ID
        String thetime; //届次
        Long classId;   //班级ID
        Long subjectId;   //课程ID
        Iterator<ExamClassScore> it;

        switch (roleId.intValue()){
            case 21:  //年级主任
                //查询学段、届次信息
                Map map1 = scoreMapper.findPeriodIdAndThetimeByAdminId(adminId);
                if(map1 == null){  //不是年级主任
                    return new ArrayList<>();
                }
                //查询信息
                examClassScores = scoreMapper.findAllGradeClassByExamAndthetime(map);
                //处理数据
                periodId = (Long)map1.get("periodId");
                thetime = (String)map1.get("thetime");

                //迭代器删除不需要的数据
                it = examClassScores.iterator();
                while(it.hasNext()){
                    ExamClassScore examClassScore = it.next();
                    if(examClassScore.getClassPeriodId() != periodId || !examClassScore.getThetime().equals(thetime)){

                        it.remove();
                    }
                }

                break;
            case 4:  //班级主任
                //查询学段、届次、班级信息
                Map map2 = scoreMapper.findPeriodIdAndThetimeAndClassIdByAdminId(adminId);
                if(map2 == null){  //不是班级主任
                    return new ArrayList<>();
                }
                //查询信息
                examClassScores = scoreMapper.findAllGradeClassByExamAndthetime(map);
                //处理数据
                periodId = (Long)map2.get("periodId");
                thetime = (String)map2.get("thetime");
                classId = (Long) map2.get("classId");

                it = examClassScores.iterator();
                while(it.hasNext()){
                    ExamClassScore examClassScore = it.next();
                    if(examClassScore.getClassPeriodId() != periodId || !examClassScore.getThetime().equals(thetime) ||examClassScore.getClassId() != classId){

                        it.remove();
                    }
                }

                break;
            case 5:  //科目教师
                //查询课程ID、班级ID集合
                List<Map> list = scoreMapper.findSubjectIdAndClassIdsByAdminId(adminId);
                if(list == null ||list.size() ==0){ //不是科目教师
                    return new ArrayList<>();
                }
                //查询信息
                examClassScores = scoreMapper.findAllGradeClassByExamAndthetime(map);
                //处理数据
                it = examClassScores.iterator();
                while(it.hasNext()){
                    ExamClassScore examClassScore = it.next();
                    for (Map map3:list){
                        classId = (Long) map3.get("classId");
                        subjectId = (Long) map3.get("classId");
                        if(examClassScore.getClassId()!=classId || examClassScore.getSubjectId()!=subjectId){
                            it.remove();
                        }
                    }
                }
                break;
        }

        return examClassScores;

    }

    /**
     * 成绩单-年级/班级/单科/个人
     * @param session
     * @param pager
     * @return
     */
    @Override
    public Pager findExamClassTotalScore(HttpSession session, Pager pager) {
        Long adminId = (Long)session.getAttribute("adminId");
        Long roleId = (Long)session.getAttribute("roleId");

        //查询本次考试数据
        Map map1 = new LinkedHashMap();
        map1.put("examParentId",pager.getParameters().get("newExamParentId"));
        map1.put("periodId",pager.getParameters().get("periodId"));
        map1.put("thetime",pager.getParameters().get("thetime"));
        map1.put("classTypeId",pager.getParameters().get("classTypeId"));
        map1.put("classId",pager.getParameters().get("classId"));
        map1.put("subjectId",pager.getParameters().get("subjectId"));
        map1.put("gradeType",1);  //查询总分档次用

        //查询本次考试总分 年级-班级排名
        pager.getParameters().put("examParentId",pager.getParameters().get("newExamParentId"));
        //TODO:本次考试学生总分排名信息
        List<List<?>> lists1 = scoreMapper.findExamClassTotalScore(pager);

        //TODO:本次考试第一页学生学籍号
        List<String> registerNumbers = new ArrayList<>();

        for (ExamClassSubjectScore examClassSubjectScore : (List<ExamClassSubjectScore>)lists1.get(0)){
            registerNumbers.add(examClassSubjectScore.getRegisterNumber());
        }

        System.out.println(registerNumbers);
        //查询学生此次考试的各科成绩-年级-班级 排名
        pager.getParameters().put("registerNumbers",registerNumbers);
        //TODO:本次考试第一页的学生的单科成绩及排名
        List<List<?>> lists3 = scoreMapper.findExamClassSubjectScore(pager);

        //查询对比考试总分 年级-班级排名
        pager.getParameters().put("examParentId",pager.getParameters().get("oldExamParentId"));
        //TODO:对比考试(此次考试第一页的)学生的总分成绩及排名
        List<List<?>> lists2 = scoreMapper.findExamClassTotalScore(pager);

        //查询对比考试各科成绩
        //TODO:对比考试(此次考试第一页的)学生的单科成绩排名
        List<List<?>> lists4 = scoreMapper.findExamClassSubjectScore(pager);




        //结果集处理
        List<Map> list = (List<Map>) lists1.get(1);

        Long total = (Long) list.get(0).get("total");
        //总条数
        pager.setRecordTotal(total.intValue());

        //TODO:本次考试学生总分信息
        List<ExamClassSubjectScore> examClassSubjectScores1 = (List<ExamClassSubjectScore>)lists1.get(0);
        //TODO:对比考试学生总分信息
        List<ExamClassSubjectScore> examClassSubjectScores2 = (List<ExamClassSubjectScore>)lists2.get(0);

        //TODO:处理总分名次
        for(ExamClassSubjectScore examClassSubjectScore1 : examClassSubjectScores1){
            examClassSubjectScore1.setSubjectName("总分");
            for (ExamClassSubjectScore examClassSubjectScore2 : examClassSubjectScores2){


                //学生匹配
                if(examClassSubjectScore1.getOsaasId() == examClassSubjectScore2.getOsaasId()){
                    //年级排名进步名次
                    examClassSubjectScore1.setYearRankRaise(examClassSubjectScore2.getYearRank()-examClassSubjectScore1.getYearRank());
                    //班级排名进步名次
                    examClassSubjectScore1.setClassRankRaise(examClassSubjectScore2.getClassRank()-examClassSubjectScore1.getClassRank());

                }

            }

        }
        //TODO:查询总分档次
        List<ExamThetimeGrade> examThetimeGrades = scoreMapper.findExamThetimeSubjectGrade(map1);

        //TODO:处理总分档次
        for (ExamClassSubjectScore examClassSubjectScore : examClassSubjectScores1){

            for (ExamThetimeGrade examThetimeGrade : examThetimeGrades){
                //如果考试、学段、届次 匹配
                if(
                        examClassSubjectScore.getExamParentId()==examThetimeGrade.getExamParentId()
                                &&
                                examClassSubjectScore.getPeriodId()==examThetimeGrade.getPeriodId()
                                &&
                                examClassSubjectScore.getThetime().equals(examThetimeGrade.getThetime())


                        ){
                    //设置此学生的年级 分数档次和名次档次
                    //获取此学生总分数
                    Double score = examClassSubjectScore.getTotalScore().doubleValue();
                    //获取此学生年级排名
                    int yearRank = examClassSubjectScore.getYearRank();
                    if(examThetimeGrade.getGGradeType().equals("1")){  //处理分数档次
                        //不含头，处理完成剩余的就是满分
                        if(score< examThetimeGrade.getNumMax() && score >= examThetimeGrade.getNumMin()){
                            examClassSubjectScore.setScoreGrade(examThetimeGrade.getGradeName());
                        }
                    }
                    if(examThetimeGrade.getGGradeType().equals("2")){  //处理名次档次
                        //含头含尾
                        if(yearRank >= examThetimeGrade.getNumMax() && yearRank <= examThetimeGrade.getNumMin()){
                            examClassSubjectScore.setRankGrade(examThetimeGrade.getGradeName());
                        }
                    }

                }

            }
        }

        //添加学生单科成绩
//        for (ExamClassSubjectScore examClassSubjectScore : examClassSubjectScores1){
//            List<ExamSubjectScore> examSubjectScoreList = new ArrayList<>();
//            for (ExamSubjectScore examSubjectScore : examSubjectScores){
//                if(examClassSubjectScore.getRegisterNumber().equals(examSubjectScore.getRegisterNumber())){  //学籍号匹配
//                    examSubjectScoreList.add(examSubjectScore);
//                }
//
//            }
//            examClassSubjectScore.setExamSubjectScores(examSubjectScoreList);
//        }

        //TODO:本次考试第一页学生单科成绩
        List<ExamClassSubjectScore> examClassSubjectScores3 = (List<ExamClassSubjectScore>)lists3.get(0);
        //TODO:对比考试第一页学生单科成绩
        List<ExamClassSubjectScore> examClassSubjectScores4 = (List<ExamClassSubjectScore>)lists4.get(0);
        //TODO:处理单科名次
        for (ExamClassSubjectScore examClassSubjectScore3 : examClassSubjectScores3){
            for (ExamClassSubjectScore examClassSubjectScore4 :examClassSubjectScores4){
                //课程匹配
                if(examClassSubjectScore3.getSubjectId()==examClassSubjectScore4.getSubjectId()){
                    //学生匹配
                    if(examClassSubjectScore3.getOsaasId()==examClassSubjectScore4.getOsaasId()){
                        //年级进步名次
                        examClassSubjectScore3.setYearRankRaise(examClassSubjectScore4.getYearRank()-examClassSubjectScore3.getYearRank());
                        //班级进步名次
                        examClassSubjectScore3.setClassRankRaise(examClassSubjectScore4.getClassRank()-examClassSubjectScore3.getClassRank());
                    }

                }
            }

        }

        //TODO:查询单科档次
        map1.put("gradeType",3);
        List<ExamThetimeGrade> examThetimeGradesSubject = scoreMapper.findExamThetimeSubjectGrade(map1);

        //TODO:处理单科档次
        for (ExamClassSubjectScore examClassSubjectScore3 : examClassSubjectScores3){
            for (ExamThetimeGrade examThetimeGrade : examThetimeGradesSubject){
                //如果考试、学段、届次、班级、课程匹配
                if(
                        examClassSubjectScore3.getExamParentId()==examThetimeGrade.getExamParentId()
                                &&
                                examClassSubjectScore3.getPeriodId()==examThetimeGrade.getPeriodId()
                                &&
                                examClassSubjectScore3.getThetime().equals(examThetimeGrade.getThetime())
                                &&
                                examClassSubjectScore3.getClassId()==examThetimeGrade.getClassId()
                                &&
                                examClassSubjectScore3.getSubjectId()==examThetimeGrade.getSubjectId()

                        ){
                    //设置此学生的年级 分数档次和名次档次
                    //获取此学生此次考试分数
                    Double score = examClassSubjectScore3.getScore().doubleValue();
                    //获取此学生此次考试年级排名
                    int yearRank = examClassSubjectScore3.getYearRank();
                    if(examThetimeGrade.getGGradeType().equals("1")){  //处理分数档次
                        //不含头，处理完成剩余的就是满分
                        if(score< examThetimeGrade.getNumMax() && score >= examThetimeGrade.getNumMin()){
                            examClassSubjectScore3.setScoreGrade(examThetimeGrade.getGradeName());
                        }
                    }
                    if(examThetimeGrade.getGGradeType().equals("2")){  //处理名次档次
                        //含头含尾
                        if(yearRank >= examThetimeGrade.getNumMax() && yearRank <= examThetimeGrade.getNumMin()){
                            examClassSubjectScore3.setRankGrade(examThetimeGrade.getGradeName());
                        }
                    }

                }

            }
        }


        //TODO:将单科成绩名次-档次等添加到总分
        for (ExamClassSubjectScore examClassSubjectScore5 : examClassSubjectScores1){

            List<ExamClassSubjectScore>  examClassSubjectScoreList = new ArrayList<>();

            for (ExamClassSubjectScore examClassSubjectScore6 : examClassSubjectScores3){
                //学生匹配
                if(examClassSubjectScore5.getOsaasId()==examClassSubjectScore6.getOsaasId()){
                    //添加
                    examClassSubjectScoreList.add(examClassSubjectScore6);
                }
            }

            examClassSubjectScore5.setExamClassSubjectScores(examClassSubjectScoreList);
        }

        System.out.println(examClassSubjectScores1);



        pager.setContent(examClassSubjectScores1);

        return pager;
    }

    /**
     * 班级学生姓名、学号、总分成绩、班级排名、班级进步名次（退步用负数表示）、年级排名、年级进步名次（退步用负数表示）、学科成绩、档次（A、B、C档）
     * @param session
     * @param map
     * @return
     */
    @Override
    public Pager findExamClassSubjectScore(HttpSession session, Pager pager) {
        Long adminId = (Long)session.getAttribute("adminId");
        Long roleId = (Long)session.getAttribute("roleId");

        //查询本次考试数据
        Map map1 = new LinkedHashMap();
        map1.put("examParentId",pager.getParameters().get("newExamParentId"));
        map1.put("periodId",pager.getParameters().get("periodId"));
        map1.put("thetime",pager.getParameters().get("thetime"));
        map1.put("classId",pager.getParameters().get("classId"));
        map1.put("classTypeId",pager.getParameters().get("classTypeId"));
        map1.put("subjectId",pager.getParameters().get("subjectId"));
        map1.put("gradeType",3);  //查询单科档次用



        //查询本次考试单科年级-班级排名信息
        pager.getParameters().put("examParentId",pager.getParameters().get("newExamParentId"));
        List<List<?>> lists1 = scoreMapper.findExamClassSubjectScore(pager);

        //结果集转换
        Map<String, List<?>> maps1 = new HashMap<String, List<?>>();  //存储
        if(lists1 != null && lists1.size()>1){  //两个结果集
            maps1.put("examClassSubjectScores",(List<ExamClassSubjectScore>)lists1.get(0));
            maps1.put("mapList",(List<Map>)lists1.get(1));

        }
        System.out.println(maps1);

        //获取学生Id
        List<Long> osaasIds = new ArrayList<>();
        for (ExamClassSubjectScore examClassSubjectScore : (List<ExamClassSubjectScore>)lists1.get(0)){
            osaasIds.add(examClassSubjectScore.getOsaasId());
        }

        //查询对比考试总分 年级-班级排名
        pager.getParameters().put("examParentId",pager.getParameters().get("oldExamParentId"));
        pager.getParameters().put("osaasIds",osaasIds);
        List<ExamClassSubjectScore> examClassSubjectScores2 = scoreMapper.findOldExamClassSubjectScore(pager);



        //查询本次考试档次参数信息
        List<ExamThetimeGrade> examThetimeGrades = scoreMapper.findExamThetimeSubjectGrade(map1);


        //结果集处理
        List<Map> list = (List<Map>) maps1.get("mapList");

        Long total = (Long) list.get(0).get("total");
        //总条数
        pager.setRecordTotal(total.intValue());
        //处理考试名次进步，档次
        List<ExamClassSubjectScore> examClassSubjectScores1 = (List<ExamClassSubjectScore>)maps1.get("examClassSubjectScores");
        for(ExamClassSubjectScore examClassSubjectScore1 : examClassSubjectScores1){
            for (ExamClassSubjectScore examClassSubjectScore2 : examClassSubjectScores2){


                if(examClassSubjectScore1.getOsaasId() == examClassSubjectScore2.getOsaasId()){
                    //年级排名进步名次
                    examClassSubjectScore1.setYearRankRaise(examClassSubjectScore2.getYearRank()-examClassSubjectScore1.getYearRank());
                    //班级排名进步名次
                    examClassSubjectScore1.setClassRankRaise(examClassSubjectScore2.getClassRank()-examClassSubjectScore1.getClassRank());

                }

            }

        }

        for (ExamClassSubjectScore examClassSubjectScore : examClassSubjectScores1){
            for (ExamThetimeGrade examThetimeGrade : examThetimeGrades){
                //如果考试、学段、届次、班级、课程匹配
                if(
                        examClassSubjectScore.getExamParentId()==examThetimeGrade.getExamParentId()
                                &&
                                examClassSubjectScore.getPeriodId()==examThetimeGrade.getPeriodId()
                                &&
                                examClassSubjectScore.getThetime().equals(examThetimeGrade.getThetime())
                                &&
                                examClassSubjectScore.getClassId()==examThetimeGrade.getClassId()
                                &&
                                examClassSubjectScore.getSubjectId()==examThetimeGrade.getSubjectId()

                        ){
                    //设置此学生的年级 分数档次和名次档次
                    //获取此学生分数
                    Double score = examClassSubjectScore.getScore().doubleValue();
                    //获取此学生年级排名
                    int yearRank = examClassSubjectScore.getYearRank();
                    if(examThetimeGrade.getGGradeType().equals("1")){  //处理分数档次
                        //不含头，处理完成剩余的就是满分
                        if(score< examThetimeGrade.getNumMax() && score >= examThetimeGrade.getNumMin()){
                            examClassSubjectScore.setScoreGrade(examThetimeGrade.getGradeName());
                        }
                    }
                    if(examThetimeGrade.getGGradeType().equals("2")){  //处理名次档次
                        //含头含尾
                        if(yearRank >= examThetimeGrade.getNumMax() && yearRank <= examThetimeGrade.getNumMin()){
                            examClassSubjectScore.setRankGrade(examThetimeGrade.getGradeName());
                        }
                    }

                }

            }
        }

        System.out.println(examClassSubjectScores1);
        pager.setContent(examClassSubjectScores1);


        return pager;
    }

    //关注学生统计
    @Override
    public Map findFollowStudents(long newExamParentId,long oldExamParentId,long classId,int followRank) {
        Map map = new HashMap();
        map.put("examParentId",newExamParentId);
        map.put("classId",classId);
        //此次考试班级排名
        List<ClassTotalScoreRank> newClassTotalScoreRanks = scoreMapper.findClassTotalScoreRank(map);
        //班级前N名
        List<ClassTotalScoreRank> front = new ArrayList<>();
        //班级后N名
        List<ClassTotalScoreRank> back = new ArrayList<>();
        if(followRank <= newClassTotalScoreRanks.size()){
            for (int i = 0; i < followRank; i++) {
                front.add(newClassTotalScoreRanks.get(i));

            }

            for (int i = newClassTotalScoreRanks.size()-1; i > newClassTotalScoreRanks.size()-1-followRank; i--) {
                back.add(newClassTotalScoreRanks.get(i));
            }


        }



        //对比考试信息
        map.put("examParentId",oldExamParentId);
        List<ClassTotalScoreRank> oldClassTotalScoreRanks = scoreMapper.findClassTotalScoreRank(map);
        //TODO:处理两次考试名次变化
        for (ClassTotalScoreRank classTotalScoreRank1 : newClassTotalScoreRanks){
            for (ClassTotalScoreRank classTotalScoreRank2 : oldClassTotalScoreRanks){
                if(classTotalScoreRank1.getRegisterNumber().equals(classTotalScoreRank2.getRegisterNumber())){
                    classTotalScoreRank1.setClassRankRaise(classTotalScoreRank2.getClassRank()-classTotalScoreRank1.getClassRank());
                }

            }
        }

        //排序
        Collections.sort(newClassTotalScoreRanks, new Comparator<ClassTotalScoreRank>() {
            @Override
            public int compare(ClassTotalScoreRank o1, ClassTotalScoreRank o2) {
                int i = o1.getClassRankRaise()-o2.getClassRankRaise();
                if(i == 0){
                    return o1.getTotalScore().compareTo(o2.getTotalScore());
                }
                return i;
            }
        });
        //进步最大的N名
        List<ClassTotalScoreRank> goForward = new ArrayList<>();
        //进步最大的N名
        List<ClassTotalScoreRank> backOff = new ArrayList<>();

        if(followRank <= newClassTotalScoreRanks.size()){
            for (int i = newClassTotalScoreRanks.size()-1; i > newClassTotalScoreRanks.size()-1-followRank; i--) {
                goForward.add(newClassTotalScoreRanks.get(i));
            }

            for (int i = 0; i < followRank; i++) {
                backOff.add(newClassTotalScoreRanks.get(i));
            }


        }
        //封装
        Map resultMap = new LinkedHashMap();
        resultMap.put("front",front);
        resultMap.put("back",back);
        resultMap.put("goForward",goForward);
        resultMap.put("backOff",backOff);
        return resultMap;
    }


    //优势学科统计（班级）
    @Override
    public List<SubjectAvgScore> findClassSuperioritySubject(long examParentId,long periodId,String thetime,long classId){
        Map map0 = new HashMap();
        map0.put("examParentId",examParentId);
        map0.put("periodId",periodId);
        map0.put("thetime",thetime);
        map0.put("classId",classId);
        map0.put("gradeType",3);

        List<SubjectAvgScore> result = new ArrayList<>();
        //查询班级单科优势分数线
        List<ExamThetimeGrade> examThetimeGrades = scoreMapper.findExamThetimeSubjectGrade(map0);

        Map map1 = new HashMap();
        //查询年级平均分
        map1.put("examParentId",examParentId);
        map1.put("periodId",periodId);
        map1.put("thetime",thetime);
        List<SubjectAvgScore> yearAvg = scoreMapper.findSubjectAvgScore(map1);

        //查询班级平均分
        map1.put("classId",classId);
        List<SubjectAvgScore> classAvg = scoreMapper.findSubjectAvgScore(map1);

        if(examThetimeGrades.size() == 0){  //没有设置班级优势分数线，取平均分做优势分数线



            for(SubjectAvgScore subjectAvgScore1 : yearAvg){

                Iterator<SubjectAvgScore> itAvg = classAvg.iterator();
                while (itAvg.hasNext()){
                    SubjectAvgScore subjectAvgScore = itAvg.next();
                    if(subjectAvgScore1.getSubjectId()==subjectAvgScore.getSubjectId()){
                        //班级平均分 大于年级平均分
                        if(subjectAvgScore.getSubjectAvgScore().compareTo(subjectAvgScore1.getSubjectAvgScore()) > 0){
                            result.add(subjectAvgScore);
                        }

                    }
                }

            }

        }else {  //设置过班级优势分数线

            for(ExamThetimeGrade examThetimeGrade : examThetimeGrades){

                Iterator<SubjectAvgScore> itAvg = classAvg.iterator();
                while (itAvg.hasNext()){
                    SubjectAvgScore subjectAvgScore = itAvg.next();

                    if(examThetimeGrade.getSubjectId()==subjectAvgScore.getSubjectId()){
                        //班级平均分 大于 班级优势分数线
                        if(!(subjectAvgScore.getSubjectAvgScore().compareTo(examThetimeGrade.getAdvantageScore()) > 0)){
                            result.add(subjectAvgScore);
                        }

                    }
                }


            }

        }





        return result;
    }

    //优势学科统计（学生）
    @Override
    public Object findStudentSuperioritySubject(long examParentId,long periodId,String thetime,String registerNumber) {
        Map map0 = new HashMap();
        map0.put("examParentId",examParentId);
        map0.put("periodId",periodId);
        map0.put("thetime",thetime);
        map0.put("gradeType",4);

        List<SubjectAvgScore> result = new ArrayList<>();
        //查询年级单科优势分数线
        List<ExamThetimeGrade> examThetimeGrades = scoreMapper.findExamThetimeSubjectGrade(map0);
        //查询年级单科平均分
        List<SubjectAvgScore> yearAvg = scoreMapper.findSubjectAvgScore(map0);

        //查询此次考试学生各科成绩
        Pager pager = new Pager();
        List<String> registerNumbers = new ArrayList<>();
        registerNumbers.add(registerNumber);
        map0.put("registerNumbers",registerNumbers);
        pager.setParameters(map0);
        List<ExamSubjectScore> examSubjectScores = scoreMapper.findExamSubjectScore(pager);


        //优势学科
        List<String> list1 = new ArrayList<>();
        //劣势学科
        List<String> list2 = new ArrayList<>();

        if(examThetimeGrades.size() != 0){  //已设置年级单科优势分数线
            for(SubjectAvgScore subjectAvgScore : yearAvg){
                for (ExamSubjectScore examSubjectScore : examSubjectScores){

                    if(subjectAvgScore.getSubjectId()==examSubjectScore.getSubjectId()){
                        if(examSubjectScore.getScore().compareTo(subjectAvgScore.getSubjectAvgScore()) > 0){
                            list1.add(examSubjectScore.getSubjectName());
                        }else {
                            list2.add(examSubjectScore.getSubjectName());
                        }
                    }
                }
            }

        }

        Map map = new HashMap();
        map.put("preponderant",list1);
        map.put("Inferiority",list2);
        return map;
    }

    //查询此学生参加过并且有成绩的考试
    @Override
    public List<SchoolExamParents> findAllExamByClassOrStudent(Long classId , String registerNumber) {
        Map map = new HashMap();
        map.put("classId",classId);
        map.put("registerNumber",registerNumber);

        return scoreMapper.findAllExamByClassOrStudent(map);
    }

    //历次考试均分趋势图：折线图，班级均分与年级均分对比(班级总分)
    @Override
    public Map findAllExamClassTotalScoreChart(List<Long> examParentIds, Long classId) {
        //根据classId查询届次-班级类型
        long t1 = System.currentTimeMillis();
        Map map0 = scoreMapper.findThetimeAndclassTypeIdByClassId(classId);
        String thetime = (String) map0.get("thetime");
        int classTypeId = (Integer) map0.get("classTypeId");

        //查询年级历次考试有0分的学生学籍号，他们不参与平均分计算
        Map map1 = new HashMap();
        map1.put("examParentIds",examParentIds);
        map1.put("thetime",thetime);
        long t2 = System.currentTimeMillis();
        List<Exams> exams = scoreMapper.findAllRegisterNumberByScoreEq0(map1);

        for (Exams exams1 : exams){
            System.out.println(exams1.getExamParentId());
            for (SubjectIdByScoreEq0 subjectIdByScoreEq0 : exams1.getSubjectIdByScoreEq0s()){
//                System.out.println(subjectIdByScoreEq0.getSubjectId());
                for(RegisterNumberByScoreEq0 registerNumberByScoreEq0 : subjectIdByScoreEq0.getRegisterNumberByScoreEq0s()){
                    System.out.println(registerNumberByScoreEq0.getRegisterNumber());
                }
            }
        }

        Map map2 = new HashMap();
        map2.put("examParentIds",examParentIds);
        map2.put("thetime",thetime);
        map2.put("classTypeId",classTypeId);
        map2.put("exams",exams);
        //查询班级历次考试年级总分平均分
        long t3 = System.currentTimeMillis();
        List<ExamAvgTotalScore> examYearAvgTotalScores = scoreMapper.findExamAvgTotalScore(map2);


        //查询班级历次考试班级总分平均分
        map2.put("classId",classId);
        long t4 = System.currentTimeMillis();
        List<ExamAvgTotalScore> examClassAvgTotalScores = scoreMapper.findExamAvgTotalScore(map2);
        long t5 = System.currentTimeMillis();

        Map map = new HashMap();
        map.put("yearAvgTotalScore",examYearAvgTotalScores);
        map.put("classAvgTotalScore",examClassAvgTotalScores);

        System.out.println("耗时=========================="+(t2-t1));
        System.out.println("耗时=========================="+(t3-t2));
        System.out.println("耗时=========================="+(t4-t3));
        System.out.println("耗时=========================="+(t5-t4));
        return map;
    }

    //历次考试均分趋势图：折线图，班级均分与年级均分对比(班级各科)
    @Override
    public Map findAllExamClassSubjectScoreChart(List<Long> examParentIds , Long classId) {
        //根据classId查询届次-班级类型
        Map map0 = scoreMapper.findThetimeAndclassTypeIdByClassId(classId);
        String thetime = (String) map0.get("thetime");
        int classTypeId = (Integer) map0.get("classTypeId");

        //查询年级历次考试有0分的学生学籍号，他们不参与平均分计算
        Map map1 = new HashMap();
        map1.put("examParentIds",examParentIds);
        map1.put("thetime",thetime);
        List<Exams> exams = scoreMapper.findAllRegisterNumberByScoreEq0(map1);

        for (Exams exams1 : exams){
            System.out.println(exams1.getExamParentId());
            for (SubjectIdByScoreEq0 subjectIdByScoreEq0 : exams1.getSubjectIdByScoreEq0s()){
                System.out.println(subjectIdByScoreEq0.getSubjectId());
                for(RegisterNumberByScoreEq0 registerNumberByScoreEq0 : subjectIdByScoreEq0.getRegisterNumberByScoreEq0s()){
                    System.out.println(registerNumberByScoreEq0.getRegisterNumber());
                }
            }
        }
        //查询班级历次考试0分的学籍号，他们不参与班级总分平均分计算
//        map1.put("classId",classId);
//        exams = scoreMapper.findAllRegisterNumberByScoreEq0(map1);

        Map map2 = new HashMap();
        map2.put("examParentIds",examParentIds);
        map2.put("thetime",thetime);
        map2.put("classTypeId",classTypeId);
        map2.put("exams",exams);
        //查询班级历次考试年级各科平均分
        List<ExamAvgTotalScore> examYearAvgSubjectScores = scoreMapper.findExamAvgSubjectScore(map2);

        map2.put("classId",classId);
        //查询班级历次考试班级各科平均分
        List<ExamAvgTotalScore> examClassAvgSubjectScores = scoreMapper.findExamAvgSubjectScore(map2);

//        //TODO:处理历次考试年级总分及各科平均分
//        for (ExamAvgTotalScore examYearAvgTotalScore : examYearAvgTotalScores){
//
//            List<ExamAvgTotalScore> examAvgTotalScores = new ArrayList<>();
//
//            for (ExamAvgTotalScore examYearAvgSubjectScore : examYearAvgSubjectScores){
//
//                //考试匹配上则将各科平均分放入年级总分平均分子节点
//                if(examYearAvgTotalScore.getExamParentId() == examYearAvgSubjectScore.getExamParentId()){
//                    examAvgTotalScores.add(examYearAvgSubjectScore);
//
//                }
//
//            }
//
//            examYearAvgTotalScore.setExamAvgTotalScores(examAvgTotalScores);
//        }
//
//        //TODO:处理历次考试班级总分及各科平均分
//        for (ExamAvgTotalScore examClassAvgTotalScore : examClassAvgTotalScores){
//
//            List<ExamAvgTotalScore> examAvgTotalScores = new ArrayList<>();
//            for (ExamAvgTotalScore examClassAvgSubjectScore : examClassAvgSubjectScores){
//                //考试匹配上则将各科平均分放入年级总分平均分子节点
//                if(examClassAvgTotalScore.getExamParentId() == examClassAvgSubjectScore.getExamParentId()){
//                    examAvgTotalScores.add(examClassAvgSubjectScore);
//
//                }
//
//            }
//            examClassAvgTotalScore.setExamAvgTotalScores(examAvgTotalScores);
//        }

        Map map = new HashMap();
        map.put("yearAvgSubjectScore",examYearAvgSubjectScores);
        map.put("classAvgSubjectScore",examClassAvgSubjectScores);



        return map;
    }

    //历次考试班级-学生 各科成绩对比
    @Override
    public Map findAllExamStudentSubjectScoreChart(List<Long> examParentIds, String registerNumber) {

        //查询学生班级ID
        long classId = scoreMapper.findClassIdByRegisterNumber(registerNumber);
        //根据classId查询届次-班级类型
        Map map0 = scoreMapper.findThetimeAndclassTypeIdByClassId(classId);
        String thetime = (String) map0.get("thetime");
        int classTypeId = (Integer) map0.get("classTypeId");
        //查询年级历次考试有0分的学生学籍号，他们不参与平均分计算
        Map map1 = new HashMap();
        map1.put("examParentIds",examParentIds);
        map1.put("thetime",thetime);
        List<Exams> exams = scoreMapper.findAllRegisterNumberByScoreEq0(map1);

        Map map2 = new HashMap();
        map2.put("examParentIds",examParentIds);
        map2.put("thetime",thetime);
        map2.put("classTypeId",classTypeId);
        map2.put("exams",exams);
        map2.put("classId",classId);

        //查询班级历次考试班级各科平均分
        List<ExamAvgTotalScore> examClassAvgSubjectScores = scoreMapper.findExamAvgSubjectScore(map2);

        map2.put("registerNumber",registerNumber);
        //查询历次考试学生各科成绩
        List<ExamAvgTotalScore> examsSubjectScores = scoreMapper.findExamsSubjectScore(map2);

        Map map = new HashMap();
        /*map.put("classAvgSubjectScore",examClassAvgSubjectScores);
        map.put("studentSubjectScore",examsSubjectScores);*/

        //创建考试id集合
        Set set1 = new LinkedHashSet();
        if (examClassAvgSubjectScores!=null && examClassAvgSubjectScores.size()!=0){
            for (ExamAvgTotalScore totalScore:examClassAvgSubjectScores){
                if (totalScore != null) {
                    //把考试父id添加进集合
                    set1.add(totalScore.getExamParentId());
                }
            }
        }
        //创建返回集合
        List<ExamAvgTotalScoreList> list1 = new ArrayList<>();
        //遍历set
        if (set1 != null){
            Iterator<Long> it = set1.iterator();
            while (it.hasNext()) {
                Long examParentId = it.next();
                //创建返回对象
                ExamAvgTotalScoreList totalScore = new ExamAvgTotalScoreList();
                totalScore.setExamAvgTotalScoreList(new ArrayList<>());
                //添加id
                totalScore.setExamParentId(examParentId);
                //添加考试信息
                if (examClassAvgSubjectScores!=null && examClassAvgSubjectScores.size()!=0){
                    for (ExamAvgTotalScore info:examClassAvgSubjectScores){
                        if (info!=null && info.getExamParentId() == examParentId){
                            totalScore.getExamAvgTotalScoreList().add(info);
                            totalScore.setExamName(info.getExamName());
                        }
                    }
                }
                list1.add(totalScore);
            }
        }



        //创建考试id集合
        Set set2 = new LinkedHashSet();
        if (examsSubjectScores!=null && examsSubjectScores.size()!=0){
            for (ExamAvgTotalScore totalScore:examsSubjectScores){
                if (totalScore != null) {
                    //把考试父id添加进集合
                    set2.add(totalScore.getExamParentId());
                }
            }
        }
        //创建返回集合
        List<ExamAvgTotalScoreList> list2 = new ArrayList<>();
        //遍历set
        if (set2 != null){
            Iterator<Long> it = set1.iterator();
            while (it.hasNext()) {
                Long examParentId = it.next();
                //创建返回对象
                ExamAvgTotalScoreList totalScore = new ExamAvgTotalScoreList();
                totalScore.setExamAvgTotalScoreList(new ArrayList<>());
                //添加id
                totalScore.setExamParentId(examParentId);
                //添加考试信息
                if (examsSubjectScores!=null && examsSubjectScores.size()!=0){
                    for (ExamAvgTotalScore info:examsSubjectScores){
                        if (info!=null && info.getExamParentId() == examParentId){
                            totalScore.getExamAvgTotalScoreList().add(info);
                            totalScore.setExamName(info.getExamName());
                        }
                    }
                }
                list2.add(totalScore);
            }
        }


        map.put("classAvgSubjectScore",list1);
        map.put("studentSubjectScore",list2);



        return map;
    }

    //历次考试班级-学生 总分成绩对比
    @Override
    public Map findAllExamStudentTotalScoreChart(List<Long> examParentIds, String registerNumber) {


        //查询学生班级ID
        long classId = scoreMapper.findClassIdByRegisterNumber(registerNumber);
        //根据classId查询届次-班级类型
        Map map0 = scoreMapper.findThetimeAndclassTypeIdByClassId(classId);
        String thetime = (String) map0.get("thetime");
        int classTypeId = (Integer) map0.get("classTypeId");
        //查询年级历次考试有0分的学生学籍号，他们不参与平均分计算
        Map map1 = new HashMap();
        map1.put("examParentIds", examParentIds);
        map1.put("thetime", thetime);
        List<Exams> exams = scoreMapper.findAllRegisterNumberByScoreEq0(map1);

        Map map2 = new HashMap();
        map2.put("examParentIds", examParentIds);
        map2.put("thetime", thetime);
        map2.put("classTypeId", classTypeId);
        map2.put("exams", exams);
        map2.put("classId", classId);

        //查询班级历次考试班级总分平均分
        List<ExamAvgTotalScore> examClassAvgTotalScores = scoreMapper.findExamAvgTotalScore(map2);

        map2.put("registerNumber", registerNumber);
        //查询历次考试学生总分
        List<ExamAvgTotalScore> examStudentTotalScores = scoreMapper.findExamTotalScore(map2);


        Map map = new HashMap();
        map.put("classAvgTotalScore", examClassAvgTotalScores);
        map.put("studentTotalScore", examStudentTotalScores);
        return map;
    }

    //历次年级排名走势图
    @Override
    public Map findAllExamStudentYearRankChart(List<Long> examParentIds, String registerNumber) {
        //查询学生班级ID
        long classId = scoreMapper.findClassIdByRegisterNumber(registerNumber);
        //根据classId查询届次-班级类型
        Map map0 = scoreMapper.findThetimeAndclassTypeIdByClassId(classId);
        int classTypeId = (Integer) map0.get("classTypeId");

        Map map2 = new HashMap();
        map2.put("examParentIds",examParentIds);
        map2.put("classTypeId",classTypeId);
        map2.put("registerNumber",registerNumber);

        //查询学生历次考试总分及排名
        List<ExamAvgTotalScore> examStudentTotalScoreYearRanks = scoreMapper.findAllExamStudentYearRankChart(map2);


        Map map = new HashMap();
        map.put("yesrRank",examStudentTotalScoreYearRanks);





        return map;
    }



    /**
     * =====================================================导出==================================================================
     */
    //成绩单导出
    @Override
    public ApiResult exportExamReport(HttpSession session, HttpServletResponse response, Pager pager) throws Exception {

        Integer newId = (Integer) pager.getParameters().get("newExamParentId");
        Integer oldId = (Integer) pager.getParameters().get("oldExamParentId");
        if (newId == oldId){
            ApiResult apiResult = new ApiResult();
            apiResult.setCode(ApiCode.FAIL);
            apiResult.setMsg("请选择不同的对比考试导出");
            return apiResult;
        }

        pager.setPageSize(10000);
        pager.setCurrentPage(1);


        Long adminId = (Long)session.getAttribute("adminId");
        Long roleId = (Long)session.getAttribute("roleId");

        //查询本次考试数据
        Map map1 = new LinkedHashMap();
        map1.put("examParentId",pager.getParameters().get("newExamParentId"));
        map1.put("periodId",pager.getParameters().get("periodId"));
        map1.put("thetime",pager.getParameters().get("thetime"));
        map1.put("classTypeId",pager.getParameters().get("classTypeId"));
        map1.put("classId",pager.getParameters().get("classId"));
        map1.put("subjectId",pager.getParameters().get("subjectId"));
        map1.put("gradeType",1);  //查询总分档次用

        //查询本次考试总分 年级-班级排名
        pager.getParameters().put("examParentId",pager.getParameters().get("newExamParentId"));
        //TODO:本次考试学生总分排名信息
        List<List<?>> lists1 = scoreMapper.findExamClassTotalScore(pager);

        //TODO:本次考试第一页学生学籍号
        List<String> registerNumbers = new ArrayList<>();

        for (ExamClassSubjectScore examClassSubjectScore : (List<ExamClassSubjectScore>)lists1.get(0)){
            registerNumbers.add(examClassSubjectScore.getRegisterNumber());
        }

        System.out.println(registerNumbers);
        //查询学生此次考试的各科成绩-年级-班级 排名
        pager.getParameters().put("registerNumbers",registerNumbers);
        //TODO:本次考试第一页的学生的单科成绩及排名
        List<List<?>> lists3 = scoreMapper.findExamClassSubjectScore(pager);

        //查询对比考试总分 年级-班级排名
        pager.getParameters().put("examParentId",pager.getParameters().get("oldExamParentId"));
        //TODO:对比考试(此次考试第一页的)学生的总分成绩及排名
        List<List<?>> lists2 = scoreMapper.findExamClassTotalScore(pager);

        //查询对比考试各科成绩
        //TODO:对比考试(此次考试第一页的)学生的单科成绩排名
        List<List<?>> lists4 = scoreMapper.findExamClassSubjectScore(pager);




        //结果集处理
        List<Map> list = (List<Map>) lists1.get(1);

        Long total = (Long) list.get(0).get("total");
        //总条数
        pager.setRecordTotal(total.intValue());

        //TODO:本次考试学生总分信息
        List<ExamClassSubjectScore> examClassSubjectScores1 = (List<ExamClassSubjectScore>)lists1.get(0);
        //TODO:对比考试学生总分信息
        List<ExamClassSubjectScore> examClassSubjectScores2 = (List<ExamClassSubjectScore>)lists2.get(0);

        //TODO:处理总分名次
        for(ExamClassSubjectScore examClassSubjectScore1 : examClassSubjectScores1){
            examClassSubjectScore1.setSubjectName("总分");
            for (ExamClassSubjectScore examClassSubjectScore2 : examClassSubjectScores2){


                //学生匹配
                if(examClassSubjectScore1.getOsaasId() == examClassSubjectScore2.getOsaasId()){
                    //年级排名进步名次
                    examClassSubjectScore1.setYearRankRaise(examClassSubjectScore2.getYearRank()-examClassSubjectScore1.getYearRank());
                    //班级排名进步名次
                    examClassSubjectScore1.setClassRankRaise(examClassSubjectScore2.getClassRank()-examClassSubjectScore1.getClassRank());

                }

            }

        }
        //TODO:查询总分档次
        List<ExamThetimeGrade> examThetimeGrades = scoreMapper.findExamThetimeSubjectGrade(map1);

        //TODO:处理总分档次
        for (ExamClassSubjectScore examClassSubjectScore : examClassSubjectScores1){

            for (ExamThetimeGrade examThetimeGrade : examThetimeGrades){
                //如果考试、学段、届次 匹配
                if(
                        examClassSubjectScore.getExamParentId()==examThetimeGrade.getExamParentId()
                                &&
                                examClassSubjectScore.getPeriodId()==examThetimeGrade.getPeriodId()
                                &&
                                examClassSubjectScore.getThetime().equals(examThetimeGrade.getThetime())


                        ){
                    //设置此学生的年级 分数档次和名次档次
                    //获取此学生总分数
                    Double score = examClassSubjectScore.getTotalScore().doubleValue();
                    //获取此学生年级排名
                    int yearRank = examClassSubjectScore.getYearRank();
                    if(examThetimeGrade.getGGradeType().equals("1")){  //处理分数档次
                        //不含头，处理完成剩余的就是满分
                        if(score< examThetimeGrade.getNumMax() && score >= examThetimeGrade.getNumMin()){
                            examClassSubjectScore.setScoreGrade(examThetimeGrade.getGradeName());
                        }
                    }
                    if(examThetimeGrade.getGGradeType().equals("2")){  //处理名次档次
                        //含头含尾
                        if(yearRank >= examThetimeGrade.getNumMax() && yearRank <= examThetimeGrade.getNumMin()){
                            examClassSubjectScore.setRankGrade(examThetimeGrade.getGradeName());
                        }
                    }

                }

            }
        }

        //添加学生单科成绩
//        for (ExamClassSubjectScore examClassSubjectScore : examClassSubjectScores1){
//            List<ExamSubjectScore> examSubjectScoreList = new ArrayList<>();
//            for (ExamSubjectScore examSubjectScore : examSubjectScores){
//                if(examClassSubjectScore.getRegisterNumber().equals(examSubjectScore.getRegisterNumber())){  //学籍号匹配
//                    examSubjectScoreList.add(examSubjectScore);
//                }
//
//            }
//            examClassSubjectScore.setExamSubjectScores(examSubjectScoreList);
//        }

        //TODO:本次考试第一页学生单科成绩
        List<ExamClassSubjectScore> examClassSubjectScores3 = (List<ExamClassSubjectScore>)lists3.get(0);
        //TODO:对比考试第一页学生单科成绩
        List<ExamClassSubjectScore> examClassSubjectScores4 = (List<ExamClassSubjectScore>)lists4.get(0);
        //TODO:处理单科名次
        for (ExamClassSubjectScore examClassSubjectScore3 : examClassSubjectScores3){
            for (ExamClassSubjectScore examClassSubjectScore4 :examClassSubjectScores4){
                //课程匹配
                if(examClassSubjectScore3.getSubjectId()==examClassSubjectScore4.getSubjectId()){
                    //学生匹配
                    if(examClassSubjectScore3.getOsaasId()==examClassSubjectScore4.getOsaasId()){
                        //年级进步名次
                        examClassSubjectScore3.setYearRankRaise(examClassSubjectScore4.getYearRank()-examClassSubjectScore3.getYearRank());
                        //班级进步名次
                        examClassSubjectScore3.setClassRankRaise(examClassSubjectScore4.getClassRank()-examClassSubjectScore3.getClassRank());
                    }

                }
            }

        }

        //TODO:查询单科档次
        map1.put("gradeType",3);
        List<ExamThetimeGrade> examThetimeGradesSubject = scoreMapper.findExamThetimeSubjectGrade(map1);

        //TODO:处理单科档次
        for (ExamClassSubjectScore examClassSubjectScore3 : examClassSubjectScores3){
            for (ExamThetimeGrade examThetimeGrade : examThetimeGradesSubject){
                //如果考试、学段、届次、班级、课程匹配
                if(
                        examClassSubjectScore3.getExamParentId()==examThetimeGrade.getExamParentId()
                                &&
                                examClassSubjectScore3.getPeriodId()==examThetimeGrade.getPeriodId()
                                &&
                                examClassSubjectScore3.getThetime().equals(examThetimeGrade.getThetime())
                                &&
                                examClassSubjectScore3.getClassId()==examThetimeGrade.getClassId()
                                &&
                                examClassSubjectScore3.getSubjectId()==examThetimeGrade.getSubjectId()

                        ){
                    //设置此学生的年级 分数档次和名次档次
                    //获取此学生此次考试分数
                    Double score = examClassSubjectScore3.getScore().doubleValue();
                    //获取此学生此次考试年级排名
                    int yearRank = examClassSubjectScore3.getYearRank();
                    if(examThetimeGrade.getGGradeType().equals("1")){  //处理分数档次
                        //不含头，处理完成剩余的就是满分
                        if(score< examThetimeGrade.getNumMax() && score >= examThetimeGrade.getNumMin()){
                            examClassSubjectScore3.setScoreGrade(examThetimeGrade.getGradeName());
                        }
                    }
                    if(examThetimeGrade.getGGradeType().equals("2")){  //处理名次档次
                        //含头含尾
                        if(yearRank >= examThetimeGrade.getNumMax() && yearRank <= examThetimeGrade.getNumMin()){
                            examClassSubjectScore3.setRankGrade(examThetimeGrade.getGradeName());
                        }
                    }

                }

            }
        }


        //TODO:将单科成绩名次-档次等添加到总分
        for (ExamClassSubjectScore examClassSubjectScore5 : examClassSubjectScores1){

            List<ExamClassSubjectScore>  examClassSubjectScoreList = new ArrayList<>();

            for (ExamClassSubjectScore examClassSubjectScore6 : examClassSubjectScores3){
                //学生匹配
                if(examClassSubjectScore5.getOsaasId()==examClassSubjectScore6.getOsaasId()){
                    //添加
                    examClassSubjectScoreList.add(examClassSubjectScore6);
                }
            }

            examClassSubjectScore5.setExamClassSubjectScores(examClassSubjectScoreList);
        }

        System.out.println(examClassSubjectScores1);



        //TODO:数据
        List<List<String>> listRow = new ArrayList<>();
        List<String> row = new ArrayList<>();
        int rowSize = 0;

        //表头 第二行数据
        Set<String> headSet = new LinkedHashSet<>();
        for (ExamClassSubjectScore examClassSubjectScore0 : examClassSubjectScores1){
            headSet.add(examClassSubjectScore0.getSubjectName());
            row = new ArrayList<>();
            row.add(examClassSubjectScore0.getFullName());  //姓名
            row.add(examClassSubjectScore0.getRegisterNumber());  //学籍号

            row.add(examClassSubjectScore0.getTotalScore()+""); //总分分数
            row.add(examClassSubjectScore0.getYearRank()+"");    //总分年级排名
            row.add(examClassSubjectScore0.getYearRankRaise()+""); //总分年级进步
            row.add(examClassSubjectScore0.getClassRank()+"");    //总分班级排名
            row.add(examClassSubjectScore0.getClassRankRaise()+""); //总分班级进步
            row.add(examClassSubjectScore0.getScoreGrade()); //总分分数档次

            for (ExamClassSubjectScore examClassSubjectScore : examClassSubjectScore0.getExamClassSubjectScores()){
                headSet.add(examClassSubjectScore.getSubjectName());
                row.add(examClassSubjectScore.getScore()+""); //单科分数
                row.add(examClassSubjectScore.getYearRank()+"");  //单科年级排名
                row.add(examClassSubjectScore.getYearRankRaise()+""); //单科年级进步
                row.add(examClassSubjectScore.getClassRank()+"");  //单科班级排名
                row.add(examClassSubjectScore.getClassRankRaise()+""); //单科班级进步
                row.add(examClassSubjectScore.getScoreGrade());  //单科分数档次

            }

            listRow.add(row);

            if(row.size()>rowSize){
                rowSize = row.size();
            }


        }

        //表头第一行
        //查询两次考试名称
        List<Integer> examParentIds = new ArrayList<>();
        examParentIds.add((Integer) pager.getParameters().get("newExamParentId"));
        examParentIds.add((Integer) pager.getParameters().get("oldExamParentId"));
        Map map = new HashMap();
        map.put("examParentIds",examParentIds);
        List<String> examNames = scoreMapper.findExamNames(map);
        String[] excelHeader0 = { examNames.get(0)+"    "+examNames.get(1)+"    对比成绩单" };
        String[] headnum0 = { "0,0,0,"+ (rowSize-1)};

        //表头第二行
        List<String> head1 = new ArrayList<>();
        for (String s : headSet){
            head1.add(s);
            head1.add("");
            head1.add("");
            head1.add("");
            head1.add("");
            head1.add("");
        }
        List<String> excle1 = new ArrayList<>();
        excle1.add("姓名");
        excle1.add("学籍号");
        List<String> num1 = new ArrayList<>();
        num1.add("1,2,0,0");
        num1.add("1,2,1,1");
        for (int i = 0; i <head1.size() ; i++) {
            excle1.add(head1.get(i));
            if(head1.get(i).length()>0){
                num1.add("1,1,"+(2+i)+","+(2+i+5));
            }else {
                num1.add("1,1,"+(2+i)+","+(2+i));
            }
        }

        String[] excelHeader1 = excle1.toArray(new String[0]);
        String[] headnum1 = num1.toArray(new String[0]);

        //表头第三行
        List<String> excle2 = new ArrayList<>();
        excle2.add("");
        excle2.add("");
        List<String> num2 = new ArrayList<>();
        num2.add("2,2,0,0");
        num2.add("2,2,1,1");


        for (int i = 0; i <head1.size() ; i++) {
            if(head1.get(i).length()>0){
                excle2.add("分数");
                num2.add("2,2,"+(2+i)+","+(2+i));
                excle2.add("年级排名");
                num2.add("2,2,"+(2+i+1)+","+(2+i+1));
                excle2.add("年级进步");
                num2.add("2,2,"+(2+i+2)+","+(2+i+2));
                excle2.add("班级排名");
                num2.add("2,2,"+(2+i+3)+","+(2+i+3));
                excle2.add("班级进步");
                num2.add("2,2,"+(2+i+4)+","+(2+i+4));
                excle2.add("档次");
                num2.add("2,2,"+(2+i+5)+","+(2+i+5));
            }

        }

        String[] excelHeader2 = excle2.toArray(new String[0]);
        String[] headnum2 = num2.toArray(new String[0]);

        // 声明一个工作簿
        HSSFWorkbook wb = new HSSFWorkbook();
//        // 生成一个表格
//        XSSFSheet sheet = wb.createSheet("TAQIDataReport");

        // 生成一种样式
        HSSFCellStyle style = wb.createCellStyle();
        // 设置样式
        // 设置单元格背景色，设置单元格背景色以下两句必须同时设置
//        style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index); // 设置填充色
//        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 设置填充样式
        // 设置单元格上、下、左、右的边框线
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);

        // 水平居中
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //垂直居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        // 生成一种字体
        HSSFFont font = wb.createFont();
        // 设置字体
        font.setFontName("微软雅黑");
        // 设置字体大小
        font.setFontHeightInPoints((short) 12);
        // 字体加粗
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);

        // 指定当单元格内容显示不下时自动换行
//        style.setWrapText(true);




        // 生成并设置另一个样式
        HSSFCellStyle style2 = wb.createCellStyle();
        // 设置单元格上、下、左、右的边框线
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 水平居中
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //垂直居中
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        // 生成一种字体
        HSSFFont font2 = wb.createFont();
        // 设置字体
        font2.setFontName("宋体");
        // 设置字体大小
        font2.setFontHeightInPoints((short) 12);

        // 把字体应用到当前的样式
        style2.setFont(font2);

        // 指定当单元格内容显示不下时自动换行
//        style2.setWrapText(true);


        List<Map<String,Object>> headers = new ArrayList<>();

        Map<String,Object> header0 = new HashMap<>();
        header0.put("excelHeader",excelHeader0);
        header0.put("headnum",headnum0);
        header0.put("style",style);

        Map<String,Object> header1 = new HashMap<>();
        header1.put("excelHeader",excelHeader1);
        header1.put("headnum",headnum1);
        header1.put("style",style);

        Map<String,Object> header2 = new HashMap<>();
        header2.put("excelHeader",excelHeader2);
        header2.put("headnum",headnum2);
        header2.put("style",style);

//        Map<String,Object> header3 = new HashMap<>();
//        header3.put("excelHeader",excelHeader3);
//        header3.put("headnum",headnum3);


        headers.add(header0);
        headers.add(header1);
        headers.add(header2);

        String fileName = "成绩单";


        exportExcelUtil.exportExcel2(wb,0,"成绩单",style2,headers,listRow,response,fileName);

        ApiResult apiResult = new ApiResult();
        apiResult.setData(ApiCode.export_success);
        apiResult.setData(ApiCode.export_success_MSG);
        return apiResult;
    }

    //关注学生导出
    @Override
    public void exportFollowStudents(HttpSession session, HttpServletResponse response,
                                     long newExamParentId, long oldExamParentId, long classId, int followRank) throws Exception {
        Map map = new HashMap();
        map.put("examParentId",newExamParentId);
        map.put("classId",classId);
        //此次考试班级排名
        List<ClassTotalScoreRank> newClassTotalScoreRanks = scoreMapper.findClassTotalScoreRank(map);
        //班级前N名
        List<ClassTotalScoreRank> front = new ArrayList<>();
        //班级后N名
        List<ClassTotalScoreRank> back = new ArrayList<>();
        if(followRank <= newClassTotalScoreRanks.size()){
            for (int i = 0; i < followRank; i++) {
                front.add(newClassTotalScoreRanks.get(i));

            }

            for (int i = newClassTotalScoreRanks.size()-1; i > newClassTotalScoreRanks.size()-1-followRank; i--) {
                back.add(newClassTotalScoreRanks.get(i));
            }


        }



        //对比考试信息
        map.put("examParentId",oldExamParentId);
        List<ClassTotalScoreRank> oldClassTotalScoreRanks = scoreMapper.findClassTotalScoreRank(map);
        //TODO:处理两次考试名次变化
        for (ClassTotalScoreRank classTotalScoreRank1 : newClassTotalScoreRanks){
            for (ClassTotalScoreRank classTotalScoreRank2 : oldClassTotalScoreRanks){
                if(classTotalScoreRank1.getRegisterNumber().equals(classTotalScoreRank2.getRegisterNumber())){
                    classTotalScoreRank1.setClassRankRaise(classTotalScoreRank2.getClassRank()-classTotalScoreRank1.getClassRank());
                }

            }
        }

        //排序
        Collections.sort(newClassTotalScoreRanks, new Comparator<ClassTotalScoreRank>() {
            @Override
            public int compare(ClassTotalScoreRank o1, ClassTotalScoreRank o2) {
                int i = o1.getClassRankRaise()-o2.getClassRankRaise();
                if(i == 0){
                    return o1.getTotalScore().compareTo(o2.getTotalScore());
                }
                return i;
            }
        });
        //进步最大的N名
        List<ClassTotalScoreRank> goForward = new ArrayList<>();
        //进步最大的N名
        List<ClassTotalScoreRank> backOff = new ArrayList<>();

        if(followRank <= newClassTotalScoreRanks.size()){
            for (int i = newClassTotalScoreRanks.size()-1; i > newClassTotalScoreRanks.size()-1-followRank; i--) {
                goForward.add(newClassTotalScoreRanks.get(i));
            }

            for (int i = 0; i < followRank; i++) {
                backOff.add(newClassTotalScoreRanks.get(i));
            }


        }
        //封装
        Map resultMap = new LinkedHashMap();
        resultMap.put("front",front);
        resultMap.put("back",back);
        resultMap.put("goForward",goForward);
        resultMap.put("backOff",backOff);

        //TODO:处理数据
        List<List<String>> listRow = new ArrayList<>();
        List<String> row = new ArrayList<>();
        int rowSize = 0;

        for (int i = 0; i <followRank ; i++) {
            row = new ArrayList<>();
            ClassTotalScoreRank front0 = front.get(i);
            ClassTotalScoreRank back0 = back.get(i);
            ClassTotalScoreRank goForward0 = goForward.get(i);
            ClassTotalScoreRank backOff0 = backOff.get(i);

            row.add(front0.getFullName());
            row.add(front0.getRegisterNumber());
            row.add(front0.getClassRank()+"");
            row.add(back0.getFullName());
            row.add(back0.getRegisterNumber());
            row.add(back0.getClassRank()+"");
            row.add(goForward0.getFullName());
            row.add(goForward0.getRegisterNumber());
            row.add(goForward0.getClassRankRaise()+"");
            row.add(backOff0.getFullName());
            row.add(backOff0.getRegisterNumber());
            row.add(backOff0.getClassRankRaise()+"");

            listRow.add(row);

            if(row.size()>rowSize){
                rowSize = row.size();
            }

        }

        //表头第一行

        String[] excelHeader0 = { "关注学生"+followRank+"名统计" };
        String[] headnum0 = { "0,0,0,"+ (rowSize-1)};

        //表头第二行

        String[] excelHeader1 = {
                "班级（前"+followRank+"名）","","",
                "班级（后"+followRank+"名）","","",
                "班级进步最大（"+followRank+"名）","","",
                "班级退步最大（"+followRank+"名）","","",
        };
        String[] headnum1 = {
                "1,1,0,2",
                "1,1,3,5",
                "1,1,6,8",
                "1,1,9,11"
        };

        //表头第三行


        String[] excelHeader2 = {
                "姓名","学籍号","名次",
                "姓名","学籍号","名次",
                "姓名","学籍号","进步名次",
                "姓名","学籍号","退步名次",
        };
        String[] headnum2 = {
                "2,2,0,0","2,2,1,1","2,2,2,2",
                "2,2,3,3","2,2,4,4","2,2,5,5",
                "2,2,6,6","2,2,7,7","2,2,8,8",
                "2,2,9,9","2,2,10,10","2,2,11,11",
        };

        // 声明一个工作簿
        HSSFWorkbook wb = new HSSFWorkbook();
//        // 生成一个表格
//        XSSFSheet sheet = wb.createSheet("TAQIDataReport");

        // 生成一种样式
        HSSFCellStyle style = wb.createCellStyle();
        // 设置样式
        // 设置单元格背景色，设置单元格背景色以下两句必须同时设置
//        style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index); // 设置填充色
//        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 设置填充样式
        // 设置单元格上、下、左、右的边框线
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);

        // 水平居中
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //垂直居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        // 生成一种字体
        HSSFFont font = wb.createFont();
        // 设置字体
        font.setFontName("微软雅黑");
        // 设置字体大小
        font.setFontHeightInPoints((short) 12);
        // 字体加粗
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);

        // 指定当单元格内容显示不下时自动换行
//        style.setWrapText(true);




        // 生成并设置另一个样式
        HSSFCellStyle style2 = wb.createCellStyle();
        // 设置单元格上、下、左、右的边框线
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        // 水平居中
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //垂直居中
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        // 生成一种字体
        HSSFFont font2 = wb.createFont();
        // 设置字体
        font2.setFontName("宋体");
        // 设置字体大小
        font2.setFontHeightInPoints((short) 12);

        // 把字体应用到当前的样式
        style2.setFont(font2);

        // 指定当单元格内容显示不下时自动换行
//        style2.setWrapText(true);


        List<Map<String,Object>> headers = new ArrayList<>();

        Map<String,Object> header0 = new HashMap<>();
        header0.put("excelHeader",excelHeader0);
        header0.put("headnum",headnum0);
        header0.put("style",style);

        Map<String,Object> header1 = new HashMap<>();
        header1.put("excelHeader",excelHeader1);
        header1.put("headnum",headnum1);
        header1.put("style",style);

        Map<String,Object> header2 = new HashMap<>();
        header2.put("excelHeader",excelHeader2);
        header2.put("headnum",headnum2);
        header2.put("style",style);

//        Map<String,Object> header3 = new HashMap<>();
//        header3.put("excelHeader",excelHeader3);
//        header3.put("headnum",headnum3);


        headers.add(header0);
        headers.add(header1);
        headers.add(header2);

        String fileName = "关注学生统计";

        exportExcelUtil.exportExcel2(wb,0,"关注学生统计",style2,headers,listRow,response,fileName);
    }


//    /**
//     * ===================================================================================================================
//     * 选择导出
//     * @param classIdList
//     * @param response
//     */
//    @Override
//    public void exportScoreInfo(List<Long> classIdList, HttpServletResponse response) {
//        OutputStream out = null;
//        try {
//            //获取流
//            out = response.getOutputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //设置标题
//        String[] titles = {"班级", "实考人数", "缺考人数", "平均分", "最低分", "最高分", "排名"};
//        //Excel结果集
//        List<List<String>> dataHandler = new ArrayList<List<String>>();
//        List<ClassGradeInfo> data = this.studentScoreMapper.selectBathInfo(classIdList);
//        //测试
//        for(Object s : data){
//            System.out.println(s.toString());
//        }
//        //结果集
//        List<List<String>> sumData = new ArrayList<List<String>>();
//        for(ClassGradeInfo info : data){
//            List<String> rowData  = new ArrayList<String>();
//            rowData.add(info.getClassName());
//            rowData.add(String.valueOf(info.getActuallyCome()));
//            rowData.add(String.valueOf(info.getNotCome()));
//            rowData.add(String.valueOf(info.getAvgScore()));
//            rowData.add(String.valueOf(info.getMinScore()));
//            rowData.add(String.valueOf(info.getMaxScore()));
//            rowData.add(String.valueOf(info.getRank()));
//            dataHandler.add(rowData);
//        }
//        //创建工作薄
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        //导出信息
//        try {
//            exportExcelUtil.exportExcel(workbook, 0, "成绩导出信息", titles, dataHandler, out);
//            workbook.write(out);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * 不同的角色,查询不同的成绩
//     * @return
//     */
//    @Override
//    public ApiResult findScoreForDifferentRole(HttpServletRequest request) {
//
//        System.out.println("进入逻辑！");
//        ApiResult result = new ApiResult();
//        //获取管理员ID
//        Long adminId = (Long) request.getSession().getAttribute("adminId");
//        //根据管理员角色ID,查询管理员表
//        //TODO:GG
//        Admin admin = this.studentScoreMapper.selectAdminInfoById(adminId);
//        //TODO:gg
//        //根据角色ID,查询角色
//        AdminRole role = this.studentScoreMapper.selectAdminRoleByRoleId(admin.getRoleId());
//        switch (role.getType()){
//            //待定
//            case "0" :
//            case "1" :
//            case "2" :
//                System.out.println("各种管理员");
////                result = allAdmin();
//                break;
//
//            case "3" :
//                System.out.println("班主任/科目教师/年级主任逻辑");
//                result = caseThree(result, role, admin);
//                break;
//            default : break;
//        }
//        return result;
//    }
//
//
//    /**
//     * 添加成绩档次
//     * @param info
//     * @return
//     */
//    @Override
//    public ApiResult addGradeLevelInfo(Grade info) {
//        ApiResult result = new ApiResult();
//        //  1--为分数档次   2--为名次档次
//        if("1".equals(info.getGradeType())){
//            //查重,根据exam_grade_id,考试ID
//            int isCopy = 0;
//            if(isCopy > 0){
//                //数据存在
//                result.setCode(ApiCode.error_duplicated_data);
//                result.setMsg(ApiCode.error_duplicated_data_MSG);
//                return result;
//            }
//            //save保存
//            ApiResultUtil.fastResultHandler(result, true, null, null, null);
//        }else if("2".equals(info.getGradeType())){
//            //查重,根据exam_grade_id,考试ID
//            int isCopy = 0;
//            if(isCopy > 0){
//                //数据存在
//                result.setCode(ApiCode.error_duplicated_data);
//                result.setMsg(ApiCode.error_duplicated_data_MSG);
//                return result;
//            }
//            //save保存
//            ApiResultUtil.fastResultHandler(result, true, null, null, null);
//        }else{
//            ApiResultUtil.fastResultHandler(result, false, ApiCode.error_create_failed, ApiCode.FAIL_MSG, null);
//        }
//        return result;
//    }
//
//
//    /**********************************************************************************************/
//    /*******************************程序私有方法****************************************************/
//    /**********************************************************************************************/
//    //角色类型3 -- 包含(班主任,科目教师,年级主任)
//    private ApiResult caseThree(ApiResult result, AdminRole role, Admin admin){
//        //角色类型,获取角色ID
//        int id = role.getId();
//        System.out.println("角色ID: " + id);
//
//
//
//        if(id == 4){        //班主任
//            //根据班主任ID,查询班级主任管理的班级信息
//            SchoolPeriodClassThetime scTime = this.classThetimeMapper.selectByAdminId(admin.getId());
//            //根据班主任ID,查找班级
//            List info = this.studentScoreMapper.selectPowerByHeadmaster(scTime.getClassId());
//            for(Object a : info){
//                System.out.println(a.toString());
//            }
//            //数据的封装
//            ApiResultUtil.fastResultHandler(result, true, null, null, info);
//        }else if(id == 5){  //科目教师
//            //根据ID,查询对应科目
//            Long adminId = admin.getId();
//            AdminInfo adminInfo = this.adminInfoMapper.selectByadminId(adminId);
//            //根据科目教师查询对应科目和班级
//            List<ClassSubjectInfo> subClass = this.clsInfo.selectAdminId(adminInfo.getAdminInfoId());
//            //根据id,查询
//            List info = this.studentScoreMapper.selectPowerBySubjectTeacher(subClass);
//            ApiResultUtil.fastResultHandler(result, true, null, null, info);
//        }else if(id == 21){ //年级主任
//            //获取ID
//            Long adminId = admin.getId();
//            PeriodDirectorThetime tempInfo = this.directorTimeMapper.selectByDirectorId(adminId);
//            //根据学段ID,毕业届次查询
//            List info = this.studentScoreMapper.selectPowerByPeriodIdAndThetime(tempInfo);
//            ApiResultUtil.fastResultHandler(result, true, null, null, info);
//        }else{
//            result.setCode(ApiCode.error_search_failed);
//            result.setMsg(ApiCode.error_search_failed_MSG);
//        }
//        return result;
//    }
//    //角色类型 -- 包含(各种系统管理员)
//    private ApiResult allAdmin(ApiResult result, AdminRole role, Admin admin){
//
//        return null;
//    }
//
//
//
//
//
//
//    /**
//     * 查看分数
//     * @param classId
//     * @param subjectId
//     * @return
//     */
//    @Override
//    public List<Map> findScoreDetils(Long classId, Long subjectId) {
//        List<Map> result = this.studentScoreMapper.selectByClassIdAndSubjectId(classId, subjectId);
//        return result;
//    }

}
