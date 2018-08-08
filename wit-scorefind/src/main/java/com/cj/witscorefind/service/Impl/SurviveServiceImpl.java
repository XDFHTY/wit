package com.cj.witscorefind.service.Impl;

import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.excle.exportExcelUtil;
import com.cj.witscorefind.entity.*;
import com.cj.witscorefind.mapper.ScoreMapper;
import com.cj.witscorefind.mapper.SurviveMapper;
import com.cj.witscorefind.service.SurviveService;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class SurviveServiceImpl implements SurviveService {

    @Autowired
    private SurviveMapper surviveMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    @Value("${school_id}")
    String schoolId;
    //成活率统计
    @Override
    public Object findSurvive(long newExamParentId, long oldExamParentId, long periodId, String thetime, long classTypeId,
                              BigDecimal upScore, BigDecimal downScore) {

        Map map1 = new HashMap();
        map1.put("examParentId",newExamParentId);
        map1.put("periodId",periodId);
        map1.put("thetime",thetime);
        map1.put("classTypeId",classTypeId);
        //查询此次考试总分各档分数线（区分班级类型）
        List<ScoreGrade> newScoreGrades = surviveMapper.findScoreGrade(map1);
        //查询本次考试学生总分及相关信息
        List<ExamClass> newExamClassStudentTotalScores = surviveMapper.findTotalScore(map1);

        //查询学校名称
        String schoolName = surviveMapper.findSchoolName(schoolId);


        map1.put("examParentId",oldExamParentId);
        //查询对比考试总分各档分数线（区分班级类型）
        List<ScoreGrade> oldScoreGrades = surviveMapper.findScoreGrade(map1);

        //查询对比考试学生总分及相关信息
        List<ExamClass> oldExamClassStudentTotalScores = surviveMapper.findTotalScore(map1);

        //TODO:判断此次考试及临界生档次
        for (ExamClass examClass : newExamClassStudentTotalScores){

            for(ClassStudentTotalScore classStudentTotalScore : examClass.getClassStudentTotalScores()){

                for (ScoreGrade scoreGrade : newScoreGrades){
                    //总分小于最高分大于等于最低分
                    if(classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumMax())<0  //-1
                            && classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumMin())>-1){  //0或1

                        //设置档次
                        classStudentTotalScore.setGradeName(scoreGrade.getGradeName());
                    }
                    //总分小于等于分数线+线上分数 && 总分大于等于分数线-线下分数
                    if(classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumScore().add(upScore))<1  //0或-1
                            && classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumScore().subtract(downScore)) > -1){  //0或-1

                        //设置为临界生
                        classStudentTotalScore.setCriticalScore(1);
                    }

                }

            }

        }
        //TODO:判断对比考试及临界生档次
        for (ExamClass examClass : oldExamClassStudentTotalScores){

            for(ClassStudentTotalScore classStudentTotalScore : examClass.getClassStudentTotalScores()){

                for (ScoreGrade scoreGrade : oldScoreGrades){
                    //总分小于最高分大于等于最低分
                    if(classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumMax())<0  //-1
                            && classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumMin())>-1){  //0或1

                        //设置档次
                        classStudentTotalScore.setGradeName(scoreGrade.getGradeName());
                    }
                    //总分小于等于分数线+线上分数 && 总分大于等于分数线-线下分数
                    if(classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumScore().add(upScore))<1  //0或-1
                            && classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumScore().subtract(downScore)) > -1){  //0或-1

                        //设置为临界生
                        classStudentTotalScore.setCriticalScore(1);
                    }
                }

            }

        }

        //统计对比考试各班各档人数
        /*for(ExamClass examClass : oldExamClassStudentTotalScores){

            List<ExamClassGrade> examClassGrades = new ArrayList<>();
            for (ScoreGrade scoreGrade : oldScoreGrades){
                int num = 0;
                ExamClassGrade examClassGrade = new ExamClassGrade();
                List<ClassStudentTotalScore> classStudentTotalScores = new ArrayList<>();
                for(ClassStudentTotalScore classStudentTotalScore : examClass.getClassStudentTotalScores()){


                    if (scoreGrade.getGradeName().equals(classStudentTotalScore.getGradeName())){
                        classStudentTotalScores.add(classStudentTotalScore);
                        num++;
                    }
                    examClassGrade.setClassStudentTotalScores(classStudentTotalScores);


                }
                examClassGrade.setGradeName(scoreGrade.getGradeName());
                examClassGrade.setGradeNameNum(num);
                examClassGrades.add(examClassGrade);
            }

            examClass.setExamClassGrades(examClassGrades);

        }*/

        //此次考试和对比考试都是A档的人数/对比考试的上线人数=A档成活率
        //统计各档此次考试上线人数


        //创建对比考试成活率集合
        List<Survive> oldSurvives = new ArrayList<>();
        //遍历对比考试学生成绩
        if (oldExamClassStudentTotalScores!=null && oldExamClassStudentTotalScores.size()!=0 && oldExamClassStudentTotalScores.get(0)!=null){
            for (ExamClass examClass:oldExamClassStudentTotalScores){
                //每个班级
                //创建成活率对象
                Survive survive = new Survive();
                //设置班级id
                survive.setClassId(examClass.getClassId());
                //设置班号
                survive.setClassNumber(examClass.getClassNumber());
                //设置班级类型
                survive.setClassType(examClass.getClassType());
                //设置班级层次
                survive.setClassLevel(examClass.getClassLevel());
                //设置班主任名
                survive.setClassHeadmaster(examClass.getClassHeadmaster());
                //创建各个成活率档次集合
                survive.setSurviveGradeList(new ArrayList<>());

                //遍历对比考试的总分各档
                if (oldScoreGrades!=null && oldScoreGrades.size()!=0 && oldScoreGrades.get(0)!=null){
                    for (ScoreGrade sg:oldScoreGrades){
                        //创建成活率档次对象
                        SurviveGrade surviveGrade = new SurviveGrade();
                        //设置档次名称
                        surviveGrade.setGradeName(sg.getGradeName());
                        surviveGrade.setInCityNumList(new ArrayList<>());
                        surviveGrade.setOutCityNumList(new ArrayList<>());
                        surviveGrade.setTheSchoolCityNumList(new ArrayList<>());
                        surviveGrade.setCriticalRawCityNumList(new ArrayList<>());

                        //遍历班级的学生分数信息
                        List<ClassStudentTotalScore> list = examClass.getClassStudentTotalScores();
                        if (list!=null && list.size()!=0 && list.get(0)!=null){
                            for (ClassStudentTotalScore score : list){
                                //判断该学生的档次是否为当前遍历档次
                                if (sg.getGradeName().equals(score.getGradeName())){
                                    //获取学生来源
                                    String studentsComeIn = score.getStudentsComeIn();
                                    //判断是否为市内学生
                                    if (studentsComeIn!=null && !"".equals(studentsComeIn)){
                                        if (!"省市外".equals(studentsComeIn)){//市内学生
                                            //添加学籍号
                                            surviveGrade.getInCityNumList().add(score.getRegisterNumber());
                                        }else {//省市外
                                            surviveGrade.getOutCityNumList().add(score.getRegisterNumber());
                                        }
                                    }

                                    //获取原学校名称
                                    String originalSchool = score.getOriginalSchool();
                                    //判断是否为本校升入
                                    if (originalSchool!=null && !"".equals(originalSchool)){
                                        if (originalSchool.equals(schoolName)){//是本校
                                            surviveGrade.getTheSchoolCityNumList().add(score.getRegisterNumber());
                                        }
                                    }

                                    //判断临界生
                                    if (score.getCriticalScore()==1){//是临界生
                                        surviveGrade.getCriticalRawCityNumList().add(score.getRegisterNumber());
                                    }
                                }
                            }
                        }

                        //添加至集合
                        survive.getSurviveGradeList().add(surviveGrade);
                    }
                }

                //添加至集合
                oldSurvives.add(survive);
            }
        }




        //创建本次考试成活率集合
        List<Survive> newSurvives = new ArrayList<>();
        //遍历对比考试学生成绩
        if (newExamClassStudentTotalScores!=null && newExamClassStudentTotalScores.size()!=0 && newExamClassStudentTotalScores.get(0)!=null){
            for (ExamClass examClass:newExamClassStudentTotalScores){
                //每个班级
                //创建成活率对象
                Survive survive = new Survive();
                //设置班级id
                survive.setClassId(examClass.getClassId());
                //设置班号
                survive.setClassNumber(examClass.getClassNumber());
                //设置班级类型
                survive.setClassType(examClass.getClassType());
                //设置班级层次
                survive.setClassLevel(examClass.getClassLevel());
                //设置班主任名
                survive.setClassHeadmaster(examClass.getClassHeadmaster());
                //创建各个成活率档次集合
                survive.setSurviveGradeList(new ArrayList<>());

                //遍历对比考试的总分各档
                if (newScoreGrades!=null && newScoreGrades.size()!=0 && newScoreGrades.get(0)!=null){
                    for (ScoreGrade sg:newScoreGrades){
                        //创建成活率档次对象
                        SurviveGrade surviveGrade = new SurviveGrade();
                        //设置档次名称
                        surviveGrade.setGradeName(sg.getGradeName());
                        surviveGrade.setInCityNumList(new ArrayList<>());
                        surviveGrade.setOutCityNumList(new ArrayList<>());
                        surviveGrade.setTheSchoolCityNumList(new ArrayList<>());
                        surviveGrade.setCriticalRawCityNumList(new ArrayList<>());

                        //遍历班级的学生分数信息
                        List<ClassStudentTotalScore> list = examClass.getClassStudentTotalScores();
                        if (list!=null && list.size()!=0 && list.get(0)!=null){
                            for (ClassStudentTotalScore score : list){
                                //判断该学生的档次是否为当前遍历档次
                                if (sg.getGradeName().equals(score.getGradeName())){
                                    //获取学生来源
                                    String studentsComeIn = score.getStudentsComeIn();
                                    //判断是否为市内学生
                                    if (studentsComeIn!=null && !"".equals(studentsComeIn)){
                                        if (!"省市外".equals(studentsComeIn)){//市内学生
                                            //添加学籍号
                                            surviveGrade.getInCityNumList().add(score.getRegisterNumber());
                                        }else {//省市外
                                            surviveGrade.getOutCityNumList().add(score.getRegisterNumber());
                                        }
                                    }

                                    //获取原学校名称
                                    String originalSchool = score.getOriginalSchool();
                                    //判断是否为本校升入
                                    if (originalSchool!=null && !"".equals(originalSchool)){
                                        if (originalSchool.equals(schoolName)){//是本校
                                            surviveGrade.getTheSchoolCityNumList().add(score.getRegisterNumber());
                                        }
                                    }

                                    //判断临界生
                                    if (score.getCriticalScore()==1){//是临界生
                                        surviveGrade.getCriticalRawCityNumList().add(score.getRegisterNumber());
                                    }
                                }
                            }
                        }

                        //添加至集合
                        survive.getSurviveGradeList().add(surviveGrade);
                    }
                }

                //添加至集合
                newSurvives.add(survive);
            }
        }



        //计算成活率  本次考试入围人数和对比考试入围人数的交集/对比考试入围总数

        //遍历本次考试集合
        if (newSurvives != null && newSurvives.size() != 0 && newSurvives.get(0) != null) {
            //每个班
            for (Survive newSurvive : newSurvives) {
                //遍历每个档次
                List<SurviveGrade> newGrades = newSurvive.getSurviveGradeList();
                if (newGrades!=null && newGrades.size()!=0 && newGrades.get(0)!=null){
                    for (SurviveGrade newSG:newGrades){

  /////////////////////////////////////////市内成活率计算////////////////////////////////////////////
                        //求市内成活的 交集个数
                        Integer sn1 = 0;
                        //对比考试此档入围总数
                        Integer sn2 = 0;
                        //获取本次考试 市内 学生学籍号集合
                        List<String> newNums = newSG.getInCityNumList();
                        //遍历每个学籍号
                        if (newNums!=null && newNums.size()!=0 && newNums.get(0)!=null){
                            for (String newNum:newNums){
                                //把这个学籍号和 对比考试的 这个班的这个档的 市内 学生学籍号对比
                                //如果存在  则市内交集+1  s1++

                                //遍历对比考试
                                if (oldSurvives!=null && oldSurvives.size()!=0 && oldSurvives.get(0)!=null){
                                    for (Survive oldSurvice:oldSurvives){
                                        //判断班级
                                        if (newSurvive.getClassId().equals(oldSurvice.getClassId())){
                                            //班级相同
                                            //遍历档次
                                            List<SurviveGrade> oldGrades = oldSurvice.getSurviveGradeList();
                                            if (oldGrades!=null && oldGrades.size()!=0 && oldGrades.get(0)!=null){
                                                for (SurviveGrade oldSG:oldGrades){
                                                    //判断档次
                                                    if (newSG.getGradeName().equals(oldSG.getGradeName())){
                                                        //档次名称相等
                                                        sn2 = oldSG.getInCityNumList().size();

                                                        //获取市内学籍号集合
                                                        List<String> oldNums = oldSG.getInCityNumList();
                                                        //遍历学籍号
                                                        if (oldNums!=null && oldNums.size()!=0 && oldNums.get(0)!=null){
                                                            for (String oldNum:oldNums){
                                                                //学籍号相同  交集+1
                                                                if (newNum.equals(oldNum)){
                                                                    sn1++;
                                                                    //找到相同学籍号 跳出循环
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        //跳出档次循环
                                                        break;
                                                    }
                                                }
                                            }

                                            //跳出班级循环
                                            break;
                                        }
                                    }
                                }

                            }
                        }

                        //市内成活率计算

                        //市内成活利率 = 交集 / 对比考试此档市内总人数
                        if (sn2!=0){//0不作为被除数
                            BigDecimal bd1 = new BigDecimal(sn1);
                            BigDecimal bd2 = new BigDecimal(sn2);
                            //计算 精确到小数点后4位 四舍五入(向上取整)
                            BigDecimal result = bd1.divide(bd2, 4, BigDecimal.ROUND_HALF_UP);
                            //转换成百分比
                            DecimalFormat df = new DecimalFormat("0.00%");
                            newSG.setInCity(df.format(result));
                        }else {
                            newSG.setInCity("0.00%");
                        }




    ///////////////////////////////////////////////室外成活率计算////////////////////////////////////////////////////////////
                        //交集个数
                        Integer sw1 = 0;
                        //对比考试此档入围总数
                        Integer sw2 = 0;
                        //获取本次考试  学生学籍号集合
                        List<String> newOutNums = newSG.getOutCityNumList();
                        //遍历每个学籍号
                        if (newOutNums!=null && newOutNums.size()!=0 && newOutNums.get(0)!=null){
                            for (String newNum:newOutNums){
                                //把这个学籍号和 对比考试的 这个班的这个档的 学生学籍号对比
                                //如果存在  则交集+1  s1++

                                //遍历对比考试
                                if (oldSurvives!=null && oldSurvives.size()!=0 && oldSurvives.get(0)!=null){
                                    for (Survive oldSurvice:oldSurvives){
                                        //判断班级
                                        if (newSurvive.getClassId().equals(oldSurvice.getClassId())){
                                            //班级相同
                                            //遍历档次
                                            List<SurviveGrade> oldGrades = oldSurvice.getSurviveGradeList();
                                            if (oldGrades!=null && oldGrades.size()!=0 && oldGrades.get(0)!=null){
                                                for (SurviveGrade oldSG:oldGrades){
                                                    //判断档次
                                                    if (newSG.getGradeName().equals(oldSG.getGradeName())){
                                                        //档次名称相等
                                                        sw2 = oldSG.getOutCityNumList().size();

                                                        //获取学籍号集合
                                                        List<String> oldOutNums = oldSG.getOutCityNumList();
                                                        //遍历学籍号
                                                        if (oldOutNums!=null && oldOutNums.size()!=0 && oldOutNums.get(0)!=null){
                                                            for (String oldNum:oldOutNums){
                                                                //学籍号相同  交集+1
                                                                if (newNum.equals(oldNum)){
                                                                    sw1++;
                                                                    //找到相同学籍号 跳出循环
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        //跳出档次循环
                                                        break;
                                                    }
                                                }
                                            }

                                            //跳出班级循环
                                            break;
                                        }
                                    }
                                }

                            }
                        }

                        //成活率计算

                        //成活利率 = 交集 / 对比考试此档总人数
                        if (sw2!=0){//0不作为被除数
                            BigDecimal bd1 = new BigDecimal(sw1);
                            BigDecimal bd2 = new BigDecimal(sw2);
                            //计算 精确到小数点后4位 四舍五入(向上取整)
                            BigDecimal result = bd1.divide(bd2, 4, BigDecimal.ROUND_HALF_UP);
                            //转换成百分比
                            DecimalFormat df = new DecimalFormat("0.00%");
                            newSG.setOutCity(df.format(result));
                        }else {
                            newSG.setOutCity("0.00%");
                        }




    ///////////////////////////////////////////////本校升入成活率计算////////////////////////////////////////////////////////////
                        //交集个数
                        Integer bxsr1 = 0;
                        //对比考试此档入围总数
                        Integer bxsr2 = 0;
                        //获取本次考试  学生学籍号集合
                        List<String> newBxNums = newSG.getTheSchoolCityNumList();
                        //遍历每个学籍号
                        if (newBxNums!=null && newBxNums.size()!=0 && newBxNums.get(0)!=null){
                            for (String newNum:newBxNums){
                                //把这个学籍号和 对比考试的 这个班的这个档的 学生学籍号对比
                                //如果存在  则交集+1  s1++

                                //遍历对比考试
                                if (oldSurvives!=null && oldSurvives.size()!=0 && oldSurvives.get(0)!=null){
                                    for (Survive oldSurvice:oldSurvives){
                                        //判断班级
                                        if (newSurvive.getClassId().equals(oldSurvice.getClassId())){
                                            //班级相同
                                            //遍历档次
                                            List<SurviveGrade> oldGrades = oldSurvice.getSurviveGradeList();
                                            if (oldGrades!=null && oldGrades.size()!=0 && oldGrades.get(0)!=null){
                                                for (SurviveGrade oldSG:oldGrades){
                                                    //判断档次
                                                    if (newSG.getGradeName().equals(oldSG.getGradeName())){
                                                        //档次名称相等
                                                        bxsr2 = oldSG.getTheSchoolCityNumList().size();

                                                        //获取学籍号集合
                                                        List<String> oldBxNums = oldSG.getTheSchoolCityNumList();
                                                        //遍历学籍号
                                                        if (oldBxNums!=null && oldBxNums.size()!=0 && oldBxNums.get(0)!=null){
                                                            for (String oldNum:oldBxNums){
                                                                //学籍号相同  交集+1
                                                                if (newNum.equals(oldNum)){
                                                                    bxsr1++;
                                                                    //找到相同学籍号 跳出循环
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        //跳出档次循环
                                                        break;
                                                    }
                                                }
                                            }

                                            //跳出班级循环
                                            break;
                                        }
                                    }
                                }

                            }
                        }

                        //成活率计算

                        //成活利率 = 交集 / 对比考试此档总人数
                        if (bxsr2!=0){//0不作为被除数
                            BigDecimal bd1 = new BigDecimal(bxsr1);
                            BigDecimal bd2 = new BigDecimal(bxsr2);
                            //计算 精确到小数点后4位 四舍五入(向上取整)
                            BigDecimal result = bd1.divide(bd2, 4, BigDecimal.ROUND_HALF_UP);
                            //转换成百分比
                            DecimalFormat df = new DecimalFormat("0.00%");
                            newSG.setTheSchool(df.format(result));
                        }else {
                            newSG.setTheSchool("0.00%");
                        }



    ///////////////////////////////////////////////临界生成活率计算////////////////////////////////////////////////////////////
                        //交集个数
                        Integer ljs1 = 0;
                        //对比考试此档入围总数
                        Integer ljs2 = 0;
                        //获取本次考试  学生学籍号集合
                        List<String> newLjsNums = newSG.getCriticalRawCityNumList();
                        //遍历每个学籍号
                        if (newLjsNums!=null && newLjsNums.size()!=0 && newLjsNums.get(0)!=null){
                            for (String newNum:newLjsNums){
                                //把这个学籍号和 对比考试的 这个班的这个档的 学生学籍号对比
                                //如果存在  则交集+1  s1++

                                //遍历对比考试
                                if (oldSurvives!=null && oldSurvives.size()!=0 && oldSurvives.get(0)!=null){
                                    for (Survive oldSurvice:oldSurvives){
                                        //判断班级
                                        if (newSurvive.getClassId().equals(oldSurvice.getClassId())){
                                            //班级相同
                                            //遍历档次
                                            List<SurviveGrade> oldGrades = oldSurvice.getSurviveGradeList();
                                            if (oldGrades!=null && oldGrades.size()!=0 && oldGrades.get(0)!=null){
                                                for (SurviveGrade oldSG:oldGrades){
                                                    //判断档次
                                                    if (newSG.getGradeName().equals(oldSG.getGradeName())){
                                                        //档次名称相等
                                                        ljs2 = oldSG.getCriticalRawCityNumList().size();

                                                        //获取学籍号集合
                                                        List<String> oldLjsNums = oldSG.getCriticalRawCityNumList();
                                                        //遍历学籍号
                                                        if (oldLjsNums!=null && oldLjsNums.size()!=0 && oldLjsNums.get(0)!=null){
                                                            for (String oldNum:oldLjsNums){
                                                                //学籍号相同  交集+1
                                                                if (newNum.equals(oldNum)){
                                                                    ljs1++;
                                                                    //找到相同学籍号 跳出循环
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        //跳出档次循环
                                                        break;
                                                    }
                                                }
                                            }

                                            //跳出班级循环
                                            break;
                                        }
                                    }
                                }

                            }
                        }

                        //成活率计算

                        //成活利率 = 交集 / 对比考试此档总人数
                        if (ljs2!=0){//0不作为被除数
                            BigDecimal bd1 = new BigDecimal(ljs1);
                            BigDecimal bd2 = new BigDecimal(ljs2);
                            //计算 精确到小数点后4位 四舍五入(向上取整)
                            BigDecimal result = bd1.divide(bd2, 4, BigDecimal.ROUND_HALF_UP);
                            //转换成百分比
                            DecimalFormat df = new DecimalFormat("0.00%");
                            newSG.setCriticalRaw(df.format(result));
                        }else {
                            newSG.setCriticalRaw("0.00%");
                        }

                        /*System.out.println("班级：" + newSurvive.getClassId() + "   本次考试档次："
                                + newSG.getGradeName() + "   市内交集个数：" + sn1 +
                                "   市内总数：" + sn2 + "   市内成活率:" + newSG.getInCity()
                                + "   室外交集个数："+sw1+ "    室外总数：" +sw2+ "   室外成活率:"+ newSG.getOutCity()
                                + "   本校升入交集个数："+bxsr1 + "    本校升入总数："+bxsr2+"    本校升入成活率："+ newSG.getTheSchool()
                        + "     临界生交集个数："+ljs1+"    临界生总数："+ljs2+"   临界生成活率:"+newSG.getCriticalRaw());
*/
                    }
                }
            }
        }


        ApiResult apiResult = new ApiResult();
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(newSurvives);

        return apiResult;
    }

    @Override
    public void exportSurvive(long newExamParentId, long oldExamParentId, long periodId, String thetime, long classTypeId, BigDecimal upScore, BigDecimal downScore, HttpServletResponse response) throws Exception {
        Map map1 = new HashMap();
        map1.put("examParentId",newExamParentId);
        map1.put("periodId",periodId);
        map1.put("thetime",thetime);
        map1.put("classTypeId",classTypeId);
        //查询此次考试总分各档分数线（区分班级类型）
        List<ScoreGrade> newScoreGrades = surviveMapper.findScoreGrade(map1);
        //查询本次考试学生总分及相关信息
        List<ExamClass> newExamClassStudentTotalScores = surviveMapper.findTotalScore(map1);

        //查询学校名称
        String schoolName = surviveMapper.findSchoolName(schoolId);


        map1.put("examParentId",oldExamParentId);
        //查询对比考试总分各档分数线（区分班级类型）
        List<ScoreGrade> oldScoreGrades = surviveMapper.findScoreGrade(map1);

        //查询对比考试学生总分及相关信息
        List<ExamClass> oldExamClassStudentTotalScores = surviveMapper.findTotalScore(map1);

        //TODO:判断此次考试及临界生档次
        for (ExamClass examClass : newExamClassStudentTotalScores){

            for(ClassStudentTotalScore classStudentTotalScore : examClass.getClassStudentTotalScores()){

                for (ScoreGrade scoreGrade : newScoreGrades){
                    //总分小于最高分大于等于最低分
                    if(classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumMax())<0  //-1
                            && classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumMin())>-1){  //0或1

                        //设置档次
                        classStudentTotalScore.setGradeName(scoreGrade.getGradeName());
                    }
                    //总分小于等于分数线+线上分数 && 总分大于等于分数线-线下分数
                    if(classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumScore().add(upScore))<1  //0或-1
                            && classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumScore().subtract(downScore)) > -1){  //0或-1

                        //设置为临界生
                        classStudentTotalScore.setCriticalScore(1);
                    }

                }

            }

        }
        //TODO:判断对比考试及临界生档次
        for (ExamClass examClass : oldExamClassStudentTotalScores){

            for(ClassStudentTotalScore classStudentTotalScore : examClass.getClassStudentTotalScores()){

                for (ScoreGrade scoreGrade : oldScoreGrades){
                    //总分小于最高分大于等于最低分
                    if(classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumMax())<0  //-1
                            && classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumMin())>-1){  //0或1

                        //设置档次
                        classStudentTotalScore.setGradeName(scoreGrade.getGradeName());
                    }
                    //总分小于等于分数线+线上分数 && 总分大于等于分数线-线下分数
                    if(classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumScore().add(upScore))<1  //0或-1
                            && classStudentTotalScore.getTotalScore().compareTo(scoreGrade.getNumScore().subtract(downScore)) > -1){  //0或-1

                        //设置为临界生
                        classStudentTotalScore.setCriticalScore(1);
                    }
                }

            }

        }

        //统计对比考试各班各档人数
        /*for(ExamClass examClass : oldExamClassStudentTotalScores){

            List<ExamClassGrade> examClassGrades = new ArrayList<>();
            for (ScoreGrade scoreGrade : oldScoreGrades){
                int num = 0;
                ExamClassGrade examClassGrade = new ExamClassGrade();
                List<ClassStudentTotalScore> classStudentTotalScores = new ArrayList<>();
                for(ClassStudentTotalScore classStudentTotalScore : examClass.getClassStudentTotalScores()){


                    if (scoreGrade.getGradeName().equals(classStudentTotalScore.getGradeName())){
                        classStudentTotalScores.add(classStudentTotalScore);
                        num++;
                    }
                    examClassGrade.setClassStudentTotalScores(classStudentTotalScores);


                }
                examClassGrade.setGradeName(scoreGrade.getGradeName());
                examClassGrade.setGradeNameNum(num);
                examClassGrades.add(examClassGrade);
            }

            examClass.setExamClassGrades(examClassGrades);

        }*/

        //此次考试和对比考试都是A档的人数/对比考试的上线人数=A档成活率
        //统计各档此次考试上线人数


        //创建对比考试成活率集合
        List<Survive> oldSurvives = new ArrayList<>();
        //遍历对比考试学生成绩
        if (oldExamClassStudentTotalScores!=null && oldExamClassStudentTotalScores.size()!=0 && oldExamClassStudentTotalScores.get(0)!=null){
            for (ExamClass examClass:oldExamClassStudentTotalScores){
                //每个班级
                //创建成活率对象
                Survive survive = new Survive();
                //设置班级id
                survive.setClassId(examClass.getClassId());
                //设置班号
                survive.setClassNumber(examClass.getClassNumber());
                //设置班级类型
                survive.setClassType(examClass.getClassType());
                //设置班级层次
                survive.setClassLevel(examClass.getClassLevel());
                //设置班主任名
                survive.setClassHeadmaster(examClass.getClassHeadmaster());
                //创建各个成活率档次集合
                survive.setSurviveGradeList(new ArrayList<>());

                //遍历对比考试的总分各档
                if (oldScoreGrades!=null && oldScoreGrades.size()!=0 && oldScoreGrades.get(0)!=null){
                    for (ScoreGrade sg:oldScoreGrades){
                        //创建成活率档次对象
                        SurviveGrade surviveGrade = new SurviveGrade();
                        //设置档次名称
                        surviveGrade.setGradeName(sg.getGradeName());
                        surviveGrade.setInCityNumList(new ArrayList<>());
                        surviveGrade.setOutCityNumList(new ArrayList<>());
                        surviveGrade.setTheSchoolCityNumList(new ArrayList<>());
                        surviveGrade.setCriticalRawCityNumList(new ArrayList<>());

                        //遍历班级的学生分数信息
                        List<ClassStudentTotalScore> list = examClass.getClassStudentTotalScores();
                        if (list!=null && list.size()!=0 && list.get(0)!=null){
                            for (ClassStudentTotalScore score : list){
                                //判断该学生的档次是否为当前遍历档次
                                if (sg.getGradeName().equals(score.getGradeName())){
                                    //获取学生来源
                                    String studentsComeIn = score.getStudentsComeIn();
                                    //判断是否为市内学生
                                    if (studentsComeIn!=null && !"".equals(studentsComeIn)){
                                        if (!"省市外".equals(studentsComeIn)){//市内学生
                                            //添加学籍号
                                            surviveGrade.getInCityNumList().add(score.getRegisterNumber());
                                        }else {//省市外
                                            surviveGrade.getOutCityNumList().add(score.getRegisterNumber());
                                        }
                                    }

                                    //获取原学校名称
                                    String originalSchool = score.getOriginalSchool();
                                    //判断是否为本校升入
                                    if (originalSchool!=null && !"".equals(originalSchool)){
                                        if (originalSchool.equals(schoolName)){//是本校
                                            surviveGrade.getTheSchoolCityNumList().add(score.getRegisterNumber());
                                        }
                                    }

                                    //判断临界生
                                    if (score.getCriticalScore()==1){//是临界生
                                        surviveGrade.getCriticalRawCityNumList().add(score.getRegisterNumber());
                                    }
                                }
                            }
                        }

                        //添加至集合
                        survive.getSurviveGradeList().add(surviveGrade);
                    }
                }

                //添加至集合
                oldSurvives.add(survive);
            }
        }




        //创建本次考试成活率集合
        List<Survive> newSurvives = new ArrayList<>();
        //遍历对比考试学生成绩
        if (newExamClassStudentTotalScores!=null && newExamClassStudentTotalScores.size()!=0 && newExamClassStudentTotalScores.get(0)!=null){
            for (ExamClass examClass:newExamClassStudentTotalScores){
                //每个班级
                //创建成活率对象
                Survive survive = new Survive();
                //设置班级id
                survive.setClassId(examClass.getClassId());
                //设置班号
                survive.setClassNumber(examClass.getClassNumber());
                //设置班级类型
                survive.setClassType(examClass.getClassType());
                //设置班级层次
                survive.setClassLevel(examClass.getClassLevel());
                //设置班主任名
                survive.setClassHeadmaster(examClass.getClassHeadmaster());
                //创建各个成活率档次集合
                survive.setSurviveGradeList(new ArrayList<>());

                //遍历对比考试的总分各档
                if (newScoreGrades!=null && newScoreGrades.size()!=0 && newScoreGrades.get(0)!=null){
                    for (ScoreGrade sg:newScoreGrades){
                        //创建成活率档次对象
                        SurviveGrade surviveGrade = new SurviveGrade();
                        //设置档次名称
                        surviveGrade.setGradeName(sg.getGradeName());
                        surviveGrade.setInCityNumList(new ArrayList<>());
                        surviveGrade.setOutCityNumList(new ArrayList<>());
                        surviveGrade.setTheSchoolCityNumList(new ArrayList<>());
                        surviveGrade.setCriticalRawCityNumList(new ArrayList<>());

                        //遍历班级的学生分数信息
                        List<ClassStudentTotalScore> list = examClass.getClassStudentTotalScores();
                        if (list!=null && list.size()!=0 && list.get(0)!=null){
                            for (ClassStudentTotalScore score : list){
                                //判断该学生的档次是否为当前遍历档次
                                if (sg.getGradeName().equals(score.getGradeName())){
                                    //获取学生来源
                                    String studentsComeIn = score.getStudentsComeIn();
                                    //判断是否为市内学生
                                    if (studentsComeIn!=null && !"".equals(studentsComeIn)){
                                        if (!"省市外".equals(studentsComeIn)){//市内学生
                                            //添加学籍号
                                            surviveGrade.getInCityNumList().add(score.getRegisterNumber());
                                        }else {//省市外
                                            surviveGrade.getOutCityNumList().add(score.getRegisterNumber());
                                        }
                                    }

                                    //获取原学校名称
                                    String originalSchool = score.getOriginalSchool();
                                    //判断是否为本校升入
                                    if (originalSchool!=null && !"".equals(originalSchool)){
                                        if (originalSchool.equals(schoolName)){//是本校
                                            surviveGrade.getTheSchoolCityNumList().add(score.getRegisterNumber());
                                        }
                                    }

                                    //判断临界生
                                    if (score.getCriticalScore()==1){//是临界生
                                        surviveGrade.getCriticalRawCityNumList().add(score.getRegisterNumber());
                                    }
                                }
                            }
                        }

                        //添加至集合
                        survive.getSurviveGradeList().add(surviveGrade);
                    }
                }

                //添加至集合
                newSurvives.add(survive);
            }
        }



        //计算成活率  本次考试入围人数和对比考试入围人数的交集/对比考试入围总数

        //遍历本次考试集合
        if (newSurvives != null && newSurvives.size() != 0 && newSurvives.get(0) != null) {
            //每个班
            for (Survive newSurvive : newSurvives) {
                //遍历每个档次
                List<SurviveGrade> newGrades = newSurvive.getSurviveGradeList();
                if (newGrades!=null && newGrades.size()!=0 && newGrades.get(0)!=null){
                    for (SurviveGrade newSG:newGrades){

                        /////////////////////////////////////////市内成活率计算////////////////////////////////////////////
                        //求市内成活的 交集个数
                        Integer sn1 = 0;
                        //对比考试此档入围总数
                        Integer sn2 = 0;
                        //获取本次考试 市内 学生学籍号集合
                        List<String> newNums = newSG.getInCityNumList();
                        //遍历每个学籍号
                        if (newNums!=null && newNums.size()!=0 && newNums.get(0)!=null){
                            for (String newNum:newNums){
                                //把这个学籍号和 对比考试的 这个班的这个档的 市内 学生学籍号对比
                                //如果存在  则市内交集+1  s1++

                                //遍历对比考试
                                if (oldSurvives!=null && oldSurvives.size()!=0 && oldSurvives.get(0)!=null){
                                    for (Survive oldSurvice:oldSurvives){
                                        //判断班级
                                        if (newSurvive.getClassId().equals(oldSurvice.getClassId())){
                                            //班级相同
                                            //遍历档次
                                            List<SurviveGrade> oldGrades = oldSurvice.getSurviveGradeList();
                                            if (oldGrades!=null && oldGrades.size()!=0 && oldGrades.get(0)!=null){
                                                for (SurviveGrade oldSG:oldGrades){
                                                    //判断档次
                                                    if (newSG.getGradeName().equals(oldSG.getGradeName())){
                                                        //档次名称相等
                                                        sn2 = oldSG.getInCityNumList().size();

                                                        //获取市内学籍号集合
                                                        List<String> oldNums = oldSG.getInCityNumList();
                                                        //遍历学籍号
                                                        if (oldNums!=null && oldNums.size()!=0 && oldNums.get(0)!=null){
                                                            for (String oldNum:oldNums){
                                                                //学籍号相同  交集+1
                                                                if (newNum.equals(oldNum)){
                                                                    sn1++;
                                                                    //找到相同学籍号 跳出循环
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        //跳出档次循环
                                                        break;
                                                    }
                                                }
                                            }

                                            //跳出班级循环
                                            break;
                                        }
                                    }
                                }

                            }
                        }

                        //市内成活率计算

                        //市内成活利率 = 交集 / 对比考试此档市内总人数
                        if (sn2!=0){//0不作为被除数
                            BigDecimal bd1 = new BigDecimal(sn1);
                            BigDecimal bd2 = new BigDecimal(sn2);
                            //计算 精确到小数点后4位 四舍五入(向上取整)
                            BigDecimal result = bd1.divide(bd2, 4, BigDecimal.ROUND_HALF_UP);
                            //转换成百分比
                            DecimalFormat df = new DecimalFormat("0.00%");
                            newSG.setInCity(df.format(result));
                        }else {
                            newSG.setInCity("0.00%");
                        }




                        ///////////////////////////////////////////////室外成活率计算////////////////////////////////////////////////////////////
                        //交集个数
                        Integer sw1 = 0;
                        //对比考试此档入围总数
                        Integer sw2 = 0;
                        //获取本次考试  学生学籍号集合
                        List<String> newOutNums = newSG.getOutCityNumList();
                        //遍历每个学籍号
                        if (newOutNums!=null && newOutNums.size()!=0 && newOutNums.get(0)!=null){
                            for (String newNum:newOutNums){
                                //把这个学籍号和 对比考试的 这个班的这个档的 学生学籍号对比
                                //如果存在  则交集+1  s1++

                                //遍历对比考试
                                if (oldSurvives!=null && oldSurvives.size()!=0 && oldSurvives.get(0)!=null){
                                    for (Survive oldSurvice:oldSurvives){
                                        //判断班级
                                        if (newSurvive.getClassId().equals(oldSurvice.getClassId())){
                                            //班级相同
                                            //遍历档次
                                            List<SurviveGrade> oldGrades = oldSurvice.getSurviveGradeList();
                                            if (oldGrades!=null && oldGrades.size()!=0 && oldGrades.get(0)!=null){
                                                for (SurviveGrade oldSG:oldGrades){
                                                    //判断档次
                                                    if (newSG.getGradeName().equals(oldSG.getGradeName())){
                                                        //档次名称相等
                                                        sw2 = oldSG.getOutCityNumList().size();

                                                        //获取学籍号集合
                                                        List<String> oldOutNums = oldSG.getOutCityNumList();
                                                        //遍历学籍号
                                                        if (oldOutNums!=null && oldOutNums.size()!=0 && oldOutNums.get(0)!=null){
                                                            for (String oldNum:oldOutNums){
                                                                //学籍号相同  交集+1
                                                                if (newNum.equals(oldNum)){
                                                                    sw1++;
                                                                    //找到相同学籍号 跳出循环
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        //跳出档次循环
                                                        break;
                                                    }
                                                }
                                            }

                                            //跳出班级循环
                                            break;
                                        }
                                    }
                                }

                            }
                        }

                        //成活率计算

                        //成活利率 = 交集 / 对比考试此档总人数
                        if (sw2!=0){//0不作为被除数
                            BigDecimal bd1 = new BigDecimal(sw1);
                            BigDecimal bd2 = new BigDecimal(sw2);
                            //计算 精确到小数点后4位 四舍五入(向上取整)
                            BigDecimal result = bd1.divide(bd2, 4, BigDecimal.ROUND_HALF_UP);
                            //转换成百分比
                            DecimalFormat df = new DecimalFormat("0.00%");
                            newSG.setOutCity(df.format(result));
                        }else {
                            newSG.setOutCity("0.00%");
                        }




                        ///////////////////////////////////////////////本校升入成活率计算////////////////////////////////////////////////////////////
                        //交集个数
                        Integer bxsr1 = 0;
                        //对比考试此档入围总数
                        Integer bxsr2 = 0;
                        //获取本次考试  学生学籍号集合
                        List<String> newBxNums = newSG.getTheSchoolCityNumList();
                        //遍历每个学籍号
                        if (newBxNums!=null && newBxNums.size()!=0 && newBxNums.get(0)!=null){
                            for (String newNum:newBxNums){
                                //把这个学籍号和 对比考试的 这个班的这个档的 学生学籍号对比
                                //如果存在  则交集+1  s1++

                                //遍历对比考试
                                if (oldSurvives!=null && oldSurvives.size()!=0 && oldSurvives.get(0)!=null){
                                    for (Survive oldSurvice:oldSurvives){
                                        //判断班级
                                        if (newSurvive.getClassId().equals(oldSurvice.getClassId())){
                                            //班级相同
                                            //遍历档次
                                            List<SurviveGrade> oldGrades = oldSurvice.getSurviveGradeList();
                                            if (oldGrades!=null && oldGrades.size()!=0 && oldGrades.get(0)!=null){
                                                for (SurviveGrade oldSG:oldGrades){
                                                    //判断档次
                                                    if (newSG.getGradeName().equals(oldSG.getGradeName())){
                                                        //档次名称相等
                                                        bxsr2 = oldSG.getTheSchoolCityNumList().size();

                                                        //获取学籍号集合
                                                        List<String> oldBxNums = oldSG.getTheSchoolCityNumList();
                                                        //遍历学籍号
                                                        if (oldBxNums!=null && oldBxNums.size()!=0 && oldBxNums.get(0)!=null){
                                                            for (String oldNum:oldBxNums){
                                                                //学籍号相同  交集+1
                                                                if (newNum.equals(oldNum)){
                                                                    bxsr1++;
                                                                    //找到相同学籍号 跳出循环
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        //跳出档次循环
                                                        break;
                                                    }
                                                }
                                            }

                                            //跳出班级循环
                                            break;
                                        }
                                    }
                                }

                            }
                        }

                        //成活率计算

                        //成活利率 = 交集 / 对比考试此档总人数
                        if (bxsr2!=0){//0不作为被除数
                            BigDecimal bd1 = new BigDecimal(bxsr1);
                            BigDecimal bd2 = new BigDecimal(bxsr2);
                            //计算 精确到小数点后4位 四舍五入(向上取整)
                            BigDecimal result = bd1.divide(bd2, 4, BigDecimal.ROUND_HALF_UP);
                            //转换成百分比
                            DecimalFormat df = new DecimalFormat("0.00%");
                            newSG.setTheSchool(df.format(result));
                        }else {
                            newSG.setTheSchool("0.00%");
                        }



                        ///////////////////////////////////////////////临界生成活率计算////////////////////////////////////////////////////////////
                        //交集个数
                        Integer ljs1 = 0;
                        //对比考试此档入围总数
                        Integer ljs2 = 0;
                        //获取本次考试  学生学籍号集合
                        List<String> newLjsNums = newSG.getCriticalRawCityNumList();
                        //遍历每个学籍号
                        if (newLjsNums!=null && newLjsNums.size()!=0 && newLjsNums.get(0)!=null){
                            for (String newNum:newLjsNums){
                                //把这个学籍号和 对比考试的 这个班的这个档的 学生学籍号对比
                                //如果存在  则交集+1  s1++

                                //遍历对比考试
                                if (oldSurvives!=null && oldSurvives.size()!=0 && oldSurvives.get(0)!=null){
                                    for (Survive oldSurvice:oldSurvives){
                                        //判断班级
                                        if (newSurvive.getClassId().equals(oldSurvice.getClassId())){
                                            //班级相同
                                            //遍历档次
                                            List<SurviveGrade> oldGrades = oldSurvice.getSurviveGradeList();
                                            if (oldGrades!=null && oldGrades.size()!=0 && oldGrades.get(0)!=null){
                                                for (SurviveGrade oldSG:oldGrades){
                                                    //判断档次
                                                    if (newSG.getGradeName().equals(oldSG.getGradeName())){
                                                        //档次名称相等
                                                        ljs2 = oldSG.getCriticalRawCityNumList().size();

                                                        //获取学籍号集合
                                                        List<String> oldLjsNums = oldSG.getCriticalRawCityNumList();
                                                        //遍历学籍号
                                                        if (oldLjsNums!=null && oldLjsNums.size()!=0 && oldLjsNums.get(0)!=null){
                                                            for (String oldNum:oldLjsNums){
                                                                //学籍号相同  交集+1
                                                                if (newNum.equals(oldNum)){
                                                                    ljs1++;
                                                                    //找到相同学籍号 跳出循环
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        //跳出档次循环
                                                        break;
                                                    }
                                                }
                                            }

                                            //跳出班级循环
                                            break;
                                        }
                                    }
                                }

                            }
                        }

                        //成活率计算

                        //成活利率 = 交集 / 对比考试此档总人数
                        if (ljs2!=0){//0不作为被除数
                            BigDecimal bd1 = new BigDecimal(ljs1);
                            BigDecimal bd2 = new BigDecimal(ljs2);
                            //计算 精确到小数点后4位 四舍五入(向上取整)
                            BigDecimal result = bd1.divide(bd2, 4, BigDecimal.ROUND_HALF_UP);
                            //转换成百分比
                            DecimalFormat df = new DecimalFormat("0.00%");
                            newSG.setCriticalRaw(df.format(result));
                        }else {
                            newSG.setCriticalRaw("0.00%");
                        }

                        /*System.out.println("班级：" + newSurvive.getClassId() + "   本次考试档次："
                                + newSG.getGradeName() + "   市内交集个数：" + sn1 +
                                "   市内总数：" + sn2 + "   市内成活率:" + newSG.getInCity()
                                + "   室外交集个数："+sw1+ "    室外总数：" +sw2+ "   室外成活率:"+ newSG.getOutCity()
                                + "   本校升入交集个数："+bxsr1 + "    本校升入总数："+bxsr2+"    本校升入成活率："+ newSG.getTheSchool()
                        + "     临界生交集个数："+ljs1+"    临界生总数："+ljs2+"   临界生成活率:"+newSG.getCriticalRaw());
*/
                    }
                }
            }
        }

        System.out.println(newSurvives);

        //TODO:数据处理
        List<List<String>> listRow = new ArrayList<>();
        List<String> row = new ArrayList<>();
        int rowSize = 0;

        //表头第二行数据
        Set<String> headSet = new LinkedHashSet<>();

        for (Survive survive : newSurvives){
            row = new ArrayList<>();

            row.add(survive.getClassNumber()+"");
            row.add(survive.getClassType());
            row.add(survive.getClassLevel());
            row.add(survive.getClassHeadmaster());

            for (SurviveGrade surviveGrade :survive.getSurviveGradeList()){

                headSet.add(surviveGrade.getGradeName());

                row.add(surviveGrade.getInCity());
                row.add(surviveGrade.getOutCity());
                row.add(surviveGrade.getTheSchool());
                row.add(surviveGrade.getCriticalRaw());

            }

            listRow.add(row);
            if(row.size()>rowSize){
                rowSize = row.size();
            }

        }

        //表头第一行
        //查询两次考试名称
        List<Integer> examParentIds = new ArrayList<>();
        examParentIds.add(((Long)newExamParentId).intValue());
        examParentIds.add(((Long)oldExamParentId).intValue());
        Map map = new HashMap();
        map.put("examParentIds",examParentIds);
        List<String> examNames = scoreMapper.findExamNames(map);
        String[] excelHeader0 = { examNames.get(0)+"    ——  "+examNames.get(1)+"  成活率对比" };
        String[] headnum0 = { "0,0,0,"+ (rowSize-1)};

        //表头第二行数据
        List<String> head1 = new ArrayList<>();
        for (String s : headSet){
            head1.add(s);
            head1.add("");
            head1.add("");
            head1.add("");
        }

        List<String> excle1 = new ArrayList<>();
        excle1.add("班级");
        excle1.add("班级类型");
        excle1.add("班级层次");
        excle1.add("班主任");
        List<String> num1 = new ArrayList<>();
        num1.add("1,2,0,0");
        num1.add("1,2,1,1");
        num1.add("1,2,2,2");
        num1.add("1,2,3,3");
        for (int i = 0; i <head1.size() ; i++) {
            excle1.add(head1.get(i));
            if(head1.get(i).length()>0){
                num1.add("1,1,"+(4+i)+","+(4+i+3));
            }else {
                num1.add("1,1,"+(4+i)+","+(4+i));
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


        for (int i = 0; i <head1.size() ; i++) {
            if(head1.get(i).length()>0){
                excle2.add("市内");
                num2.add("2,2,"+(4+i)+","+(4+i));
                excle2.add("市外");
                num2.add("2,2,"+(4+i+1)+","+(4+i+1));
                excle2.add("本校升入");
                num2.add("2,2,"+(4+i+2)+","+(4+i+2));
                excle2.add("临界生");
                num2.add("2,2,"+(4+i+3)+","+(4+i+3));

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


        String fileName = "成活率统计";

        exportExcelUtil.exportExcel2(wb,0,"成活率统计",style2,headers,listRow,response,fileName);

    }
}
