package com.cj.witscorefind.service.Impl;

import com.cj.witbasics.entity.StudentScore;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witcommon.utils.excle.exportExcelUtil;
import com.cj.witscorefind.entity.*;
import com.cj.witscorefind.mapper.AverageMapper;
import com.cj.witscorefind.mapper.CriticalRawMapper;
import com.cj.witscorefind.mapper.ShortlistedMapper;
import com.cj.witscorefind.service.CriticalRawService;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * 临界生统计
 * Created by XD on 2018/6/12.
 */
@Service
public class CriticalRawServiceImpl implements CriticalRawService {

    private static final Logger log = LoggerFactory.getLogger(CriticalRawServiceImpl.class);

    @Autowired
    private CriticalRawMapper criticalRawMapper;

    @Autowired
    private ShortlistedMapper shortlistedMapper;

    @Autowired
    private AverageMapper averageMapper;

    @Value("${school_id}")
    private String schoolId;


    /**
     * 转为Long
     *
     * @return
     */
    private Long toLong() {
        System.out.println(this.schoolId);
        return Long.valueOf(this.schoolId);
    }


    /**
     * 临界生统计
     *
     * @param p
     * @return
     */
    @Override
    public ApiResult findCriticalRaw(Pager pager) {
        //返回结果
        ApiResult apiResult = new ApiResult();
        //设置学校id
        Map<String, Object> map = pager.getParameters();
        map.put("schoolId", toLong());
        pager.setParameters(map);

        //存下  需要查询的班级id集合
        List<Long> classIdList = (List<Long>) pager.getParameters().get("classIdList");

        //查询该类型下 所有班级id
        List<Long> classIdByType = shortlistedMapper.findClassIdByType(pager);
        //作为查询条件
        pager.getParameters().put("newClassIdList", classIdByType);


        //取出线上分
        Object score1 = pager.getParameters().get("onlineScore");
        BigDecimal onlineScore = new BigDecimal((String) score1);
        //取出下线分
        Object score2 = pager.getParameters().get("offlineScore");
        BigDecimal offlineScore = new BigDecimal((String) score2);


        //查询各班、各科、各班总分 各档入围分数线
        List<ShortlistedResp> criticalRawRespList = this.criticalRawMapper.findFindShortlisted(pager);


        //当 不选课程，size=0时  手动添加属性
        if (criticalRawRespList!=null && criticalRawRespList.size()==0){
            //查询各班信息+总分信息 各档入围分数线
            criticalRawRespList = this.criticalRawMapper.findFindShortlistedByTotal(pager);
        }

        //总分

        //查询各班每个人的总分
        List<ShortlistedScore> totalScores = this.criticalRawMapper.findTotalScores(pager);

        //遍历各班
        if (criticalRawRespList != null && criticalRawRespList.size() != 0 && criticalRawRespList.get(0) != null) {
            for (ShortlistedResp sr : criticalRawRespList) {
                //遍历总分各档
                List<GradeShort> gsList = sr.getTotalList();
                if (gsList != null && gsList.size() != 0 && gsList.get(0) != null) {
                    for (GradeShort gs : gsList) {
                        //获取分数线
                        BigDecimal fsx = gs.getTotalScore();
                        //计算线上分  分数线+线上分数
                        BigDecimal xsf = fsx.add(onlineScore);
                        //计算线下分  分数线-线下分数
                        BigDecimal xxf = fsx.subtract(offlineScore);

                        //创建线上人数
                        Integer onlineNum = 0;
                        //创建线下人数
                        Integer offlineNum = 0;


                        //遍历查询出来的分数信息
                        if (totalScores != null && totalScores.size() != 0 && totalScores.get(0) != null) {
                            for (ShortlistedScore ss : totalScores) {
                                //判断班级
                                if (sr.getClassId().equals(ss.getClassId())) {
                                    //遍历总分信息
                                    List<StudentScore> scores = ss.getScores();
                                    if (scores != null && scores.size() != 0 && scores.get(0) != null) {
                                        for (StudentScore score : scores) {
                                            //获取分数
                                            BigDecimal s = score.getScore();
                                            //分数和入围分数线比较
                                            int a = s.compareTo(fsx);
                                            if (a == 0) {//相等
                                                //则判定为 线上人数+1
                                                onlineNum++;
                                            }
                                            if (a == 1) {//分数 大于 分数线
                                                //比较是否小于等于 线上分数
                                                int a1 = s.compareTo(xsf);
                                                if (a1 == 0 || a1 == -1) {//分数小于等于线上分数
                                                    //线上人数+1
                                                    onlineNum++;
                                                }
                                            }
                                            if (a == -1) {//分数 小于 分数线
                                                //比较是否大于等于 线下分数
                                                int a2 = s.compareTo(xxf);
                                                if (a2 == 0 || a2 == 1) {//分数大于等于线下分数
                                                    //线下人数+1
                                                    offlineNum++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //设置线上线下人数
                        gs.setOnlineNum(onlineNum);
                        gs.setOfflineNum(offlineNum);
                    }
                }
            }
        }


        //各科

        //查询各班各学科每个人的成绩
        List<ShortlistedScore> scores = this.criticalRawMapper.findScores(pager);

        //遍历各班
        if (criticalRawRespList != null && criticalRawRespList.size() != 0 && criticalRawRespList.get(0) != null) {
            for (ShortlistedResp sr : criticalRawRespList) {
                //遍历各科
                List<Shortlisted> shortlisteds = sr.getShortlistedList();
                if (shortlisteds != null && shortlisteds.size() != 0 && shortlisteds.get(0) != null) {
                    for (Shortlisted shortlisted : shortlisteds) {
                        //遍历各档次
                        List<GradeShort> gradeShorts = shortlisted.getGradeShorts();
                        for (GradeShort gs : gradeShorts) {
                            //获取分数线
                            BigDecimal fsx = gs.getDNumScore();
                            //计算线上分  分数线+线上分数
                            BigDecimal xsf = fsx.add(onlineScore);
                            //计算线下分  分数线-线下分数
                            BigDecimal xxf = fsx.subtract(offlineScore);

                            //创建线上人数
                            Integer onlineNum = 0;
                            //创建线下人数
                            Integer offlineNum = 0;

                            //遍历查询出来的各科分数信息
                            //遍历班级
                            if (scores != null && scores.size() != 0 && scores.get(0) != null) {
                                for (ShortlistedScore ss : scores) {
                                    //判断班级id
                                    if (sr.getClassId().equals(ss.getClassId())) {
                                        //遍历课程
                                        List<SubScore> subScores = ss.getSubScores();
                                        if (subScores != null && subScores.size() != 0 && subScores.get(0) != null) {
                                            for (SubScore subScore : subScores) {
                                                //判断课程id
                                                if (shortlisted.getSubjectId().equals(subScore.getSubjectId())) {
                                                    //遍历分数
                                                    List<StudentScore> scoreList = subScore.getScores();
                                                    if (scoreList != null && scoreList.size() != 0 && scoreList.get(0) != null) {
                                                        for (StudentScore score : scoreList) {
                                                            //获取考试分数
                                                            BigDecimal s = score.getScore();
                                                            //分数和入围分数线比较
                                                            int a = s.compareTo(fsx);
                                                            if (a == 0) {//相等
                                                                //则判定为 线上人数+1
                                                                onlineNum++;
                                                            }
                                                            if (a == 1) {//分数 大于 分数线
                                                                //比较是否小于等于 线上分数
                                                                int a1 = s.compareTo(xsf);
                                                                if (a1 == 0 || a1 == -1) {//分数小于等于线上分数
                                                                    //线上人数+1
                                                                    onlineNum++;
                                                                }
                                                            }
                                                            if (a == -1) {//分数 小于 分数线
                                                                //比较是否大于等于 线下分数
                                                                int a2 = s.compareTo(xxf);
                                                                if (a2 == 0 || a2 == 1) {//分数大于等于线下分数
                                                                    //线下人数+1
                                                                    offlineNum++;
                                                                }
                                                            }

                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //设置线上线下人数
                            gs.setOnlineNum(onlineNum);
                            gs.setOfflineNum(offlineNum);
                        }
                    }
                }
            }
        }


        //年级情况

        //创建年级情况
        ShortlistedResp shortlistedResp = new ShortlistedResp();
        shortlistedResp.setTotalList(new ArrayList<>());
        shortlistedResp.setShortlistedList(new ArrayList<>());
        criticalRawRespList.add(shortlistedResp);

        //年级总分情况
        //遍历总分的档次有几个
        if (criticalRawRespList != null && criticalRawRespList.size() != 0 && criticalRawRespList.get(0) != null) {
            //设置班级类型
            shortlistedResp.setClassTypeId(criticalRawRespList.get(0).getClassTypeId());
            shortlistedResp.setClassType(criticalRawRespList.get(0).getClassType());
            //获取第一条记录 遍历档次
            List<GradeShort> gradeShorts = criticalRawRespList.get(0).getTotalList();
            if (gradeShorts != null && gradeShorts.size() != 0 && gradeShorts.get(0) != null) {
                for (GradeShort gs : gradeShorts) {

                    //创建对象
                    GradeShort totalGS = new GradeShort();

                    //创建线上人数
                    Integer onlineNum = 0;
                    //创建线下人数
                    Integer offlineNum = 0;

                    //遍历班级
                    for (ShortlistedResp sr : criticalRawRespList) {
                        if (sr.getClassId() != null) {
                            //遍历总分各档次
                            List<GradeShort> gradeShorts1 = sr.getTotalList();
                            if (gradeShorts1 != null && gradeShorts1.size() != 0 && gradeShorts1.get(0) != null) {
                                for (GradeShort gs1 : gradeShorts1) {
                                    //判断档次名称是否相等
                                    if (gs.getGradeName().equals(gs1.getGradeName())) {
                                        //相等  则 线上线 下人数++
                                        onlineNum += gs1.getOnlineNum();
                                        offlineNum += gs1.getOfflineNum();
                                        totalGS.setGradeName(gs1.getGradeName());
                                    }
                                }
                            }
                        }
                    }
                    totalGS.setOnlineNum(onlineNum);
                    totalGS.setOfflineNum(offlineNum);
                    shortlistedResp.getTotalList().add(totalGS);
                }
            }
        }


        //年级各科情况
        if (criticalRawRespList != null && criticalRawRespList.size() != 0 && criticalRawRespList.get(0) != null) {
            //获取课程集合
            List<Shortlisted> shortlisteds = criticalRawRespList.get(0).getShortlistedList();
            //遍历各个课程
            if (shortlisteds != null && shortlisteds.size() != 0 && shortlisteds.get(0) != null) {
                for (Shortlisted s : shortlisteds) {
                    //创建课程对象
                    Shortlisted shortlisted = new Shortlisted();
                    shortlisted.setSubjectId(s.getSubjectId());
                    shortlisted.setSubjectName(s.getSubjectName());
                    shortlisted.setGradeShorts(new ArrayList<>());
                    //遍历课程的档次
                    List<GradeShort> list = s.getGradeShorts();
                    if (list != null && list.size() != 0 && list.get(0) != null) {
                        for (GradeShort gs : list) {
                            //创建对象
                            GradeShort gradeGS = new GradeShort();

                            //创建线上人数
                            Integer onlineNum = 0;
                            //创建线下人数
                            Integer offlineNum = 0;


                            //遍历各班
                            for (ShortlistedResp sr : criticalRawRespList) {
                                //判断班级id不能为空 过滤最后一条年级情况
                                if (sr.getClassId() != null) {
                                    //遍历一个班的各科
                                    if (sr.getShortlistedList() != null && sr.getShortlistedList().size() != 0 && sr.getShortlistedList().get(0) != null) {
                                        for (Shortlisted gs1 : sr.getShortlistedList()) {
                                            //判断课程名称
                                            if (gs1.getSubjectId().equals(shortlisted.getSubjectId())) {
                                                //遍历这个学科的各档
                                                if (gs1.getGradeShorts() != null && gs1.getGradeShorts().size() != 0 && gs1.getGradeShorts().get(0) != null) {
                                                    for (GradeShort gs2 : gs1.getGradeShorts()) {
                                                        //判断档次名称
                                                        if (gs.getGradeName().equals(gs2.getGradeName())) {
                                                            //线上 线下人数 累加
                                                            offlineNum += gs2.getOfflineNum();
                                                            onlineNum += gs2.getOnlineNum();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //设置总的线上人数线下人数
                            gradeGS.setOnlineNum(onlineNum);
                            gradeGS.setOfflineNum(offlineNum);
                            //设置档次名称
                            gradeGS.setGradeName(gs.getGradeName());
                            shortlisted.getGradeShorts().add(gradeGS);
                        }
                    }

                    shortlistedResp.getShortlistedList().add(shortlisted);
                }
            }

        }


        //移除不需要展示的班级


        //判断是否要查总分   不查则设置为空
        //判断是否需要查询总分信息
        boolean isTotal = false;
        List<Integer> curriculumIdList = (List<Integer>) pager.getParameters().get("curriculumIdList");
        for (Integer s : curriculumIdList) {
            if (s.equals(0)) {
                isTotal = true;
                break;
            }
        }
        if (!isTotal) {
            //不需要查询总分， 总分信息设置为空
            //遍历各班 和最后一条年级情况
            if (criticalRawRespList != null && criticalRawRespList.size() != 0 && criticalRawRespList.get(0) != null) {
                for (ShortlistedResp sr : criticalRawRespList) {
                    sr.setTotalList(null);
                }
            }
        }

        //计算当前页的班级id集合
        pager = this.classIdPager(pager);
        //取出分页后的班级id集合
        List<Long> classIds = (List<Long>) pager.getParameters().get("classIdList");

        //遍历班级
        if (criticalRawRespList != null && criticalRawRespList.size() != 0 && criticalRawRespList.get(0) != null) {
            for (int k = 0; k < criticalRawRespList.size(); k++) {
                //过滤最后一条年级信息
                if (criticalRawRespList.get(k).getClassId() != null) {
                    //判断表示
                    boolean isClassId = false;
                    //遍历分页后班级id
                    if (classIds != null && classIds.size() != 0) {
                        for (int i = 0; i < classIds.size(); i++) {
                            if (classIds.get(i) != null) {
                                String classId1 = String.valueOf(classIds.get(i));
                                String classId2 = String.valueOf(criticalRawRespList.get(k).getClassId());
                                if (classId2 != null) {
                                    if (classId2.equals(classId1)) {
                                        isClassId = true;
                                    }
                                }
                            }
                        }
                    }
                    //如果为false 则不需要展示这个班级
                    if (!isClassId) {
                        criticalRawRespList.set(k, null);
                    }
                }
            }
        }

        //按照前端传入的班级排序
        List<ShortlistedResp> newCriticalRawRespList = new ArrayList<>();
        for (Object c:classIds){
            if (c!=null){
                String classId= String.valueOf(c);
                if (criticalRawRespList!=null && criticalRawRespList.size()!=0){
                    for (ShortlistedResp sr:criticalRawRespList){
                        if (sr!=null && sr.getClassId()!=null){
                            if (sr.getClassId().equals(Long.valueOf(classId))){
                                newCriticalRawRespList.add(sr);
                            }
                        }
                    }
                }
            }
        }
        //传入年级信息
        newCriticalRawRespList.add(criticalRawRespList.get(criticalRawRespList.size()-1));



        pager.setContent(newCriticalRawRespList);
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(pager);
        return apiResult;
    }


    /**
     * 计算分页所需班级id
     *
     * @param pager
     * @return
     */
    public Pager classIdPager(Pager pager) {
        //获取班级集合
        List<Long> classIdList = (List<Long>) pager.getParameters().get("classIdList");
        //设置分页总条数
        pager.setRecordTotal(classIdList.size());
        //开始下标
        int minRow = pager.getMinRow();
        //结束下标
        int maxRow = pager.getMaxRow();

        //把开始下标之前的classId设置为空
        if (minRow != 0) {
            for (int i = 0; i < minRow; i++) {
                if (i != minRow) {
                    if (i < classIdList.size()) {
                        classIdList.set(i, null);
                    }
                }
            }
        }
        //把结束下标之后的classId设置为空
        if (maxRow < classIdList.size()) {
            for (int i = maxRow; i < classIdList.size(); i++) {
                if (i != maxRow - 1) {
                    classIdList.set(i, null);
                }
            }
        }
        return pager;
    }


    /**
     * 导出
     *
     * @param response
     * @param p
     * @param request
     * @return
     */
    @Override
    public ApiResult criticalExport(HttpServletResponse response, Pager pager, HttpServletRequest request) {
        //获取数据
        ApiResult apiResult = findCriticalRaw(pager);
        Pager p = (Pager) apiResult.getData();
        List<ShortlistedResp> list = p.getContent();

        //判断是否选择总分
        boolean isZf = false;
        List<Integer> zfList = (List<Integer>) p.getParameters().get("curriculumIdList");
        for (Integer curriculumId : zfList) {
            if (curriculumId == 0) {
                isZf = true;
                break;
            }
        }

        //总分档次列表
        Set totalSet = new LinkedHashSet();
        if (isZf) {
            //获取每个班级的总分档次（并集）
            //遍历每个班级
            for (ShortlistedResp info1 : list) {
                if (info1 != null) {
                    //获取总分档次列表
                    List<GradeShort> gradeShorts = info1.getTotalList();
                    if (gradeShorts != null && gradeShorts.size() != 0 && gradeShorts.get(0) != null) {
                        for (GradeShort gs : gradeShorts) {
                            totalSet.add(gs.getGradeName());
                        }
                    }
                }
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
                for(ShortlistedResp info : list){
                    if (info!=null){
                        List<String> stringList = new ArrayList<>();
                        //设置班号
                        //判断是否最后一条年级情况
                        if (info.getClassNumber()==null){
                            stringList.add("年级情况");
                        }else {
                            stringList.add(String.valueOf(info.getClassNumber()));
                        }
                        //设置班级类型
                        if (info.getClassType()==null){
                            stringList.add("");
                        }else {
                            stringList.add(info.getClassType());
                        }

                        //设置班级层次
                        if (info.getClassLevel()==null){
                            stringList.add("");
                        }else {
                            stringList.add(info.getClassLevel());
                        }

                        //设置班主任
                        if (info.getClassHeadmaster()==null){
                            stringList.add("");
                        }else {
                            stringList.add(info.getClassHeadmaster());
                        }

                        if (isZf) {
                            //设置总分信息
                            //遍历总分列表的并集
                            if (totalSet!=null){
                                Iterator<String> it = totalSet.iterator();
                                while (it.hasNext()) {
                                    String str = it.next();
                                    //档次匹配标识
                                    //总分 可不考虑档次匹配问题，拿到的班级都是同年级同类型的，所以他们的总分档次都是一样的
                                    boolean isdc = false;
                                    List<GradeShort> gradeShorts = info.getTotalList();
                                    if (gradeShorts!=null && gradeShorts.size()!=0 && gradeShorts.get(0)!=null){
                                        for (GradeShort gs:gradeShorts){
                                            if (str.equals(gs.getGradeName())) {
                                                //档次名称匹配 正常展示
                                                isdc = true;
                                                if (gs.getOnlineNum()==null){
                                                    stringList.add("");
                                                }else {
                                                    stringList.add(String.valueOf(gs.getOnlineNum()));
                                                }
                                                if (gs.getOfflineNum()==null){
                                                    stringList.add("");
                                                }else {
                                                    stringList.add(String.valueOf(gs.getOfflineNum()));
                                                }
                                            }
                                        }
                                        if (isdc==false){
                                            //档次不匹配  显示空字符串
                                            stringList.add("");
                                            stringList.add("");
                                        }
                                    }
                                }
                            }
                        }

                        //获取课程信息
                        List<Shortlisted> shortlisteds = info.getShortlistedList();
                        //未考试科目 设置为空字符串
                        boolean isKs = false;
                        //遍历课程id集合
                        List<Integer> subjectIds = (List<Integer>) p.getParameters().get("curriculumIdList");
                        if (subjectIds != null && subjectIds.size() != 0 && subjectIds.get(0) != null) {
                            for (Integer subjectId:subjectIds){
                                //获取该课程档次的并集  1班 ABC   2班 ABCD 并集 ABCD
                                Set subjectSet = new LinkedHashSet();
                                for(ShortlistedResp info1 : list){
                                    if (info1!=null){
                                        //获取档次列表
                                        List<Shortlisted> shortlisteds1 = info1.getShortlistedList();
                                        if (shortlisteds1!=null && shortlisteds1.size()!=0 && shortlisteds1.get(0)!=null){
                                            for (Shortlisted s:shortlisteds1){
                                                if (s.getSubjectId().equals(Long.valueOf(subjectId))) {
                                                    List<GradeShort> gradeShorts = s.getGradeShorts();
                                                    if (gradeShorts != null && gradeShorts.size() != 0 && gradeShorts.get(0) != null) {
                                                        for (GradeShort gs : gradeShorts) {
                                                            subjectSet.add(gs.getGradeName());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                //遍历课程
                                if (shortlisteds!=null && shortlisteds.size()!=0 && shortlisteds.get(0)!=null){
                                    for (Shortlisted s:shortlisteds){
                                        //如果id相等 则考了试 正常展示
                                        if (Long.valueOf(subjectId).equals(s.getSubjectId()) && subjectId!=0) {
                                            isKs = true;
                                            //考虑档次是否匹配
                                            //遍历档次列表（并集）
                                            if (subjectSet!=null){
                                                Iterator<String> it = subjectSet.iterator();
                                                while (it.hasNext()) {
                                                    //档次是否匹配标识
                                                    boolean isdc = false;
                                                    String str = it.next();
                                                    //遍历该班该课程的档次集合
                                                    List<GradeShort> list1 = s.getGradeShorts();
                                                    if (list1!=null && list1.size()!=0 && list1.get(0)!=null){
                                                        for (GradeShort gs:list1){
                                                            if (gs.getGradeName().equals(str)){
                                                                //档次名称匹配 正常显示
                                                                isdc = true;
                                                                //设置线上围人数
                                                                if (gs.getOnlineNum()==null){
                                                                    stringList.add("");
                                                                }else {
                                                                    stringList.add(String.valueOf(gs.getOnlineNum()));
                                                                }
                                                                //设置线下人数
                                                                if (gs.getOfflineNum()==null){
                                                                    stringList.add("");
                                                                }else {
                                                                    stringList.add(String.valueOf(gs.getOfflineNum()));
                                                                }
                                                            }
                                                        }
                                                        if (isdc==false){
                                                            //无档次匹配  显示为空
                                                            stringList.add("");
                                                            stringList.add("");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (isKs==false){
                                        //该课程未考试
                                        //遍历档次并集
                                        if (subjectSet!=null){
                                            Iterator<String> it = subjectSet.iterator();
                                            while (it.hasNext()) {
                                                String str = it.next();
                                                stringList.add("");
                                                stringList.add("");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        dataHandler.add(stringList);
                    }
                }
            }


            //设置表头
            String[] excelHeader0 = { "临界生统计" };
            String[] headnum0 = { "0,0,0,"+ (dataHandler.get(0).size()-1)};


            //表头第二行
            List<String> excle1 = new ArrayList<>();
            excle1.add("班号");
            excle1.add("班级类型");
            excle1.add("班级层次");
            excle1.add("班主任");
            List<String> num1 = new ArrayList<>();
            num1.add("1,3,0,0");
            num1.add("1,3,1,1");
            num1.add("1,3,2,2");
            num1.add("1,3,3,3");

            if (isZf) {//需要展示总分
                excle1.add("总分");
                //空格站位数 = 2*档次个数 - 1
                int n = (2*totalSet.size())-1;
                for (int i=1;i<=n;i++){
                    excle1.add("");
                }
                int s= 4 + n;
                num1.add("1,1,4,"+ s);

                //起始坐标  档次数站位依次相加
                int n3 = 0;
                //展示各科
                //遍历各科的并集
                List<Integer> subjectIds = (List<Integer>) p.getParameters().get("curriculumIdList");
                if (subjectIds!=null && subjectIds.size()!=0 && subjectIds.get(0)!=null){
                    int i = 0;
                    for (Integer subjectId:subjectIds){
                        if (subjectId!=0) {
                            //遍历各班级的 这个课程  取出档次并集
                            Set subjectSet = new LinkedHashSet();
                            for(ShortlistedResp info1 : list){
                                if (info1!=null){
                                    //获取档次列表
                                    List<Shortlisted> shortlisteds1 = info1.getShortlistedList();
                                    if (shortlisteds1!=null && shortlisteds1.size()!=0 && shortlisteds1.get(0)!=null){
                                        for (Shortlisted s1:shortlisteds1){
                                            if (s1.getSubjectId().equals(Long.valueOf(subjectId))) {
                                                List<GradeShort> gradeShorts = s1.getGradeShorts();
                                                if (gradeShorts != null && gradeShorts.size() != 0 && gradeShorts.get(0) != null) {
                                                    for (GradeShort gs : gradeShorts) {
                                                        subjectSet.add(gs.getGradeName());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //设置名称
                            String subjectName = this.averageMapper.findSubjectName(subjectId);
                            excle1.add(subjectName);
                            //添加占位符： 2 * 并集档次个数 - 1
                            int n1 = 2 * subjectSet.size() - 1;
                            n3 += n1;
                            for (int i1=1;i1<=n1;i1++){
                                excle1.add("");
                            }
                            //合并单元格的起始坐标： 总分的站位 + 当前学科站位 * 学科数
                            int s1=s + 1 + n3-n1 + i;
                            int s2 = s1+n1;
                            num1.add("1,1," + s1 + "," + s2);
                            i++;
                        }
                    }
                }
            }else {//不需要展示总分
                //起始坐标  档次数站位依次相加
                int n3 = 0;
                //展示各科
                //遍历各科的并集
                List<Integer> subjectIds = (List<Integer>) p.getParameters().get("curriculumIdList");
                if (subjectIds!=null && subjectIds.size()!=0 && subjectIds.get(0)!=null){
                    int i = 0;
                    for (Integer subjectId:subjectIds){
                        if (subjectId!=0){
                            //遍历各班级的 这个课程  取出档次并集
                            Set subjectSet = new LinkedHashSet();
                            for(ShortlistedResp info1 : list){
                                if (info1!=null){
                                    //获取档次列表
                                    List<Shortlisted> shortlisteds1 = info1.getShortlistedList();
                                    if (shortlisteds1!=null && shortlisteds1.size()!=0 && shortlisteds1.get(0)!=null){
                                        for (Shortlisted s1:shortlisteds1){
                                            if (s1.getSubjectId().equals(Long.valueOf(subjectId))) {
                                                List<GradeShort> gradeShorts = s1.getGradeShorts();
                                                if (gradeShorts != null && gradeShorts.size() != 0 && gradeShorts.get(0) != null) {
                                                    for (GradeShort gs : gradeShorts) {
                                                        subjectSet.add(gs.getGradeName());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //设置名称
                            String subjectName = this.averageMapper.findSubjectName(subjectId);
                            excle1.add(subjectName);
                            //添加占位符： 7 * 并集档次个数 - 1
                            int n1 = 2 * subjectSet.size() - 1;
                            n3 += n1;
                            for (int i1=1;i1<=n1;i1++){
                                excle1.add("");
                            }
                            //合并单元格的起始坐标： 总分的站位 + 当前学科站位 * 学科数
                            int s1=  4 + n3-n1 + i;
                            int s2 = s1+n1;
                            num1.add("1,1," + s1 + "," + s2);
                            i++;
                        }
                    }
                }
            }

            String[] excelHeader1 = excle1.toArray(new String[0]);
            String[] headnum1 = num1.toArray(new String[0]);



            //表头第三行
            List<String> excle2 = new ArrayList<>();
            excle2.add("");
            excle2.add("");
            excle2.add("");
            excle2.add("");
            List<String> num2 = new ArrayList<>();
            num2.add("2,2,0,0");
            num2.add("2,2,1,1");
            num2.add("2,2,2,2");
            num2.add("2,2,3,3");

            if(isZf){//展示总分
                int i = 1;
                if (totalSet!=null){
                    Iterator<String> it = totalSet.iterator();
                    while (it.hasNext()) {
                        String str = it.next();
                        excle2.add(str);
                        excle2.add("");
                        int n = (i * 2) + 2;
                        int n1 = n + 1;
                        num2.add("2,2," + n + "," + n1);
                        i++;
                    }
                }

                //单科

                //遍历各课程，获取该课程的并集
                List<Integer> subjectIds = (List<Integer>) p.getParameters().get("curriculumIdList");
                if (subjectIds != null && subjectIds.size() != 0 && subjectIds.get(0) != null) {
                    int i2 = 0;
                    int a = 0;
                    int subTotal = 0;
                    for (Integer subjectId : subjectIds) {
                        if (subjectId != 0) {
                            //遍历各班级的 这个课程  取出档次并集
                            Set subjectSet = new LinkedHashSet();
                            for (ShortlistedResp info1 : list) {
                                if (info1 != null) {
                                    //获取档次列表
                                    List<Shortlisted> shortlisteds1 = info1.getShortlistedList();
                                    if (shortlisteds1 != null && shortlisteds1.size() != 0 && shortlisteds1.get(0) != null) {
                                        for (Shortlisted s1 : shortlisteds1) {
                                            if (s1.getSubjectId().equals(Long.valueOf(subjectId))) {
                                                List<GradeShort> gradeShorts = s1.getGradeShorts();
                                                if (gradeShorts != null && gradeShorts.size() != 0 && gradeShorts.get(0) != null) {
                                                    for (GradeShort gs : gradeShorts) {
                                                        subjectSet.add(gs.getGradeName());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //单课程总站位
                            int toatl = subjectSet.size() * 2 ;

                            //遍历并集
                            if (subjectSet != null) {
                                Iterator<String> it = subjectSet.iterator();
                                int i1 = 0;
                                while (it.hasNext()) {
                                    String str = it.next();
                                    excle2.add(str);
                                    excle2.add("");
                                    int n = (2 * totalSet.size()) + 4 + (i1 * 2)  + subTotal;
                                    int n1 = n + 1;
                                    num2.add("2,2," + n + "," + n1);
                                    i1++;
                                }
                                subTotal += toatl;
                            }

                        }
                    }
                }

            }else {
                //单科

                //遍历各课程，获取该课程的并集
                List<Integer> subjectIds = (List<Integer>) p.getParameters().get("curriculumIdList");
                if (subjectIds != null && subjectIds.size() != 0 && subjectIds.get(0) != null) {
                    int i2 = 0;
                    int a = 0;
                    int subTotal = 0;
                    for (Integer subjectId : subjectIds) {
                        if (subjectId != 0) {
                            //遍历各班级的 这个课程  取出档次并集
                            Set subjectSet = new LinkedHashSet();
                            for (ShortlistedResp info1 : list) {
                                if (info1 != null) {
                                    //获取档次列表
                                    List<Shortlisted> shortlisteds1 = info1.getShortlistedList();
                                    if (shortlisteds1 != null && shortlisteds1.size() != 0 && shortlisteds1.get(0) != null) {
                                        for (Shortlisted s1 : shortlisteds1) {
                                            if (s1.getSubjectId().equals(Long.valueOf(subjectId))) {
                                                List<GradeShort> gradeShorts = s1.getGradeShorts();
                                                if (gradeShorts != null && gradeShorts.size() != 0 && gradeShorts.get(0) != null) {
                                                    for (GradeShort gs : gradeShorts) {
                                                        subjectSet.add(gs.getGradeName());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //单课程总站位
                            int toatl = subjectSet.size() * 2 ;

                            //遍历并集
                            if (subjectSet != null) {
                                Iterator<String> it = subjectSet.iterator();
                                int i1 = 0;
                                while (it.hasNext()) {
                                    String str = it.next();
                                    excle2.add(str);
                                    excle2.add("");
                                    int n =  4 + (i1 * 2)  + subTotal;
                                    int n1 = n + 1;
                                    num2.add("2,2," + n + "," + n1);
                                    i1++;
                                }
                                subTotal += toatl;
                            }

                        }
                    }
                }

            }


            String[] excelHeader2 = excle2.toArray(new String[0]);
            String[] headnum2 = num2.toArray(new String[0]);


            //线上分
            String online = (String) p.getParameters().get("onlineScore");
            String s = "线上" + online + "分";
            //线下分
            String offline = (String) p.getParameters().get("offlineScore");
            String x = "线下" + offline + "分";

            //表头第四行
            List<String> excle3 = new ArrayList<>();
            excle3.add("");
            excle3.add("");
            excle3.add("");
            excle3.add("");
            List<String> num3 = new ArrayList<>();
            num3.add("3,3,0,0");
            num3.add("3,3,1,1");
            num3.add("3,3,2,2");
            num3.add("3,3,3,3");

            if (isZf){//展示总分
                //遍历总分档次
                if(totalSet!=null && totalSet.size()!=0){
                    Iterator<String> it = totalSet.iterator();
                    //循环参数
                    int i = 1;
                    while (it.hasNext()) {
                        //每一个就是一个档次
                        String str = it.next();
                        excle3.add(s);
                        excle3.add(x);

                        int s1 = i*2;
                        num3.add("3,3," + s1 + "," + s1);
                        num3.add("3,3," + ++s1 + "," + s1);
                        i++;
                    }
                }

                //单科设置
                //遍历课程
                if (zfList!=null && zfList.size()!=0 && zfList.get(0)!=null){
                    int a = 0;
                    for (Integer subjectId:zfList){
                        if (subjectId!=0){
                            //根据课程id查询档次并集
                            //遍历各班级的 这个课程  取出档次并集
                            Set subjectSet = new LinkedHashSet();
                            for (ShortlistedResp info1 : list) {
                                if (info1 != null) {
                                    //获取档次列表
                                    List<Shortlisted> shortlisteds1 = info1.getShortlistedList();
                                    if (shortlisteds1 != null && shortlisteds1.size() != 0 && shortlisteds1.get(0) != null) {
                                        for (Shortlisted s1 : shortlisteds1) {
                                            if (s1.getSubjectId().equals(Long.valueOf(subjectId))) {
                                                List<GradeShort> gradeShorts = s1.getGradeShorts();
                                                if (gradeShorts != null && gradeShorts.size() != 0 && gradeShorts.get(0) != null) {
                                                    for (GradeShort gs : gradeShorts) {
                                                        subjectSet.add(gs.getGradeName());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //遍历档次并集
                            if (subjectSet!=null && subjectSet.size()!=0){
                                //课程总占位数
                                int s1 = 0;
                                for (int j = 0; j< subjectSet.size(); j++){
                                    //该课程占位数
                                    int s2 = subjectSet.size() * 2;
                                    //起始位置 = 固定参数 + 总分站位符  + 课程站位总数
                                    int s3 = 4 + totalSet.size() * 2 + 1 + s1;
                                    excle3.add(s);
                                    excle3.add(x);
                                    num3.add("3,3," + s3 + "," + s3);
                                    num3.add("3,3," + ++s3 + "," + s3);
                                    s1 += s2;
                                }
                            }
                        }
                    }
                }

            }else {
                //单科设置
                //遍历课程
                if (zfList!=null && zfList.size()!=0 && zfList.get(0)!=null){
                    int a = 0;
                    for (Integer subjectId:zfList){
                        if (subjectId!=0){
                            //根据课程id查询档次并集
                            //遍历各班级的 这个课程  取出档次并集
                            Set subjectSet = new LinkedHashSet();
                            for (ShortlistedResp info1 : list) {
                                if (info1 != null) {
                                    //获取档次列表
                                    List<Shortlisted> shortlisteds1 = info1.getShortlistedList();
                                    if (shortlisteds1 != null && shortlisteds1.size() != 0 && shortlisteds1.get(0) != null) {
                                        for (Shortlisted s1 : shortlisteds1) {
                                            if (s1.getSubjectId().equals(Long.valueOf(subjectId))) {
                                                List<GradeShort> gradeShorts = s1.getGradeShorts();
                                                if (gradeShorts != null && gradeShorts.size() != 0 && gradeShorts.get(0) != null) {
                                                    for (GradeShort gs : gradeShorts) {
                                                        subjectSet.add(gs.getGradeName());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //遍历档次并集
                            if (subjectSet!=null && subjectSet.size()!=0){
                                //课程总占位数
                                int s1 = 0;
                                for (int j = 0; j< subjectSet.size(); j++){
                                    //该课程占位数
                                    int s2 = subjectSet.size() * 2;
                                    //起始位置 = 固定参数 + 总分站位符  + 课程站位总数
                                    int s3 = 4  + 1 + s1;
                                    excle3.add(s);
                                    excle3.add(x);
                                    num3.add("3,3," + s3 + "," + s3);
                                    num3.add("3,3," + ++s3 + "," + s3);
                                    s1 += s2;
                                }
                            }
                        }
                    }
                }

            }
            String[] excelHeader3 = excle3.toArray(new String[0]);
            String[] headnum3 = num3.toArray(new String[0]);


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

            Map<String,Object> header3 = new HashMap<>();
            header3.put("excelHeader",excelHeader3);
            header3.put("headnum",headnum3);
            header3.put("style",style);

            headers.add(header0);
            headers.add(header1);
            headers.add(header2);
            headers.add(header3);
            try {
                //导出信息

                String fileName = "临界生统计";
                exportExcelUtil.exportExcel2(wb,0,"临界生统计",style2,headers,dataHandler,response,fileName);
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
