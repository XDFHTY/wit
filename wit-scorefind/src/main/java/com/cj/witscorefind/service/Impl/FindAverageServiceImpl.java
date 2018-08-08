package com.cj.witscorefind.service.Impl;

import com.cj.witbasics.entity.AdminInfoExport;
import com.cj.witbasics.entity.PeriodDirectorThetime;
import com.cj.witbasics.entity.SchoolClass;
import com.cj.witbasics.entity.SchoolClassLevel;
import com.cj.witbasics.mapper.PeriodDirectorThetimeMapper;
import com.cj.witbasics.service.Impl.SchoolSubjectServiceImpl;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witcommon.utils.excle.exportExcelUtil;
import com.cj.witscorefind.entity.*;
import com.cj.witscorefind.mapper.AverageMapper;
import com.cj.witscorefind.service.FindAverageService;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * 查询平均分
 * Created by XD on 2018/5/23.
 */
@Service
public class FindAverageServiceImpl implements FindAverageService {

    private static final Logger log = LoggerFactory.getLogger(FindAverageServiceImpl.class);

    @Value("${school_id}")
    private String schoolId;


    /**
     * 转为Long
     * @return
     */
    private Long toLong(){
        System.out.println(this.schoolId);
        return Long.valueOf(this.schoolId);
    }

    @Autowired
    private AverageMapper averageMapper;

    @Autowired
    private PeriodDirectorThetimeMapper thetimeMapper;

    /**
     * 根据条件查询课程平均分
     *
     * @param averageReq
     * @return
     */
   /* @Override
    public List<AverageRsp> findAverageInfo(AverageReq averageReq) {
        //查询平均分信息
        List<AverageRsp> list = this.averageMapper.findAverageInfo(averageReq);
        System.out.println(list.size());
        return list;
    }*/


    /**
     * 查询年级平均分
     * @param pager
     * @param request
     * @return
     */
    @Override
    public ApiResult findAverageInfo(Pager pager, HttpServletRequest request) {

        //返回结果
        ApiResult apiResult = new ApiResult();
            //设置学校id
        Map<String, Object> map = pager.getParameters();
        map.put("schoolId", toLong());
        pager.setParameters(map);




        //查询 平均分 和 总数
        List<List<?>> lists1 = this.averageMapper.findAverageInfo(pager);
        //查询年级的课程平均分情况
        List<AverageRsp> averageGrade = this.averageMapper.findAverageByGrade(pager);
        //查询总分情况  传了几个课程id 就算几个课程的总分
        List<AverageRsp> averageTotal = this.averageMapper.averageTotal(pager);

        if (lists1 != null && lists1.size() > 1 && lists1.get(1) != null){  //两个结果集
            //设置总数
            List<?> list2 = lists1.get(1);
            Map<String, Long> map1 = (Map<String, Long>) list2.get(0);
            Long total = map1.get("total");
            pager.setRecordTotal(total.intValue());

            //设置结果集
            List<?> list = lists1.get(0);
            List<List<?>> lists = new ArrayList<>();
            //各班情况
            lists.add(list);
            //年级情况
            lists.add(averageGrade);
            //总分情况
            lists.add(averageTotal);

            pager.setContent(lists);

        }
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(pager);
        return apiResult;
    }

    /**
     * 根据届次id 学段 层次id 返回班级列表
     *
     * @param map
     * @return
     */
    @Override
    public ApiResult findClassByLevel(Map<String, Object> map) {
        ApiResult apiResult = new ApiResult();
        //设置学校id
        map.put("schoolId",toLong());
        //查询
        try {
            List<SchoolClass> list = this.averageMapper.findClassByLevel(map);
            apiResult.setCode(ApiCode.SUCCESS);
            apiResult.setMsg(ApiCode.SUCCESS_MSG);
            apiResult.setData(list);
        }catch (Exception e){
            //失败
            e.printStackTrace();
            apiResult.setCode(ApiCode.FAIL);
            apiResult.setMsg(ApiCode.FAIL_MSG);
        }
        return apiResult;
    }

