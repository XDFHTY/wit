package com.cj.witscorefind.service.Impl;

import com.cj.witbasics.entity.StudentScore;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witcommon.utils.excle.exportExcelUtil;
import com.cj.witscorefind.entity.*;
import com.cj.witscorefind.mapper.CriticalRawMapper;
import com.cj.witscorefind.mapper.ScorePeriodMapper;
import com.cj.witscorefind.mapper.ShortlistedMapper;
import com.cj.witscorefind.service.ScorePeriodService;
import com.cj.witscorefind.service.ShortlistedService;
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
import java.text.DecimalFormat;
import java.util.*;

/**
 * 分数分段统计
 * Created by XD on 2018/6/8.
 */
@Service
public class ScorePeriodServiceImpl implements ScorePeriodService {

    private static final Logger log = LoggerFactory.getLogger(ScorePeriodServiceImpl.class);

    @Autowired
    private ShortlistedMapper shortlistedMapper;

    @Autowired
    private ScorePeriodMapper scorePeriodMapper;

    @Autowired
    private CriticalRawMapper criticalRawMapper;

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

    @Override
    public ApiResult findScorePeriod(Pager pager) {
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
        pager.getParameters().put("newClassIdList",classIdByType);


        //取出分数段
        List<String> scores1 = (List<String>) pager.getParameters().get("scoreList");
        List<BigDecimal> scores = new ArrayList<>();
        //转换
        if (scores1!=null && scores1.size()!=0){
            for(String s:scores1){
                BigDecimal s1 = new BigDecimal(s);
                scores.add(s1);
            }
        }
        //升序排序
        Collections.sort(scores);
        //倒置
        Collections.reverse(scores);


        //创建返回对象集合
        List<ShortlistedResp> shortlistedResps = new ArrayList<>();

        //判断是总分还是单科
        Integer curriculumId = (Integer) pager.getParameters().get("curriculumId");
        if (curriculumId==0){
            //查询总分

            //查询各班每个人总分成绩
            //查询各班每个人的总分
            List<ShortlistedScore> totalScores = this.scorePeriodMapper.findTotalScores(pager);

            //遍历查询出来的分数信息
            if(totalScores!=null && totalScores.size()!=0 && totalScores.get(0)!=null){
                //每个ss就是一个班
                for (ShortlistedScore ss:totalScores){
                    //创建班级对象
                    ShortlistedResp shortlistedResp = new ShortlistedResp();
                    //设置班级id
                    shortlistedResp.setClassId(ss.getClassId());
                    //设置班号
                    shortlistedResp.setClassNumber(ss.getClassNumber());

                    //给班级设置分数段
                    Shortlisted shortlisted = new Shortlisted();
                    shortlistedResp.setShortlisted(shortlisted);
                    shortlisted.setSubjectName("总分");
                    shortlisted.setScorePeriods(new ArrayList<>());
                    //遍历前端输入分数段
                    if (scores!=null && scores.size()!=0 && scores.get(0)!=null) {
                        for (int i=0;i<scores.size()-1;i++){
                            //创建分数段对象
                            ScorePeriod scorePeriod = new ScorePeriod();
                            //设置最大分数
                            BigDecimal b = new BigDecimal(String.valueOf(scores.get(i)));
                            scorePeriod.setMaxScore(b);
                            //设置最小分数
                            BigDecimal b1 = new BigDecimal(String.valueOf(scores.get(i+1)));
                            scorePeriod.setMinScore(b1);
                            //设置人数默认为0
                            scorePeriod.setPeriodNum(0);
                            //编辑分数段名称
                            scorePeriod.setPeriodName(scores.get(i) + "-" + scores.get(i+1));
                            //添加至集合
                            shortlisted.getScorePeriods().add(scorePeriod);
                        }
                    }

                    //遍历分数信息
                    List<StudentScore> studentScoreList = ss.getScores();
                    if (studentScoreList!=null && studentScoreList.size()!=0 && studentScoreList.get(0)!=null){
                        for (StudentScore studentScore:studentScoreList){
                            //获取总分
                            BigDecimal score = studentScore.getScore();
                            //获取分数段集合
                            List<ScorePeriod> scorePeriods = shortlistedResp.getShortlisted().getScorePeriods();
                            //遍历分数段集合
                            if (scorePeriods != null && scorePeriods.size() != 0 && scorePeriods.get(0) != null) {
                                //判断分数是否等于最高分段分数
                                //取出最高分段分数
                                BigDecimal max = scorePeriods.get(0).getMaxScore();
                                //比较
                                int res = score.compareTo(max);
                                if (res==0){//相等 则属于这个分数段
                                    //人数累加
                                    scorePeriods.get(0).setPeriodNum(scorePeriods.get(0).getPeriodNum()+1);
                                }else {
                                    for (ScorePeriod sp : scorePeriods) {
                                        //取出最大分数
                                        BigDecimal maxScore = sp.getMaxScore();
                                        //取出最小分数
                                        BigDecimal minScore = sp.getMinScore();
                                        //分数和最大分数比较
                                        int a = score.compareTo(maxScore);
                                        if (a == -1) {
                                            //分数小于最大分数
                                            //再判断分数是否大于等于最小分数
                                            int a1 = score.compareTo(minScore);
                                            if (a1 == 0 || a1 == 1) {
                                                //分数 小于 最大分数 且 大于等于 最小分数  就认定为属于这个分数段
                                                // 人数+1
                                                sp.setPeriodNum(sp.getPeriodNum() + 1);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //把对象添加至返回集合中
                    shortlistedResps.add(shortlistedResp);
                }
            }



            //计算总分占比

            //遍历有几个分数段
            if (shortlistedResps != null && shortlistedResps.size() != 0 && shortlistedResps.get(0) != null) {
                //获取第一条信息  遍历分数段
                List<ScorePeriod> scorePeriods = shortlistedResps.get(0).getShortlisted().getScorePeriods();
                if (scorePeriods!=null && scorePeriods.size()!=0 && scorePeriods.get(0)!=null){
                    for (ScorePeriod sp:scorePeriods){
                        //声明总人数  一个分数段一个总人数  计算占比用
                        Integer total = 0;

                        //各班的这个分段 人数 累加
                        //遍历班级
                        for (ShortlistedResp sr:shortlistedResps){
                            //获取分数段集合
                            List<ScorePeriod> periods = sr.getShortlisted().getScorePeriods();
                            if (periods!=null && periods.size()!=0 && periods.get(0)!=null){
                                //遍历分数段
                                for (ScorePeriod period:periods){
                                    //判断分数段名称
                                    if (sp.getPeriodName().equals(period.getPeriodName())){
                                        //同一个分数段  总人数累加
                                        total += period.getPeriodNum();
                                        break;
                                    }
                                }
                            }
                        }
                        //累加完之后  给各班的这个分段 计算占比
                        //遍历班级
                        for (ShortlistedResp sr:shortlistedResps){
                            //获取分数段集合
                            List<ScorePeriod> periods = sr.getShortlisted().getScorePeriods();
                            if (periods!=null && periods.size()!=0 && periods.get(0)!=null){
                                //遍历分数段
                                for (ScorePeriod period:periods){
                                    //判断分数段名称
                                    if (sp.getPeriodName().equals(period.getPeriodName())){
                                        //计算占比
                                        //除数 这个班这个分段的人数
                                        BigDecimal bd1 = new BigDecimal(period.getPeriodNum());
                                        //被除数  总人数
                                        if (total!=0){ //0不作为被除数
                                            BigDecimal bd2 = new BigDecimal(total);
                                            //计算占比 精确到小数点后4位 四舍五入(向上取整)
                                            BigDecimal result = bd1.divide(bd2, 4, BigDecimal.ROUND_HALF_UP);
                                            //转换成百分比
                                            DecimalFormat df = new DecimalFormat("0.00%");
                                            period.setAccounted(df.format(result));
                                        }else {
                                            period.setAccounted("0.00%");
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }



        }else {
            //查询单科

            //查询各班单学科每个人的成绩
            List<ShortlistedScore> subScore = this.scorePeriodMapper.findScores(pager);
            //遍历查询出来的分数信息
            if(subScore!=null && subScore.size()!=0 && subScore.get(0)!=null){
                //每个ss就是一个班
                for (ShortlistedScore ss:subScore){
                    //创建班级对象
                    ShortlistedResp shortlistedResp = new ShortlistedResp();
                    //设置班级id
                    shortlistedResp.setClassId(ss.getClassId());
                    //设置班号
                    shortlistedResp.setClassNumber(ss.getClassNumber());

                    //给班级设置分数段
                    Shortlisted shortlisted = new Shortlisted();
                    shortlistedResp.setShortlisted(shortlisted);
                    //获取单科成绩
                    List<SubScore> subScore1 = ss.getSubScores();
                    if (subScore1 != null && subScore1.size() != 0 && subScore.get(0) != null) {
                        //设置课程名称
                        shortlisted.setSubjectName(subScore1.get(0).getSubName());
                    }
                    shortlisted.setScorePeriods(new ArrayList<>());
                    //遍历前端输入分数段
                    if (scores!=null && scores.size()!=0 && scores.get(0)!=null) {
                        for (int i=0;i<scores.size()-1;i++){
                            //创建分数段对象
                            ScorePeriod scorePeriod = new ScorePeriod();
                            //设置最大分数
                            BigDecimal b = new BigDecimal(String.valueOf(scores.get(i)));
                            scorePeriod.setMaxScore(b);
                            //设置最小分数
                            BigDecimal b1 = new BigDecimal(String.valueOf(scores.get(i+1)));
                            scorePeriod.setMinScore(b1);
                            //设置人数默认为0
                            scorePeriod.setPeriodNum(0);
                            //编辑分数段名称
                            scorePeriod.setPeriodName(scores.get(i) + "-" + scores.get(i+1));
                            //添加至集合
                            shortlisted.getScorePeriods().add(scorePeriod);
                        }
                    }

                    //遍历分数信息
                    if (subScore1 != null && subScore1.size() != 0 && subScore.get(0) != null) {
                        List<StudentScore> studentScoreList = subScore1.get(0).getScores();
                        if (studentScoreList!=null && studentScoreList.size()!=0 && studentScoreList.get(0)!=null){
                            for (StudentScore studentScore:studentScoreList){
                                //获取分数
                                BigDecimal score = studentScore.getScore();
                                //获取分数段集合
                                List<ScorePeriod> scorePeriods = shortlistedResp.getShortlisted().getScorePeriods();
                                //遍历分数段集合
                                if (scorePeriods!=null && scorePeriods.size()!=0 && scorePeriods.get(0)!=null){
                                    //判断分数是否等于最高分段分数
                                    //取出最高分段分数
                                    BigDecimal max = scorePeriods.get(0).getMaxScore();
                                    //比较
                                    int res = score.compareTo(max);
                                    if (res==0){//相等 则属于这个分数段
                                        //人数累加
                                        scorePeriods.get(0).setPeriodNum(scorePeriods.get(0).getPeriodNum()+1);
                                    }else {
                                        for (ScorePeriod sp:scorePeriods){
                                            //取出最大分数
                                            BigDecimal maxScore = sp.getMaxScore();
                                            //取出最小分数
                                            BigDecimal minScore = sp.getMinScore();
                                            //分数和最大分数比较
                                            int a = score.compareTo(maxScore);
                                            if (a==-1){
                                                //分数小于最大分数
                                                //再判断分数是否大于等于最小分数
                                                int a1 = score.compareTo(minScore);
                                                if (a1==0 || a1==1){
                                                    //分数 小于 最大分数 且 大于等于 最小分数  就认定为属于这个分数段
                                                    // 人数+1
                                                    sp.setPeriodNum(sp.getPeriodNum()+1);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //把对象添加至返回集合中
                    shortlistedResps.add(shortlistedResp);
                }
            }

            //计算总分占比

            //遍历有几个分数段
            //遍历有几个分数段
            if (shortlistedResps != null && shortlistedResps.size() != 0 && shortlistedResps.get(0) != null) {
                //获取第一条信息  遍历分数段
                List<ScorePeriod> scorePeriods = shortlistedResps.get(0).getShortlisted().getScorePeriods();
                if (scorePeriods!=null && scorePeriods.size()!=0 && scorePeriods.get(0)!=null){
                    for (ScorePeriod sp:scorePeriods){
                        //声明总人数  一个分数段一个总人数  计算占比用
                        Integer total = 0;

                        //各班的这个分段 人数 累加
                        //遍历班级
                        for (ShortlistedResp sr:shortlistedResps){
                            //获取分数段集合
                            List<ScorePeriod> periods = sr.getShortlisted().getScorePeriods();
                            if (periods!=null && periods.size()!=0 && periods.get(0)!=null){
                                //遍历分数段
                                for (ScorePeriod period:periods){
                                    //判断分数段名称
                                    if (sp.getPeriodName().equals(period.getPeriodName())){
                                        //同一个分数段  总人数累加
                                        total += period.getPeriodNum();
                                        break;
                                    }
                                }
                            }
                        }
                        //累加完之后  给各班的这个分段 计算占比
                        //遍历班级
                        for (ShortlistedResp sr:shortlistedResps){
                            //获取分数段集合
                            List<ScorePeriod> periods = sr.getShortlisted().getScorePeriods();
                            if (periods!=null && periods.size()!=0 && periods.get(0)!=null){
                                //遍历分数段
                                for (ScorePeriod period:periods){
                                    //判断分数段名称
                                    if (sp.getPeriodName().equals(period.getPeriodName())){
                                        //计算占比
                                        //除数 这个班这个分段的人数
                                        BigDecimal bd1 = new BigDecimal(period.getPeriodNum());
                                        //被除数  总人数
                                        if (total!=0){ //0不作为除数
                                            BigDecimal bd2 = new BigDecimal(total);
                                            //计算占比 精确到小数点后4位 四舍五入(向上取整)
                                            BigDecimal result = bd1.divide(bd2, 4, BigDecimal.ROUND_HALF_UP);
                                            //转换成百分比
                                            DecimalFormat df = new DecimalFormat("0.00%");
                                            period.setAccounted(df.format(result));
                                        }else {
                                            period.setAccounted("0.00%");
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
            }


        //计算当前页的班级id集合
        pager = this.classIdPager(pager);
        //取出分页后的班级id集合
        List<Long> classIds = (List<Long>) pager.getParameters().get("classIdList");

        //遍历班级
        if (shortlistedResps != null && shortlistedResps.size()!=0 && shortlistedResps.get(0)!= null) {
            for (int k = 0; k < shortlistedResps.size();k++) {
                //过滤最后一条年级信息
                if (shortlistedResps.get(k).getClassId()!=null){
                    //判断表示
                    boolean isClassId = false;
                    //遍历分页后班级id
                    if (classIds!=null && classIds.size()!=0){
                        for (int i = 0;i<classIds.size();i++){
                            if (classIds.get(i)!=null){
                                String classId1 = String.valueOf(classIds.get(i));
                                String classId2 = String.valueOf(shortlistedResps.get(k).getClassId());
                                if (classId2!=null){
                                    if (classId2.equals(classId1)){
                                        isClassId = true;
                                    }
                                }
                            }
                        }
                    }
                    //如果为false 则不需要展示这个班级
                    if (!isClassId){
                        shortlistedResps.set(k,null);
                    }
                }
            }
        }
        //按照前端传入的班级排序
        List<ShortlistedResp> newShortlistedResps = new ArrayList<>();
        for (Object c:classIds){
            if (c!=null){
                String classId= String.valueOf(c);
                if (shortlistedResps!=null && shortlistedResps.size()!=0){
                    for (ShortlistedResp sr:shortlistedResps){
                        if (sr!=null && sr.getClassId()!=null){
                            if (sr.getClassId().equals(Long.valueOf(classId))){
                                newShortlistedResps.add(sr);
                            }
                        }
                    }
                }
            }
        }


        pager.setContent(newShortlistedResps);
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
     * 导出
     * @param response
     * @param p
     * @param request
     * @return
     */
    @Override
    public ApiResult scorePeriodExport(HttpServletResponse response, Pager pager, HttpServletRequest request) {
        //获取数据
        ApiResult apiResult = findScorePeriod(pager);
        Pager p = (Pager) apiResult.getData();
        List<ShortlistedResp> list = p.getContent();



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
                        if (info.getClassNumber()==null){
                            stringList.add("");
                        }else {
                            stringList.add(String.valueOf(info.getClassNumber()));
                        }

                        Shortlisted shortlisted = info.getShortlisted();
                        //设置课程
                        if (shortlisted!=null && shortlisted.getSubjectName()!=null){
                            stringList.add(shortlisted.getSubjectName());
                        }else {
                            stringList.add("");
                        }

                        //获取分段信息
                        if (shortlisted!=null){
                            List<ScorePeriod> scorePeriods = shortlisted.getScorePeriods();
                            if (scorePeriods!=null && scorePeriods.size()!=0){
                                for (ScorePeriod sp:scorePeriods){
                                    if (sp!=null){
                                        //设置数量
                                        if (sp.getPeriodNum()==null){
                                            stringList.add("");
                                        }else {
                                            stringList.add(String.valueOf(sp.getPeriodNum()));
                                        }
                                        //设置占比数
                                        if (sp.getAccounted()==null){
                                            stringList.add("");
                                        }else {
                                            stringList.add(sp.getAccounted());
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
            String[] excelHeader0 = { "分数分段统计" };
            String[] headnum0 = { "0,0,0,"+ (dataHandler.get(0).size()-1)};

            //第二行
            //表头第二行
            List<String> excle1 = new ArrayList<>();
            excle1.add("       班号       ");
            excle1.add("课程");
            List<String> num1 = new ArrayList<>();
            num1.add("1,1,0,0");
            num1.add("1,1,1,1");

            //获取list中第一条不为空的数据
            if (list!=null && list.size()!=0){
                for (ShortlistedResp sr:list){
                    if (sr!=null){
                        //获取分段集合
                        Shortlisted shortlisted = sr.getShortlisted();
                        if (shortlisted!=null){
                            List<ScorePeriod> scorePeriods = shortlisted.getScorePeriods();
                            //遍历
                            if (scorePeriods!=null && scorePeriods.size()!=0){
                                int i = 0;
                                for (ScorePeriod sp:scorePeriods){
                                    //设置表头分数段
                                    excle1.add(sp.getPeriodName());
                                    //位置
                                    int s = 2 + i;
                                    num1.add("1,1," + s + "," + s);
                                    //设置年级占比
                                    excle1.add("年级占比");
                                    num1.add("1,1," + ++s + "," + s);
                                    i += 2;
                                }
                            }
                        }
                        break;
                    }
                }
            }

            String[] excelHeader1 = excle1.toArray(new String[0]);
            String[] headnum1 = num1.toArray(new String[0]);


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

            headers.add(header0);
            headers.add(header1);

            try {
                //导出信息

                String fileName = "分数分段统计";
                exportExcelUtil.exportExcel2(wb,0,"分数分段统计",style2,headers,dataHandler,response,fileName);
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
