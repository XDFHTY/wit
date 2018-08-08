package com.cj.witscorefind.service.Impl;

import com.cj.witbasics.entity.StudentScore;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witcommon.utils.excle.exportExcelUtil;
import com.cj.witscorefind.entity.*;
import com.cj.witscorefind.mapper.AverageMapper;
import com.cj.witscorefind.mapper.ShortlistedMapper;
import com.cj.witscorefind.service.ShortlistedService;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 入围统计
 * Created by XD on 2018/6/8.
 */
@Service
public class ShortlistedServiceImpl implements ShortlistedService {

    private static final Logger log = LoggerFactory.getLogger(ShortlistedServiceImpl.class);

    @Autowired
    private ShortlistedMapper shortlistedMapper;

    @Autowired
    private AverageMapper averageMapper;

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
    /**
     * 统计各班各科年级入围信息
     *
     * @param p
     * @return
     */
    @Override
    public ApiResult findFindShortlisted(Pager pager) {
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

        //判断是否需要查询总分信息
        List<Integer> newSubjectIds = new ArrayList<>();
        boolean isTotal = false;
        List<Integer> curriculumIdList = (List<Integer>) pager.getParameters().get("curriculumIdList");
        for (Integer s:curriculumIdList){
            if (s.equals(0)){
                isTotal = true;
            }else {
                //不是0的课程id 作为查询条件
                newSubjectIds.add(s);
            }
        }
        pager.getParameters().put("newSubjectIds",newSubjectIds);

        //查询各班、各科、各班总分 入围信息
        List<ShortlistedResp> shortlistedRespList = this.shortlistedMapper.findFindShortlisted(pager);

        //给各档设置双入围分数线和任务数  （等于 总分任务数，分数线）
        //遍历各班
        if (shortlistedRespList != null && shortlistedRespList.size()!=0 && shortlistedRespList.get(0)!= null){
            for (ShortlistedResp sr : shortlistedRespList) {
                //遍历班级的各学科
                if (sr.getShortlistedList() != null && sr.getShortlistedList().size() != 0 && sr.getShortlistedList().get(0) != null) {
                    for (Shortlisted s:sr.getShortlistedList()){
                        //遍历学科的各档次
                        if(s.getGradeShorts()!=null && s.getGradeShorts().size()!=0 && s.getGradeShorts().get(0)!=null){
                            for (GradeShort g:s.getGradeShorts()){
                                //遍历总分集合
                                if (sr.getTotalList()!=null && sr.getTotalList().size()!=0 && sr.getTotalList().get(0)!=null){
                                    for (GradeShort g2:sr.getTotalList()){
                                        //如果档次名称一样 就设置双入围分数线和双入围任务数
                                        if (g.getGradeName().equals(g2.getGradeName())){
                                            g.setSTask(g2.getTask());
                                            g.setSNumScore(g2.getTotalScore());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //查询各班各学科每个人的成绩
        List<ShortlistedScore> scores = this.shortlistedMapper.findScores(pager);
        //查询各班每个人的总分
        List<ShortlistedScore> totalScores = this.shortlistedMapper.findTotalScores(pager);


        //计算总分各档入围人数  差异  贡献率

        //遍历各班
        if (shortlistedRespList != null && shortlistedRespList.size()!=0 && shortlistedRespList.get(0)!= null){
            for (ShortlistedResp sr : shortlistedRespList){
                //遍历总分各档
                if(sr.getTotalList() != null && sr.getTotalList().size()!=0 && sr.getTotalList().get(0)!=null){
                    for (GradeShort gs:sr.getTotalList()){
                        //获取该档分数线
                        BigDecimal shortScore = gs.getTotalScore();
                        //入围人数
                        Integer i = 0;
                        //遍历各班总分
                        if (totalScores != null && totalScores.size()!=0 && totalScores.get(0)!=null){
                            for (ShortlistedScore ss:totalScores){
                                //判断班级
                                if (ss.getClassId().equals(sr.getClassId())){
                                    //遍历分数
                                    if (ss.getScores()!=null && ss.getScores().size()!=0 && ss.getScores().get(0)!=null){
                                     for (StudentScore s:ss.getScores()){
                                         //获取总分
                                         BigDecimal score = s.getScore();
                                         //判断  总分(score) 如果 大于 该档分数线(shortScore) i++
                                         int result = score.compareTo(shortScore);
                                         if (result==0 || result ==1){
                                             i++;
                                         }
                                     }
                                    }
                                }
                            }
                        }
                        //设置该档次的总分入围人数
                        gs.setShortNum(i);
                    }
                    //处理入围人数  精确计算每个档次区间的人数
                    for(int j=sr.getTotalList().size()-1; j > 0;j--){
                        //取出后一个档次入围人数
                        Integer hNum = sr.getTotalList().get(j).getShortNum();
                        //取出前一个档次入围人数
                        Integer qNum = sr.getTotalList().get(j-1).getShortNum();
                        //入围人数
                        Integer num = hNum - qNum;
                        sr.getTotalList().get(j).setShortNum(num);
                        //计算 总分入围差异  入围人数 - 入围任务数
                        BigDecimal bNum = new BigDecimal(num);
                        //任务数
                        BigDecimal task = sr.getTotalList().get(j).getTask();
                        sr.getTotalList().get(j).setZfDifferences(bNum.subtract(task));
                    }
                    //处理第一条档次的 总分入围差异
                    GradeShort gs = sr.getTotalList().get(0);
                    //获取入围人数
                    BigDecimal num = new BigDecimal(gs.getShortNum());
                    //计算差异
                    gs.setZfDifferences(num.subtract(gs.getTask()));
                }

            }
        }




        //比较单科入围，如果成绩大于等于入围分数线，则入围人数 +1
        //遍历各班
        if (shortlistedRespList != null && shortlistedRespList.size()!=0 && shortlistedRespList.get(0)!= null) {
            for (ShortlistedResp sr : shortlistedRespList) {
                 //遍历学科
                if (sr.getShortlistedList() != null && sr.getShortlistedList().size() != 0 && sr.getShortlistedList().get(0) != null){
                    for (Shortlisted s:sr.getShortlistedList()){
                        //遍历档次
                        if(s.getGradeShorts()!=null && s.getGradeShorts().size()!=0 && s.getGradeShorts().get(0)!=null){
                            for (GradeShort g:s.getGradeShorts()){
                                //取出该档次的单入围分数线
                                BigDecimal dNumScore = g.getDNumScore();
                                //取出该档次的双入围分数线
                                BigDecimal sNumScore = g.getSNumScore();
                                //单科入围人数
                                Integer i = 0;
                                //单科双入围人数
                                Integer k = 0;
                                //遍历分数集合
                                if (scores != null && scores.size()!=0 && scores.get(0)!=null){
                                    for (ShortlistedScore ss:scores){
                                        //判断班级
                                        if (ss.getClassId().equals(sr.getClassId())){
                                            //遍历学科
                                            if (ss.getSubScores() != null && ss.getSubScores().size()!=0 && ss.getSubScores().get(0)!=null){
                                                for (SubScore subScore:ss.getSubScores()){
                                                    //判断是否学科相等
                                                    if (subScore.getSubjectId().equals(s.getSubjectId())){
                                                        //遍历分数集合
                                                        if (subScore.getScores()!=null && subScore.getScores().size()!=0 && subScore.getScores().get(0)!=null){
                                                            for (StudentScore stuScore:subScore.getScores()){
                                                                //获取成绩
                                                                BigDecimal score = stuScore.getScore();
                                                                //成绩和入围分数线比较
                                                                int result = score.compareTo(dNumScore);
                                                                //分数大于或等于 入围分数线时， 计数
                                                                if (result==0 || result ==1){
                                                                    i++;
                                                                    //遍历总分  找到这个学生的总分  判断是否大于 双入围分数线，如果大于 k++
                                                                    if (totalScores!=null && totalScores.size()!=0 && totalScores.get(0)!=null){
                                                                        for (ShortlistedScore ss1:totalScores){
                                                                            List<StudentScore> ss2 = ss1.getScores();
                                                                            if (ss2!=null && ss2.size()!=0 && ss2.get(0)!=null){
                                                                                //遍历每个学生
                                                                                for (StudentScore stu:ss2){
                                                                                    if (stu.getRegisterNumber().equals(stuScore.getRegisterNumber())){
                                                                                        //获取总分
                                                                                        BigDecimal total = stu.getScore();
                                                                                        //比较
                                                                                        if (sNumScore!=null){
                                                                                            int jg = total.compareTo(sNumScore);
                                                                                            if (jg == 0 || jg == 1){
                                                                                                k++;
                                                                                            }
                                                                                        }
                                                                                        break;
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
                                            }
                                        }
                                    }
                                }
                                //设置该档次的单科入围人数
                                g.setDShortNum(i);
                                //设置该档次的双入围人数
                                g.setSShortNum(k);
                            }
                        }
                        //处理单双入围人数  精确计算每个档次区间的人数
                        if (s.getGradeShorts() != null && s.getGradeShorts().size() !=0 && s.getGradeShorts().get(0)!=null){
                            for(int j=s.getGradeShorts().size()-1; j > 0;j--){
                                //取出后一个档次单入围人数
                                Integer hNum = s.getGradeShorts().get(j).getDShortNum();
                                //取出前一个档次单入围人数
                                Integer qNum = s.getGradeShorts().get(j-1).getDShortNum();
                                //单入围人数
                                Integer num = hNum - qNum;
                                s.getGradeShorts().get(j).setDShortNum(num);
                                //计算 单入围差异  入围人数 - 入围任务数
                                BigDecimal bNum = new BigDecimal(num);
                                //任务数
                                BigDecimal task = s.getGradeShorts().get(j).getDTask();
                                s.getGradeShorts().get(j).setDDifferences(bNum.subtract(task));

                                //处理双入围人数
                                //取出后一个档次双入围人数
                                Integer shNum = s.getGradeShorts().get(j).getSShortNum();
                                //取出前一个档次双入围人数
                                Integer sqNum = s.getGradeShorts().get(j-1).getSShortNum();
                                //单入围人数
                                Integer snum = shNum - sqNum;
                                s.getGradeShorts().get(j).setSShortNum(snum);
                                //计算 双入围差异  入围人数 - 入围任务数
                                BigDecimal sbNum = new BigDecimal(snum);
                                //任务数
                                BigDecimal stask = s.getGradeShorts().get(j).getSTask();
                                if (stask!=null){
                                    s.getGradeShorts().get(j).setSDifferences(sbNum.subtract(stask));
                                }

                            }
                            //处理第一条档次的 单双入围差异
                            GradeShort gs = s.getGradeShorts().get(0);
                            //获取单入围人数
                            BigDecimal num = new BigDecimal(gs.getDShortNum());
                            //计算差异
                            gs.setDDifferences(num.subtract(gs.getDTask()));

                            //获取双入围人数
                            BigDecimal snum = new BigDecimal(gs.getSShortNum());
                            //计算差异
                            if (gs.getSTask()!=null){
                                gs.setSDifferences(snum.subtract(gs.getSTask()));
                            }
                        }


                    }
                }
            }
        }


        //计算年级入围统计
        ShortlistedResp gradeSR = new ShortlistedResp();
        gradeSR.setShortlistedList(new ArrayList<>());




        //创建年级总分对象
        gradeSR.setTotalList(new ArrayList<>());

        //年级总分情况
        if (shortlistedRespList != null && shortlistedRespList.size()!=0 && shortlistedRespList.get(0)!= null){
            //设置班级类型名称
            gradeSR.setClassType(shortlistedRespList.get(0).getClassType());
            //遍历总分的档次
            List<GradeShort> list = shortlistedRespList.get(0).getTotalList();
            if (list!=null && list.size()!=0 && list.get(0)!=null) {
                for (GradeShort gs :list) {
                    //创建对象
                    GradeShort gradeGS = new GradeShort();
                    //任务数累加
                    BigDecimal task = new BigDecimal(0);
                    //入围人数累加
                    Integer num = 0;
                    //总分差异累加
                    BigDecimal differences = new BigDecimal(0);

                    //遍历各班
                    for (ShortlistedResp sr : shortlistedRespList) {
                        //遍历一个班的总分档次
                        for (GradeShort gs1:sr.getTotalList()){
                            if (gs.getGradeName().equals(gs1.getGradeName())) {
                                //任务数累加
                                task = gs1.getTask().add(task);
                                //入围人数累加
                                num += gs1.getShortNum();
                                //差异累加
                                differences = gs1.getZfDifferences().add(differences);
                                break;
                            }
                        }
                    }
                    //设置档次名称
                    gradeGS.setGradeName(gs.getGradeName());
                    //设置总分入围任务数
                    gradeGS.setTask(task);
                    //设置总分入围人数
                    gradeGS.setShortNum(num);
                    //设置总分差异
                    gradeGS.setZfDifferences(differences);

                    gradeSR.getTotalList().add(gradeGS);
                }
            }
        }

        //设置年级情况
        shortlistedRespList.add(gradeSR);



        //年级单科情况

        if (shortlistedRespList != null && shortlistedRespList.size()!=0 && shortlistedRespList.get(0)!= null){
            //获取课程集合
            List<Shortlisted> shortlisteds = shortlistedRespList.get(0).getShortlistedList();
            //遍历各个课程
            if (shortlisteds != null && shortlisteds.size()!=0 && shortlisteds.get(0)!=null){
                for (Shortlisted s:shortlisteds){
                    //创建课程对象
                    Shortlisted shortlisted = new Shortlisted();
                    shortlisted.setSubjectId(s.getSubjectId());
                    shortlisted.setSubjectName(s.getSubjectName());
                    shortlisted.setGradeShorts(new ArrayList<>());
                    //遍历课程的档次
                    List<GradeShort> list = s.getGradeShorts();
                    if (list!=null && list.size()!=0 && list.get(0)!=null){

                        for (GradeShort gs :list){
                            //创建对象
                            GradeShort gradeGS = new GradeShort();
                            //单入围任务数累加
                            BigDecimal dTask = new BigDecimal(0);
                            //单入围人数累加
                            Integer dNum = 0;
                            //单入围 差异累加
                            BigDecimal dDifferences = new BigDecimal(0);
                            //双入围任务数累加
                            BigDecimal sTask = new BigDecimal(0);
                            //双入围人数累加
                            Integer sNum = 0;
                            //双入围 差异累加
                            BigDecimal sDifferences = new BigDecimal(0);

                            //遍历各班
                            for (ShortlistedResp sr : shortlistedRespList) {
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
                                                            //单入围任务数累加
                                                            dTask = gs2.getDTask().add(dTask);
                                                            //单入围人数累加
                                                            dNum += gs2.getDShortNum();
                                                            //单入围差异累加
                                                            dDifferences = gs2.getDDifferences().add(dDifferences);

                                                            //双入围任务数累加
                                                            if (gs2.getSTask()!=null){
                                                                sTask = gs2.getSTask().add(sTask);
                                                            }
                                                            //双入围人数累加
                                                            if (gs2.getSShortNum()!=null) {
                                                                sNum += gs2.getSShortNum();
                                                            }
                                                            //双入围差异累加
                                                            if (gs2.getSDifferences()!=null){
                                                                sDifferences = gs2.getSDifferences().add(sDifferences);
                                                            }
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //设置档次名称
                            gradeGS.setGradeName(gs.getGradeName());
                            //设置单入围任务数
                            gradeGS.setDTask(dTask);
                            //设置单入围人数
                            gradeGS.setDShortNum(dNum);
                            //设置单入围差异
                            gradeGS.setDDifferences(dDifferences);

                            //设置双入围任务数
                            gradeGS.setSTask(sTask);
                            //设置双入围人数
                            gradeGS.setSShortNum(sNum);
                            //设置双入围差异
                            gradeGS.setSDifferences(sDifferences);
                            shortlisted.getGradeShorts().add(gradeGS);

                        }
                    }
                    gradeSR.getShortlistedList().add(shortlisted);
                }
            }
        }



        //计算各班总分各档次贡献率  （各班入围人数/年级入围人数）
        //获取年级总分各档入围人数
        List<GradeShort> gradeShorts = gradeSR.getTotalList();

        //遍历各班
        if (shortlistedRespList != null && shortlistedRespList.size()!=0 && shortlistedRespList.get(0)!= null){
            for (ShortlistedResp sr : shortlistedRespList){
                //判断班级id不能为空 过滤最后一条年级情况
                if (sr.getClassId() != null) {
                    //遍历总分档次
                    if (sr.getTotalList()!=null && sr.getTotalList().size()!=0 && sr.getTotalList().get(0)!=null){
                        for (GradeShort gs :sr.getTotalList()){
                            //获取该档次入围人数
                            BigDecimal bdNum = new BigDecimal(gs.getShortNum());
                            //遍历年级各档入围人数
                            if (gradeShorts!=null && gradeShorts.size()!=0 && gradeShorts.get(0)!=null){
                                for (GradeShort gs1:gradeShorts){
                                    //判断档次名称
                                    if (gs.getGradeName().equals(gs1.getGradeName())){
                                        //获取年级入围人数
                                        Integer num = gs1.getShortNum();
                                        if (!num.equals(0)){ //0不作为被除数
                                            //转换类型
                                            BigDecimal gradeNum = new BigDecimal(num);
                                            //计算贡献率  精确到小数点后4位 四舍五入(向上取整)
                                            BigDecimal gxl = bdNum.divide(gradeNum,4,BigDecimal.ROUND_HALF_UP);
                                            //转换成百分比
                                            DecimalFormat df = new DecimalFormat("0.00%");
                                            gs.setContribution(df.format(gxl));
                                        }else {
                                            gs.setContribution("0.00%");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //计算各班各科各档次贡献率
        //获取年级各科各档入围人数
        List<Shortlisted> shortlisteds = gradeSR.getShortlistedList();
        //遍历各班
        if (shortlistedRespList != null && shortlistedRespList.size()!=0 && shortlistedRespList.get(0)!= null){
            for (ShortlistedResp sr : shortlistedRespList){
                //判断班级id不能为空 过滤最后一条年级情况
                if (sr.getClassId() != null) {
                    //遍历各科
                    List<Shortlisted> list = sr.getShortlistedList();
                    if (list!=null &&  list.size()!=0 && list.get(0)!=null){
                        for (Shortlisted s:list){
                            //遍历各档
                            List<GradeShort> gradeShortList = s.getGradeShorts();
                            if (gradeShortList!=null && gradeShortList.size()!=0 && gradeShortList.get(0)!=null){
                                for (GradeShort gs:gradeShortList){
                                    //获取这个班这个科目这个档次的双入围人数
                                    BigDecimal num = new BigDecimal(gs.getSShortNum());
                                    //遍历年级各科情况
                                    if (shortlisteds!=null && shortlisteds.size()!=0 && shortlisteds.get(0)!=null){
                                        for (Shortlisted shortlisted:shortlisteds){
                                            //判断课程id
                                            if (s.getSubjectId().equals(shortlisted.getSubjectId())){
                                                //相同课程下 遍历课程各档
                                                List<GradeShort> list1 = shortlisted.getGradeShorts();
                                                if (list1!=null && list1.size()!=0 && list1.get(0)!=null){
                                                    for (GradeShort gradeShort:list1){
                                                        //如果档次名称相等
                                                        if (gs.getGradeName().equals(gradeShort.getGradeName())){
                                                            //相等 则计算
                                                            //获取年级的这个科目这个档次的入围人数
                                                            Integer gNum = gradeShort.getSShortNum();
                                                            if (!gNum.equals(0)){//0不作为除数
                                                                //转换类型
                                                                BigDecimal gradeNum = new BigDecimal(gNum);
                                                                //计算贡献率 班级入围人数/年级入围人数 精确到小数点后4位 四舍五入(向上取整)
                                                                BigDecimal reult = num.divide(gradeNum, 4, BigDecimal.ROUND_HALF_UP);
                                                                //转换为百分比
                                                                DecimalFormat df = new DecimalFormat("0.00%");
                                                                gs.setContribution(df.format(reult));
                                                            }else {
                                                                gs.setContribution("0.00%");
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
                    }
                }
            }
        }


        //同层次排名

        //获取所有的层次id
        List<Integer> classLevelIdList = this.shortlistedMapper.findClassLevelIdList(pager);

        //各班总分各档排名

        //遍历有几个档次
        List<GradeShort> gsList = shortlistedRespList.get(0).getTotalList();
        if (gsList!=null && gsList.size()!=0 && gsList.get(0)!=null){
            for (GradeShort gradeShort:gsList){
                //遍历层次
                if (classLevelIdList!=null && classLevelIdList.size()!=0 && classLevelIdList.get(0)!=null){
                    for (Integer classLeveId:classLevelIdList){
                        //创建新的排名集合
                        List<GradeShort> list = new ArrayList<>();
                        //遍历各班
                        if (shortlistedRespList != null && shortlistedRespList.size()!=0 && shortlistedRespList.get(0)!= null){
                            for (ShortlistedResp sr : shortlistedRespList){
                                //判断班级id不能为空 过滤最后一条年级情况
                                if (sr.getClassId() != null) {
                                    //过滤掉层次为空的情况
                                    if (sr.getClassLevel()!=null){
                                        //判断班级层次是否相同
                                        if (classLeveId.equals(sr.getClassLevelId())){
                                            //遍历各班总分各档
                                            List<GradeShort> gradeShortList = sr.getTotalList();
                                            if (gradeShortList!=null && gradeShortList.size()!=0 && gradeShortList.get(0)!=null){
                                                for (GradeShort gs:gradeShortList){
                                                    //判断档次是否相同
                                                    if (gradeShort.getGradeName().equals(gs.getGradeName())){
                                                        //相同就取出这个对象
                                                        list.add(gs);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //同层次排名  按总分差异数 降序
                        if (list!=null && list.size()!=0 && list.get(0)!=null){
                            Collections.sort(list, new GradeShort());
                            for (int i = list.size();i > 0; i--){
                                list.get(i-1).setZfRank(i);
                            }
                        }
                    }
                }
            }
        }

        //各班各科各档双入围排名
        //遍历有几个课程
        List<Shortlisted> sList = shortlistedRespList.get(0).getShortlistedList();
        if (sList!=null && sList.size()!=0 && sList.get(0)!=null){
            for (Shortlisted s:sList){
                //遍历有几个档次
                List<GradeShort> gradeShortList = s.getGradeShorts();
                if (gradeShortList!=null && gradeShortList.size()!=0 && gradeShortList.get(0)!=null){
                    for (GradeShort gs:gradeShortList){
                        //遍历层次
                        if (classLevelIdList!=null && classLevelIdList.size()!=0 && classLevelIdList.get(0)!=null){
                            for (Integer classLeveId:classLevelIdList){
                                //创建新的排名集合
                                List<GradeShort> list = new ArrayList<>();
                                //遍历各班
                                if (shortlistedRespList != null && shortlistedRespList.size()!=0 && shortlistedRespList.get(0)!= null){
                                    for (ShortlistedResp sr : shortlistedRespList){
                                        //判断班级id不能为空 过滤最后一条年级情况
                                        if (sr.getClassId() != null) {
                                            //过滤掉层次为空的情况
                                            if (sr.getClassLevel()!=null){
                                                //判断班级层次是否相同
                                                if (classLeveId.equals(sr.getClassLevelId())){
                                                    //遍历这个班 总分各科
                                                    List<Shortlisted> shortlisteds1 = sr.getShortlistedList();
                                                    if (shortlisteds1 != null && shortlisteds1.size() != 0 && shortlisteds1.get(0) != null) {
                                                        for (Shortlisted s1 : shortlisteds1) {
                                                            //判断课程名称是否相同
                                                            if (s1.getSubjectId().equals(s.getSubjectId())){
                                                                //遍历这个课程的各个档次
                                                                List<GradeShort> gsList1 = s1.getGradeShorts();
                                                                if (gsList1 != null && gsList1.size() != 0 && gsList1.get(0) != null) {
                                                                    for (GradeShort gs1:gsList1){
                                                                        //判断档次是否相同
                                                                        if (gs.getGradeName().equals(gs1.getGradeName())) {
                                                                            //把双入围差异数  赋给 总分差异数  用于排名
                                                                            gs1.setZfDifferences(gs1.getSDifferences());
                                                                            //相同就取出这个对象
                                                                            list.add(gs1);
                                                                            break;
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
                                }
                                //同层次排名  按总分差异数 降序
                                if (list!=null && list.size()!=0 && list.get(0)!=null){
                                    Collections.sort(list, new GradeShort());
                                    for (int i = list.size();i > 0; i--){
                                        list.get(i-1).setSRank(i);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //各班各科各档单入围排名
        //遍历有几个课程
        List<Shortlisted> dList = shortlistedRespList.get(0).getShortlistedList();
        if (dList!=null && dList.size()!=0 && dList.get(0)!=null){
            for (Shortlisted s:dList){
                //遍历有几个档次
                List<GradeShort> gradeShortList = s.getGradeShorts();
                if (gradeShortList!=null && gradeShortList.size()!=0 && gradeShortList.get(0)!=null){
                    for (GradeShort gs:gradeShortList){
                        //遍历层次
                        if (classLevelIdList!=null && classLevelIdList.size()!=0 && classLevelIdList.get(0)!=null){
                            for (Integer classLeveId:classLevelIdList){
                                //创建新的排名集合
                                List<GradeShort> list = new ArrayList<>();
                                //遍历各班
                                if (shortlistedRespList != null && shortlistedRespList.size()!=0 && shortlistedRespList.get(0)!= null){
                                    for (ShortlistedResp sr : shortlistedRespList){
                                        //判断班级id不能为空 过滤最后一条年级情况
                                        if (sr.getClassId() != null) {
                                            //过滤掉层次为空的情况
                                            if (sr.getClassLevel()!=null){
                                                //判断班级层次是否相同
                                                if (classLeveId.equals(sr.getClassLevelId())){
                                                    //遍历这个班 总分各科
                                                    List<Shortlisted> shortlisteds1 = sr.getShortlistedList();
                                                    if (shortlisteds1 != null && shortlisteds1.size() != 0 && shortlisteds1.get(0) != null) {
                                                        for (Shortlisted s1 : shortlisteds1) {
                                                            //判断课程名称是否相同
                                                            if (s1.getSubjectId().equals(s.getSubjectId())){
                                                                //遍历这个课程的各个档次
                                                                List<GradeShort> gsList1 = s1.getGradeShorts();
                                                                if (gsList1 != null && gsList1.size() != 0 && gsList1.get(0) != null) {
                                                                    for (GradeShort gs1:gsList1){
                                                                        //判断档次是否相同
                                                                        if (gs.getGradeName().equals(gs1.getGradeName())) {
                                                                            //把单入围差异数  赋给 总分差异数  用于排名
                                                                            gs1.setZfDifferences(gs1.getDDifferences());
                                                                            //相同就取出这个对象
                                                                            list.add(gs1);
                                                                            break;
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
                                }
                                //同层次排名  按总分差异数 降序
                                if (list!=null && list.size()!=0 && list.get(0)!=null){
                                    Collections.sort(list, new GradeShort());
                                    for (int i = list.size();i > 0; i--){
                                        list.get(i-1).setDRank(i);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        //移除不需要展示的班级


        //判断是否要查总分   不查则设置为空
        if (!isTotal){
            //不需要查询总分， 总分信息设置为空
            //遍历各班 和最后一条年级情况
            if (shortlistedRespList != null && shortlistedRespList.size()!=0 && shortlistedRespList.get(0)!= null){
                for (ShortlistedResp sr : shortlistedRespList){
                    sr.setTotalList(null);
                }
            }
        }else {
            //如果只查询总分，就把单科的信息设置为null
            if (newSubjectIds.size()==0){
                if (shortlistedRespList != null && shortlistedRespList.size()!=0 && shortlistedRespList.get(0)!= null){
                    for (ShortlistedResp sr : shortlistedRespList){
                       sr.getShortlistedList().clear();
                    }
                }
            }
        }

        //计算当前页的班级id集合
        pager = this.classIdPager(pager);
        //取出分页后的班级id集合
        List<Long> classIds = (List<Long>) pager.getParameters().get("classIdList");

        //遍历班级
        if (shortlistedRespList != null && shortlistedRespList.size()!=0 && shortlistedRespList.get(0)!= null) {
            for (int k = 0; k < shortlistedRespList.size();k++) {
                //过滤最后一条年级信息
                if (shortlistedRespList.get(k).getClassId()!=null){
                    //判断表示
                    boolean isClassId = false;
                    //遍历分页后班级id
                    if (classIds!=null && classIds.size()!=0){
                        for (int i = 0;i<classIds.size();i++){
                            if (classIds.get(i)!=null){
                                String classId1 = String.valueOf(classIds.get(i));
                                String classId2 = String.valueOf(shortlistedRespList.get(k).getClassId());
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
                        shortlistedRespList.set(k,null);
                    }
                }
            }
        }

        //按照前端传入的班级排序
        List<ShortlistedResp> newShortlistedRespList = new ArrayList<>();
        for (Object c:classIds){
            if (c!=null){
                String classId= String.valueOf(c);
                if (shortlistedRespList!=null && shortlistedRespList.size()!=0){
                    for (ShortlistedResp sr:shortlistedRespList){
                        if (sr!=null && sr.getClassId()!=null){
                            if (sr.getClassId().equals(Long.valueOf(classId))){
                                newShortlistedRespList.add(sr);
                            }
                        }
                    }
                }
            }
        }
        //传入年级信息
        newShortlistedRespList.add(shortlistedRespList.get(shortlistedRespList.size()-1));

        pager.setContent(newShortlistedRespList);
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
    public ApiResult shortlistedExport(HttpServletResponse response, Pager pager, HttpServletRequest request) {
        //查询数据
        ApiResult apiResult = findFindShortlisted(pager);
        Pager p = (Pager) apiResult.getData();
        List<ShortlistedResp> list = p.getContent();

        //判断是否选择总分
        boolean isZf = false;
        List<Integer> zfList = (List<Integer>) p.getParameters().get("curriculumIdList");
        for (Integer curriculumId:zfList){
            if (curriculumId==0){
                isZf = true;
                break;
            }
        }
        //总分档次列表
        Set totalSet = new LinkedHashSet();
        if (isZf){
            //获取每个班级的总分档次（并集）
            //遍历每个班级
            for(ShortlistedResp info1 : list){
                if (info1!=null){
                    //获取总分档次列表
                    List<GradeShort> gradeShorts = info1.getTotalList();
                    if (gradeShorts!=null && gradeShorts.size()!=0 && gradeShorts.get(0)!=null){
                        for (GradeShort gs:gradeShorts){
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
                        //设置班级
                        if (info.getClassName()==null){
                            stringList.add("");
                        }else {
                            stringList.add(info.getClassName());
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
                                    //遍历该班级的档次列表
                                    List<GradeShort> gradeShorts = info.getTotalList();
                                    if (gradeShorts!=null && gradeShorts.size()!=0 && gradeShorts.get(0)!=null){
                                        for (GradeShort gs:gradeShorts){
                                            if (str.equals(gs.getGradeName())){
                                                //档次名称匹配 正常展示
                                                isdc = true;
                                                if (gs.getTask()==null){
                                                    stringList.add("");
                                                }else {
                                                    stringList.add(String.valueOf(gs.getTask()));
                                                }
                                                if (gs.getShortNum()==null){
                                                    stringList.add("");
                                                }else {
                                                    stringList.add(String.valueOf(gs.getShortNum()));
                                                }
                                                if (gs.getZfDifferences()==null){
                                                    stringList.add("");
                                                }else {
                                                    stringList.add(String.valueOf(gs.getZfDifferences()));
                                                }
                                                if (gs.getZfRank()==null){
                                                    stringList.add("");
                                                }else {
                                                    stringList.add(String.valueOf(gs.getZfRank()));
                                                }
                                                if (gs.getContribution()==null){
                                                    stringList.add("");
                                                }else {
                                                    stringList.add(String.valueOf(gs.getContribution()));
                                                }

                                            }
                                        }
                                        if (isdc==false){
                                            //档次不匹配  显示空字符串
                                            stringList.add("");
                                            stringList.add("");
                                            stringList.add("");
                                            stringList.add("");
                                            stringList.add("");
                                        }
                                    }
                                }
                            }
                        }

                        //各科参数设置
                        // List<AverageRsp> averageRsps = info.getAverageRspList();
                        //单科 考虑课程、档次匹配问题

                        //获取课程信息
                        List<Shortlisted> shortlisteds = info.getShortlistedList();
                        //未考试科目 设置为空字符串
                        boolean isKs = false;
                        //遍历课程id集合
                        List<Integer> subjectIds = (List<Integer>) p.getParameters().get("curriculumIdList");
                        if (subjectIds!=null && subjectIds.size()!=0 && subjectIds.get(0)!=null){

                            //获取该课程档次的并集  1班 ABC   2班 ABCD 并集 ABCD
                           /* Set subjectSet = new LinkedHashSet();
                            for(ShortlistedResp info1 : list){
                                if (info1!=null){
                                    //获取总分档次列表
                                    List<Shortlisted> shortlisteds1 = info1.getShortlistedList();
                                    if (shortlisteds1!=null && shortlisteds1.size()!=0 && shortlisteds1.get(0)!=null){
                                        for (Shortlisted s:shortlisteds1){

                                            List<GradeShort> gradeShorts = s.getGradeShorts();
                                            if (gradeShorts!=null && gradeShorts.size()!=0 && gradeShorts.get(0)!=null){
                                                for (GradeShort gs:gradeShorts){
                                                    subjectSet.add(gs.getGradeName());
                                                }
                                            }
                                        }
                                    }
                                }
                            }*/


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
                                        if (Long.valueOf(subjectId).equals(s.getSubjectId()) && subjectId!=0){
                                            isKs = true;
                                            //设置教师名称
                                            if (s.getFullName()==null){
                                                stringList.add("");
                                            }else {
                                                stringList.add(s.getFullName());
                                            }
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

                                                                //设置单入围人数
                                                                if (gs.getDShortNum()==null){
                                                                    stringList.add("");
                                                                }else {
                                                                    stringList.add(String.valueOf(gs.getDShortNum()));
                                                                }

                                                                //设置单入围任务数
                                                                if (gs.getDTask()==null){
                                                                    stringList.add("");
                                                                }else {
                                                                    stringList.add(String.valueOf(gs.getDTask()));
                                                                }

                                                                //设置单入围排名
                                                                if (gs.getDRank()==null){
                                                                    stringList.add("");
                                                                }else {
                                                                    stringList.add(String.valueOf(gs.getDRank()));
                                                                }

                                                                //设置双入围人数
                                                                if (gs.getSShortNum()==null){
                                                                    stringList.add("");
                                                                }else {
                                                                    stringList.add(String.valueOf(gs.getSShortNum()));
                                                                }

                                                                //设置双入围任务数
                                                                if (gs.getSTask()==null){
                                                                    stringList.add("");
                                                                }else {
                                                                    stringList.add(String.valueOf(gs.getSTask()));
                                                                }

                                                                //设置双入围排名
                                                                if (gs.getSRank()==null){
                                                                    stringList.add("");
                                                                }else {
                                                                    stringList.add(String.valueOf(gs.getSRank()));
                                                                }

                                                                //设置贡献率
                                                                if (gs.getContribution()==null){
                                                                    stringList.add("");
                                                                }else {
                                                                    stringList.add(String.valueOf(gs.getContribution()));
                                                                }
                                                            }
                                                        }
                                                        if (isdc==false){
                                                            //无档次匹配  显示为空
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
                                }
                            }
                        }
                        dataHandler.add(stringList);
                    }
                }
            }

            //设置表头
            String[] excelHeader0 = { "入围统计" };
            String[] headnum0 = { "0,0,0,"+ (dataHandler.get(0).size()-1)};

            //表头第二行
            List<String> excle1 = new ArrayList<>();
            excle1.add("班号");
            excle1.add("班级");
            excle1.add("班级类型");
            excle1.add("班级层次");
            excle1.add("班主任");
            List<String> num1 = new ArrayList<>();
            num1.add("1,3,0,0");
            num1.add("1,3,1,1");
            num1.add("1,3,2,2");
            num1.add("1,3,3,3");
            num1.add("1,3,4,4");
            if (isZf){//需要展示总分
                excle1.add("总分");
                //空格站位数 = 5*档次个数 - 1
                int n = (5*totalSet.size())-1;
                for (int i=1;i<=n;i++){
                    excle1.add("");
                }
                int s= 5 + n;
                num1.add("1,1,5,"+ s);


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
                            //添加占位符： 7 * 并集档次个数
                            int n1 = 7 * subjectSet.size();
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
                            //添加占位符： 7 * 并集档次个数
                            int n1 = 7 * subjectSet.size();
                            n3 += n1;
                            for (int i1=1;i1<=n1;i1++){
                                excle1.add("");
                            }
                            //合并单元格的起始坐标： 总分的站位 + 当前学科站位 * 学科数
                            int s1=  5 + n3-n1 + i;
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
            excle2.add("");
            List<String> num2 = new ArrayList<>();
            num2.add("2,2,0,0");
            num2.add("2,2,1,1");
            num2.add("2,2,2,2");
            num2.add("2,2,3,3");
            num2.add("2,2,4,4");

            if(isZf){//展示总分
                int i = 1;
                if (totalSet!=null){
                    Iterator<String> it = totalSet.iterator();
                    while (it.hasNext()) {
                        String str = it.next();
                        excle2.add(str);
                        excle2.add("");
                        excle2.add("");
                        excle2.add("");
                        excle2.add("");
                        int n = i * 5;
                        int n1 = n + 4;
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
                            //处理第一个单入围前面的空格
                            int a1 = subjectSet.size()*7 + 1;
                            excle2.add("");
                            int n3 = (5 * totalSet.size()) + 5 + a;
                            num2.add("2,2," + n3 + "," + n3);
                            a += a1;

                            //单课程总站位
                            int toatl = subjectSet.size() * 7 + 1;

                            //遍历并集
                            if (subjectSet != null) {
                                Iterator<String> it = subjectSet.iterator();
                                int i1 = 0;
                                while (it.hasNext()) {
                                   String str = it.next();
                                    //单入围
                                    String d = str + "单入围";
                                    //双入围
                                    String s = str + "双入围";


                                    excle2.add(d);
                                    excle2.add("");
                                    excle2.add("");
                                    int n = (5 * totalSet.size()) + 6 + (i1 * 7) + subTotal;
                                    int n1 = n + 2;
                                    num2.add("2,2," + n + "," + n1);

                                    excle2.add(s);
                                    excle2.add("");
                                    excle2.add("");
                                    excle2.add("");
                                    int s1 = n+3;
                                    int s2 = s1+3;
                                    num2.add("2,2," + s1 + "," + s2);
                                    i1++;
                                }
                                subTotal += toatl;
                            }
                            //课程循环
                            i2++;
                        }
                    }
                }


                }else {//不展示总分

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
                            //处理第一个单入围前面的空格
                            int a1 = subjectSet.size()*7 + 1;
                            excle2.add("");
                            int n3 =  5 + a;
                            num2.add("2,2," + n3 + "," + n3);
                            a += a1;

                            //课程总站位
                            int toatl = subjectSet.size() * 7 + 1;



                            //遍历并集
                            if (subjectSet != null) {
                                Iterator<String> it = subjectSet.iterator();
                                int i1 = 0;
                                while (it.hasNext()) {
                                    String str = it.next();
                                    //单入围
                                    String d = str + "单入围";
                                    //双入围
                                    String s = str + "双入围";


                                    excle2.add(d);
                                    excle2.add("");
                                    excle2.add("");
                                    int n = 6 + (i1 * 7) + subTotal;
                                    int n1 = n + 2;
                                    num2.add("2,2," + n + "," + n1);

                                    excle2.add(s);
                                    excle2.add("");
                                    excle2.add("");
                                    excle2.add("");
                                    int s1 = n+3;
                                    int s2 = s1+3;
                                    num2.add("2,2," + s1 + "," + s2);
                                    i1++;
                                }
                                subTotal += toatl;
                            }
                            //课程循环
                            i2++;
                        }
                    }
                }

            }

            String[] excelHeader2 = excle2.toArray(new String[0]);
            String[] headnum2 = num2.toArray(new String[0]);




            //表头第四行
            List<String> excle3 = new ArrayList<>();
            excle3.add("");
            excle3.add("");
            excle3.add("");
            excle3.add("");
            excle3.add("");
            List<String> num3 = new ArrayList<>();
            num3.add("3,3,0,0");
            num3.add("3,3,1,1");
            num3.add("3,3,2,2");
            num3.add("3,3,3,3");
            num3.add("3,3,4,4");

            if (isZf){//展示总分
                //遍历总分档次
                if(totalSet!=null && totalSet.size()!=0){
                    Iterator<String> it = totalSet.iterator();
                    //循环参数
                    int i = 1;
                    while (it.hasNext()) {
                        //每一个就是一个档次
                        String str = it.next();
                        excle3.add("任务数");
                        excle3.add("入围人数");
                        excle3.add("差异");
                        excle3.add("同层次排名");
                        excle3.add("贡献率");

                        int s1 = i*5;
                        num3.add("3,3," + s1 + "," + s1);
                        num3.add("3,3," + ++s1 + "," + s1);
                        num3.add("3,3," + ++s1 + "," + s1);
                        num3.add("3,3," + ++s1 + "," + s1);
                        num3.add("3,3," + ++s1 + "," + s1);
                        i++;
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
                                //设置教师
                                excle3.add("教师");
                                //起始位置 = 固定参数 + 总分占位符
                                int n3 = (5 * totalSet.size()) + 5 + a;
                                int a1 = subjectSet.size()*7 + 1;
                                num3.add("3,3," + n3 + "," + n3);
                                a += a1;
                                //遍历档次并集
                                if (subjectSet!=null && subjectSet.size()!=0){
                                    //课程总占位数
                                    int s = 0;
                                  for (int j = 0; j< subjectSet.size(); j++){
                                      //该课程占位数
                                      int s2 = subjectSet.size() * 7;
                                      //起始位置 = 固定参数 + 总分站位符 + 教师一栏占位符 + 课程站位总数
                                      int s1 = 5 + totalSet.size() * 5 + 1 + s;
                                      excle3.add("人数");
                                      excle3.add("任务数");
                                      excle3.add("排名");
                                      excle3.add("人数");
                                      excle3.add("任务数");
                                      excle3.add("排名");
                                      excle3.add("贡献率");

                                      num3.add("3,3," + s1 + "," + s1);
                                      num3.add("3,3," + ++s1 + "," + s1);
                                      num3.add("3,3," + ++s1 + "," + s1);
                                      num3.add("3,3," + ++s1 + "," + s1);
                                      num3.add("3,3," + ++s1 + "," + s1);
                                      num3.add("3,3," + ++s1 + "," + s1);
                                      num3.add("3,3," + ++s1 + "," + s1);

                                      s += s2;

                                  }
                                }
                            }
                        }
                    }
                }
            }else {//不展示总分
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
                            //设置教师
                            excle3.add("教师");
                            //起始位置 = 固定参数
                            int n3 =   5 + a;
                            int a1 = subjectSet.size()*7 + 1;
                            num3.add("3,3," + n3 + "," + n3);
                            a += a1;
                            //遍历档次并集
                            if (subjectSet!=null && subjectSet.size()!=0){
                                //课程总占位数
                                int s = 0;
                                for (int j = 0; j< subjectSet.size(); j++){
                                    //该课程占位数
                                    int s2 = subjectSet.size() * 7;
                                    //起始位置 = 固定参数  + 教师一栏占位符 + 课程站位总数
                                    int s1 = 5  + 1 + s;
                                    excle3.add("人数");
                                    excle3.add("任务数");
                                    excle3.add("排名");
                                    excle3.add("人数");
                                    excle3.add("任务数");
                                    excle3.add("排名");
                                    excle3.add("贡献率");

                                    num3.add("3,3," + s1 + "," + s1);
                                    num3.add("3,3," + ++s1 + "," + s1);
                                    num3.add("3,3," + ++s1 + "," + s1);
                                    num3.add("3,3," + ++s1 + "," + s1);
                                    num3.add("3,3," + ++s1 + "," + s1);
                                    num3.add("3,3," + ++s1 + "," + s1);
                                    num3.add("3,3," + ++s1 + "," + s1);

                                    s += s2;

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
                String fileName = "入围统计";
                exportExcelUtil.exportExcel2(wb,0,"入围统计",style2,headers,dataHandler,response,fileName);
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