    /**
     * 查询同层次各个班级总分平均分
     *
     * @param p
     * @param request
     * @return
     */
    @Override
    public ApiResult findTotalAvg(Pager pager, HttpServletRequest request) {
        //返回结果
        ApiResult apiResult = new ApiResult();
        //设置学校id
        Map<String, Object> map = pager.getParameters();
        map.put("schoolId", toLong());
        pager.setParameters(map);

        //查询总分平均分和计数
        List<List<?>> lists = this.averageMapper.findTotalAvg(pager);
        //查询年级总分平均分情况
        List<AverageRsp> averageRsps = this.averageMapper.findTotalAvgByGrade(pager);

        if (lists != null && lists.size() > 1 && lists.get(1) != null) {  //两个结果集
            //设置总数
            List<?> list = lists.get(1);
            Map<String, Long> map1 = (Map<String, Long>) list.get(0);
            Long total = map1.get("total");
            pager.setRecordTotal(total.intValue());

            //设置结果集
            List<?> list1 = lists.get(0);
            List<List<?>> lists2 = new ArrayList<>();
            //各班总分信息
            lists2.add(list1);
            //年级总分信息
            lists2.add(averageRsps);

            pager.setContent(lists2);
        }
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(pager);
        return apiResult;
    }

    /**
     * 查询年级班级平均分，按班级封装
     *
     * @param p
     * @param request
     * @return
     */
    @Override
    public ApiResult findAverageInfos(Pager pager, HttpServletRequest request) {
        //返回结果
        ApiResult apiResult = new ApiResult();
        //设置学校id
        Map<String, Object> map = pager.getParameters();
        map.put("schoolId", toLong());
        pager.setParameters(map);


        //计算当前页的班级id集合
        pager = this.classIdPager(pager);

        //判断是否需要查询总分信息
        boolean isTotal = false;
        List<Integer> curriculumIdList = (List<Integer>) pager.getParameters().get("curriculumIdList");
        for (Integer s:curriculumIdList){
            if (s.equals(0)){
                isTotal = true;
                break;
            }
        }

        List<ExamCome> comes = null;
        List<ExamCome> examTotal = null;
        List<String> comeList = null;
        Integer gradeTotal = null;
        List<String> gradeExam = null;
        if (isTotal){
            //查询这次考试  各班总分的缺考学生的id集合
            comes = this.averageMapper.findExamComeByTotal(pager);
            //查询这次考试  各班总分的考试人数
            examTotal = this.averageMapper.findExamTotal(pager);
            //创建List 装所有缺考学生的id
            comeList = new ArrayList<>();
            //遍历班级
            if (comes != null && comes.size()!=0 && comes.get(0)!=null){
                for (ExamCome e : comes) {
                    //遍历缺考学生id
                    if (e.getRegisterNumbers() != null && e.getRegisterNumbers().size() != 0 && e.getRegisterNumbers().get(0) != null) {
                        for (String s : e.getRegisterNumbers()) {
                            //添加到集合
                            comeList.add(s);
                        }
                    }
                    //计算实考 缺考人数
                    if(examTotal != null && examTotal.size() != 0 && examTotal.get(0) != null){
                        for (ExamCome e1:examTotal){
                            if (e.getClassId().equals(e1.getClassId())){
                                e.setActuallyCome(e1.getTotal()-e.getNotCome());
                            }
                        }
                    }
                }
            }
            //添加至pager
            pager.getParameters().put("notComes",comeList);


            //查询这次考试  年级总分的考试总人数
            gradeTotal = this.averageMapper.findGradeTotal(pager);
            //查询这次考试  年级总分的缺考人数 和 学生id集合
            gradeExam = this.averageMapper.findGradeExamNotCome(pager);
            if (gradeExam != null && gradeExam.size()!=0 && gradeExam.get(0)!=null){
                //添加至 查询条件
                pager.getParameters().put("gradNotComes",gradeExam);
            }
        }


        //查询这次考试  各班各科的缺考学生总数
        List<ExamCome> subNotComes = this.averageMapper.findSubNotComes(pager);
        //查询这次考试  各班各科的考试总人数
        List<ExamCome> subActuallyComes = this.averageMapper.findSubActuallyComes(pager);


        //查询 课程平均分
        List<AverageInfos> averageInfos = this.averageMapper.findAverageInfos(pager);
        //设置实考人数为 总人数   缺考人数为0
        if (averageInfos != null && averageInfos.size() != 0 && averageInfos.get(0) != null) {
            //遍历班级
            for (AverageInfos averageInfo : averageInfos) {
                //遍历课程
                for (AverageRsp a : averageInfo.getAverageRspList()) {
                    //遍历 考试总人数集合
                    for (ExamCome e:subActuallyComes) {
                        //判断班级 和 课程
                        if (averageInfo.getClassId().equals(e.getClassId()) && a.getSubjectId().equals(e.getSubjectId())){
                            //设置
                            a.setActuallyCome(e.getTotal());
                            a.setNotCome(0);
                        }
                    }
                    //遍历缺考学生总数
                    if (subNotComes != null && subNotComes.size() != 0 && subNotComes.get(0) != null) {
                        for (ExamCome e2 : subNotComes) {
                            if(averageInfo.getClassId().equals(e2.getClassId()) && a.getSubjectId().equals(e2.getSubjectId())){
                                //设置
                                a.setNotCome(e2.getNotCome());
                                Integer total = a.getActuallyCome();
                                a.setActuallyCome(total-e2.getNotCome());
                            }
                        }
                    }
                }
            }
        }


        //年级平均分情况
        List<AverageInfos> averageGrade = this.averageMapper.findAveragesByGrade(pager);
        //查询年级各科考试总人数
        List<ExamCome> gradeSubComes = this.averageMapper.findAradeSubComes(pager);
        //查询年级各科缺考人数
        List<ExamCome> gradeSubNotComes = this.averageMapper.findAradeSubNotComes(pager);


        if (isTotal){
            //选择总分时 需要查询的信息
            //查询班级总分情况(过滤掉缺考的学生)
            List<AverageRsp> averageTotal = this.averageMapper.averageTotal(pager);

            //年级总分情况(过滤掉缺考的学生)
            List<AverageRsp> averageTotalGrade = this.averageMapper.findAverageTotalGrade(pager);

            //如果未查课程信息  则不会有averageInfos  但是需要总分 就要手动设置
            if (averageInfos != null && averageInfos.size()==0){
                //遍历总分
                if (averageTotal != null && averageTotal.size() != 0 && averageTotal.get(0) != null){
                    for (AverageRsp averageRsp : averageTotal){
                        AverageInfos a = new AverageInfos();
                        a.setClassId(averageRsp.getClassId());
                        a.setClassPeriod(averageRsp.getClassPeriod());
                        a.setClassPeriodId(averageRsp.getClassPeriodId());
                        a.setThetime(averageRsp.getThetime());
                        a.setClassHeadmaster(averageRsp.getClassHeadmaster());
                        a.setClassNumber(averageRsp.getClassNumber());
                        averageInfos.add(a);
                    }
                }
            }

            //遍历总分信息，判断是哪个班级的
            if (averageInfos != null && averageInfos.size() != 0 && averageInfos.get(0) != null
                    && averageTotal != null && averageTotal.size() != 0 && averageTotal.get(0) != null)
                for(AverageInfos infos:averageInfos){
                    //为这个班设置总分信息
                    for (AverageRsp averageRsp : averageTotal){
                        if (averageRsp.getClassId().equals(infos.getClassId())){
                            infos.setAverageTotal(averageRsp);
                        }
                    }
                    //为这个班设置实考人数 为总人数， 缺考为0
                    if(examTotal != null && examTotal.size() != 0 && examTotal.get(0) != null) {
                        for (ExamCome e3 : examTotal) {
                            if (infos.getClassId().equals(e3.getClassId())) {
                                infos.getAverageTotal().setActuallyCome(e3.getTotal());
                                infos.getAverageTotal().setNotCome(0);
                            }
                        }
                    }
                    //设置实际 实考人数 和缺考人数
                    if (comes != null && comes.size()!=0 && comes.get(0)!=null){
                        for (ExamCome e4:comes){
                            if (infos.getClassId().equals(e4.getClassId())){
                                infos.getAverageTotal().setActuallyCome(e4.getActuallyCome());
                                infos.getAverageTotal().setNotCome(e4.getNotCome());
                            }
                        }
                    }
                }
            //设置年级总分情况
            if (averageTotalGrade != null && averageTotalGrade.size()!=0 && averageTotalGrade.get(0)!= null){
                    if (averageGrade!= null && averageGrade.size()==0){
                        AverageInfos a = new AverageInfos();
                        averageGrade.add(a);
                    }
                        averageGrade.get(0).setAverageTotal(averageTotalGrade.get(0));
                        //设置实考人数为总人数，缺考人数为0
                        averageGrade.get(0).getAverageTotal().setActuallyCome(gradeTotal);
                        averageGrade.get(0).getAverageTotal().setNotCome(0);
                        //如果有缺考学生 就设置
                        if (gradeExam != null && gradeExam.size()!=0 && gradeExam.get(0)!=null){
                            averageGrade.get(0).getAverageTotal().setNotCome(gradeExam.size());
                            averageGrade.get(0).getAverageTotal().setActuallyCome(gradeTotal-gradeExam.size());
                        }

            }
        }



        //设置年级平均分情况
        if (averageGrade!=null && averageGrade.size()!=0 && averageGrade.get(0)!=null) {
            List<AverageRsp> list = averageGrade.get(0).getAverageRspList();
            if(list != null && list.size() != 0 && list.get(0)!=null){
                for (AverageRsp a:list){
                    //设置 实考人数为总数  缺考数为0
                    for (ExamCome e:gradeSubComes){
                        if (a.getSubjectId().equals(e.getSubjectId())){
                            a.setActuallyCome(e.getTotal());
                            a.setNotCome(0);
                        }
                    }
                    //设置实考人数 缺考人数
                    for (ExamCome e2:gradeSubNotComes){
                        if (a.getSubjectId().equals(e2.getSubjectId())){
                            a.setNotCome(e2.getNotCome());
                            a.setActuallyCome(a.getActuallyCome()-e2.getNotCome());
                        }
                    }
                }
            }
            averageInfos.add(averageGrade.get(0));
        }

        //查询每个班的层次名称
        List<SchoolClassLevel> levels = this.averageMapper.findClassLevel(pager);
        for (AverageInfos infos:averageInfos){
            if (levels != null && levels.size() != 0 && levels.get(0) != null) {
                 //如果当前循环班级 层次id 不等于0 则判断层次名称
                if (infos.getClassLevelId() != 0) {
                    for (SchoolClassLevel level :levels){
                        if (level.getClassLevelId().equals(infos.getClassLevelId())){
                            infos.setClassLevel(level.getClasslevelName());
                        }
                    }
                }
            }
        }

        //按照前端传入的班级排序
        List<AverageInfos> newAverageInfos  = new ArrayList<>();
        List classIds = (List) pager.getParameters().get("classIdList");
        for (Object c:classIds){
            if (c!=null){
                String classId= String.valueOf(c);
                if (averageInfos!=null && averageInfos.size()!=0){
                    for (AverageInfos sr:averageInfos){
                        if (sr!=null && sr.getClassId()!=null){
                            if (sr.getClassId().equals(Long.valueOf(classId))){
                                newAverageInfos.add(sr);
                            }
                        }
                    }
                }
            }
        }
        //传入年级信息
        newAverageInfos.add(averageInfos.get(averageInfos.size()-1));


        pager.setContent(newAverageInfos);
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(pager);
        return apiResult;
    }



    /**
     * 计算分页所需班级id
     * @param pager
     * @return
     */
    public Pager classIdPager(Pager pager){
        //获取班级集合
        List<Integer> classIdList = (List<Integer>) pager.getParameters().get("classIdList");
        //设置分页总条数
        pager.setRecordTotal(classIdList.size());
        //开始下标
        int minRow = pager.getMinRow();
        //结束下标
        int maxRow = pager.getMaxRow();

        //把开始下标之前的classId设置为空
        if (minRow != 0) {
            for (int i = 0; i < minRow; i++) {
                if (i != minRow){
                    if (i<classIdList.size()) {
                        classIdList.set(i, null);
                    }
                }
            }
        }
        //把结束下标之后的classId设置为空
        if(maxRow < classIdList.size()){
            for (int i = maxRow; i<classIdList.size(); i++){
                if (i != maxRow - 1){
                    classIdList.set(i,null);
                }
            }
        }
        return pager;
    }


    /**
     * 平均分导出
     *
     * @param response
     * @return
     */
    @Override
    public ApiResult AverageExport(HttpServletResponse response,Pager pager,HttpServletRequest request) {
        //查询数据
        ApiResult apiResult = findAverageInfos(pager, request);
        Pager p = (Pager) apiResult.getData();
        List<AverageInfos> list = p.getContent();

        //判断是否选择总分
        boolean isZf = false;
        List<Integer> zfList = (List<Integer>) p.getParameters().get("curriculumIdList");
        for (Integer curriculumId:zfList){
            if (curriculumId==0){
                isZf = true;
                break;
            }
        }


        //导出
        OutputStream out = null;
        ApiResult result = new ApiResult();

        try {
            //获取流
            try {
                out = response.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Excel结果集
            List<List<String>> dataHandler = new ArrayList<List<String>>();



            if (list!=null && list.size()!=0){
                for(AverageInfos info : list){
                    if (info != null){
                        List<String> stringList = new ArrayList<>();
                        //设置班号
                        //判断是否最后一条年级情况
                        if (info.getClassNumber()==null){
                            stringList.add("年级情况");
                        }else {
                            stringList.add(String.valueOf(info.getClassNumber()));
                        }
                        //设置班主任
                        if (info.getClassHeadmaster()==null){
                            stringList.add("");
                        }else {
                            stringList.add(info.getClassHeadmaster());
                        }
                        if (isZf) {
                            //设置总分信息
                            AverageRsp total = info.getAverageTotal();

                            //实考人数
                            if (total.getActuallyCome() == null) {
                                stringList.add("");
                            } else {
                                stringList.add(String.valueOf(total.getActuallyCome()));
                            }

                            //缺考人数
                            if (total.getNotCome() == null) {
                                stringList.add("");
                            } else {
                                stringList.add(String.valueOf(total.getNotCome()));
                            }

                            //平均分
                            if (total.getAvgScore() == null) {
                                stringList.add("");
                            } else {
                                stringList.add(String.valueOf(total.getAvgScore()));
                            }

                            //排名
                            if (total.getExamSort() == null) {
                                stringList.add("");
                            } else {
                                stringList.add(String.valueOf(total.getExamSort()));
                            }

                            //难度系数
                            if (total.getDegree() == null) {
                                stringList.add("");
                            } else {
                                stringList.add(String.valueOf(total.getDegree()));
                            }

                            //最高分
                            if (total.getMaxScore() == null) {
                                stringList.add("");
                            } else {
                                stringList.add(String.valueOf(total.getMaxScore()));
                            }

                            //最低分
                            if (total.getMinScore() == null) {
                                stringList.add("");
                            } else {
                                stringList.add(String.valueOf(total.getMinScore()));
                            }
                        }

                        //各科参数设置
                        List<AverageRsp> averageRsps = info.getAverageRspList();

                        //未考试科目 设置为空字符串
                        boolean isKs = false;
                        //遍历前端所需要展示的课程
                        for(Integer subjectId:zfList){
                            //遍历查询出来的课程
                            if (averageRsps!=null && averageRsps.size()!=0 && averageRsps.get(0)!=null){
                                for (AverageRsp averageRsp:averageRsps){
                                    //如果id相等 则考了试 正常展示
                                    if (Long.valueOf(subjectId).equals(averageRsp.getSubjectId()) && subjectId!=0){
                                        isKs = true;
                                        //设置教师
                                        if (averageRsp.getFullName()==null){
                                            stringList.add("");
                                        }else {
                                            stringList.add(averageRsp.getFullName());
                                        }

                                        //实考人数
                                        if (averageRsp.getActuallyCome()==null){
                                            stringList.add("");
                                        }else {
                                            stringList.add(String.valueOf(averageRsp.getActuallyCome()));
                                        }

                                        //缺考人数
                                        if (averageRsp.getNotCome()==null){
                                            stringList.add("");
                                        }else {
                                            stringList.add(String.valueOf(averageRsp.getNotCome()));
                                        }

                                        //平均分
                                        if (averageRsp.getAvgScore()==null){
                                            stringList.add("");
                                        }else {
                                            stringList.add(String.valueOf(averageRsp.getAvgScore()));
                                        }

                                        //排名
                                        if (averageRsp.getExamSort()==null){
                                            stringList.add("");
                                        }else {
                                            stringList.add(String.valueOf(averageRsp.getExamSort()));
                                        }

                                        //难度系数
                                        if (averageRsp.getDegree()==null){
                                            stringList.add("");
                                        }else {
                                            stringList.add(averageRsp.getDegree());
                                        }

                                        //最高分
                                        if (averageRsp.getMaxScore()==null){
                                            stringList.add("");
                                        }else {
                                            stringList.add(String.valueOf(averageRsp.getMaxScore()));
                                        }

                                        //最低分
                                        if (averageRsp.getMinScore()==null){
                                            stringList.add("");
                                        }else {
                                            stringList.add(String.valueOf(averageRsp.getMinScore()));
                                        }
                                    }
                                }
                                if (isKs==false && subjectId != 0){
                                    //该课程未考试
                                    stringList.add("");
                                    stringList.add("");
                                    stringList.add("");
                                    stringList.add("");
                                    stringList.add("");
                                    stringList.add("");
                                    stringList.add("");
                                    stringList.add("");
                                }
                            }
                        }
                        dataHandler.add(stringList);
                    }
                }
            }

            //获取所有课程名称
            List<String> subjectNames = new ArrayList<>();
            if (zfList!=null && zfList.size()!=0 && zfList.get(0)!=null){
                for (Integer subjectId:zfList){
                    if (subjectId!=0) {
                        String subjectName = this.averageMapper.findSubjectName(subjectId);
                        subjectNames.add(subjectName);
                    }
                }
            }


            //设置表头
            String[] excelHeader0 = { "平均分统计" };
            String[] headnum0 = { "0,0,0,"+ (dataHandler.get(0).size()-1)};

            //表头第二行
            List<String> excle1 = new ArrayList<>();
            excle1.add("班号");
            excle1.add("班主任");
            List<String> num1 = new ArrayList<>();
            num1.add("1,2,0,0");
            num1.add("1,2,1,1");
            if (isZf) {
                //需要展示总分
                excle1.add("总分");
                excle1.add("");
                excle1.add("");
                excle1.add("");
                excle1.add("");
                excle1.add("");
                excle1.add("");
                num1.add("1,1,2,8");

                for (int i = 0; i < subjectNames.size(); i++) {
                    excle1.add(subjectNames.get(i));
                    excle1.add("");
                    excle1.add("");
                    excle1.add("");
                    excle1.add("");
                    excle1.add("");
                    excle1.add("");
                    excle1.add("");
                    //合并单元格的起始列数
                    int i1 = 1 + (i + 1) * 8;
                    int i2 = i1 + 7;
                    num1.add("1,1," + i1 + "," + i2);

                }
            }else {
                //不需要展示总分
                for (int i = 0; i < subjectNames.size(); i++) {
                    excle1.add(subjectNames.get(i));
                    excle1.add("");
                    excle1.add("");
                    excle1.add("");
                    excle1.add("");
                    excle1.add("");
                    excle1.add("");
                    excle1.add("");
                    //合并单元格的起始列数
                    int i1 = 2 + (i * 8);
                    int i2 = i1 + 7;
                    num1.add("1,1," + i1 + "," + i2);
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
            if (isZf){
                excle2.add("实考人数");
                num2.add("2,2,2,2");
                excle2.add("缺考人数");
                num2.add("2,2,3,3");
                excle2.add("平均分");
                num2.add("2,2,4,4");
                excle2.add("同层次排名");
                num2.add("2,2,5,5");
                excle2.add("难度系数");
                num2.add("2,2,6,6");
                excle2.add("最高分");
                num2.add("2,2,7,7");
                excle2.add("最低分");
                num2.add("2,2,8,8");
                int n = 9;
                for (int i = 0; i < subjectNames.size(); i++) {
                    excle2.add("教师");
                    num1.add("2,2," + n + "," + n);
                    excle2.add("实考人数");
                    num1.add("2,2," + n + "," + n);
                    excle2.add("缺考人数");
                    num1.add("2,2," + n + 2 + "," + n + 2);
                    excle2.add("平均分");
                    num1.add("2,2," + n + 3 + "," + n + 3);
                    excle2.add("同层次排名");
                    num1.add("2,2," + n + 4 + "," + n + 4);
                    excle2.add("难度系数");
                    num1.add("2,2," + n + 5 + "," + n + 5);
                    excle2.add("最高分");
                    num1.add("2,2," + n + 6 + "," + n + 6);
                    excle2.add("最低分");
                    num1.add("2,2," + n + 7 + "," + n + 7);
                    n = n + 8;
                }
            }else {
                int n = 3;
                for (int i = 0; i < subjectNames.size(); i++) {
                    excle2.add("教师");
                    num1.add("2,2," + n + "," + n);
                    excle2.add("实考人数");
                    num1.add("2,2," + n + "," + n);
                    excle2.add("缺考人数");
                    num1.add("2,2," + n + 2 + "," + n + 2);
                    excle2.add("平均分");
                    num1.add("2,2," + n + 3 + "," + n + 3);
                    excle2.add("同层次排名");
                    num1.add("2,2," + n + 4 + "," + n + 4);
                    excle2.add("难度系数");
                    num1.add("2,2," + n + 5 + "," + n + 5);
                    excle2.add("最高分");
                    num1.add("2,2," + n + 6 + "," + n + 6);
                    excle2.add("最低分");
                    num1.add("2,2," + n + 7 + "," + n + 7);
                    n = n + 8;

                }
            }
            String[] excelHeader2 = excle2.toArray(new String[0]);
            String[] headnum2 = num2.toArray(new String[0]);


            // 声明一个工作簿
            HSSFWorkbook wb = new HSSFWorkbook();
            // 生成一种样式
            HSSFCellStyle style = wb.createCellStyle();
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

            headers.add(header0);
            headers.add(header1);
            headers.add(header2);




            //创建工作薄
           // XSSFWorkbook workbook = new XSSFWorkbook();
            try {
                //导出信息
                //exportExcelUtil.exportExcel(workbook, 0, "平均分信息", titles, dataHandler, out);
                //workbook.write(out);


                String fileName = "平均分统计";
                exportExcelUtil.exportExcel2(wb,0,"平均分统计",style2,headers,dataHandler,response,fileName);

            } catch (Exception e) {
                log.error("导出失败");
                e.printStackTrace();
            }
        } catch (Exception e) {
            log.error("无法写出文件");
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                log.error("关闭流失败");
                e.printStackTrace();
            }
        }


        result.setCode(ApiCode.export_success);
        result.setMsg(ApiCode.export_success_MSG);
        return result;
    }

}
