package com.cj.witcommon.utils.common;

import com.cj.witcommon.utils.excle.ScoreModelTwoInfo;
import com.cj.witcommon.utils.excle.StudentScoreInfo;
import org.apache.xmlbeans.impl.regex.Match;

import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 分割字符串并返回
 * key : 教师名称
 * value：教师编号
 */
public class StringHandler {


    /**
     * 成绩导入
     */

    public static boolean equalsIs(String str){
        if(str == null) return false;
        if("/".equals(str)) return true;
        return false;
    }

    public static void changeTable(ScoreModelTwoInfo s){

    }




    /**
     *
     * @param grade
     * @return
     */
    public static int getGradeAge(String grade){
        if("".equals(grade) || grade == null) return -1;
        if("一年级".equals(grade)) return 1;
        if("二年级".equals(grade)) return 2;
        if("三年级".equals(grade)) return 3;
        if("四年级".equals(grade)) return 4;
        if("五年级".equals(grade)) return 5;
        if("六年级".equals(grade)) return 6;
        return -1;
    }


    public static String paramHandler(String str) {
        if ("".equals(str) || str == null) throw new RuntimeException("参数错误");
        String[] temp = str.split("/");
        // /参数判断，大于2，参数格式错误
        if (temp.length > 2 || temp.length < 0) throw new RuntimeException("参数错误");
//        Long val = Long.valueOf(temp[1]);
System.out.println(temp[1]);
        return temp[1];
    }




    public static Map<String, Object> paramToMap(HttpServletRequest request){
        Map<String, String[]> tempMap = request.getParameterMap();
        Map<String, Object> params = new HashMap<String, Object>(); //参数Map
        for(Map.Entry<String, String[]> entry : tempMap.entrySet()){ //遍历
            String key = entry.getKey();
            Object val = entry.getValue();
            params.put(key, val); //转换
        }
        return params;
    }


    /**
     * @param gradeName
     * @return 正则表达式，允许中文逗号，英文逗号，空格
     */
    public static List<String> gradeNameHandler(String gradeName){
        String[] strSplit=gradeName.split("(\\s*,\\s*|\\s*，\\s*|\\s+)");
        ArrayList<String> temp = new ArrayList<String>();
        for(String str : strSplit){
            temp.add(str);
System.out.println(str);
        }
        return temp;
    }


    /**
     * 正则表达式，分割中文（ 和 英文(
     * @param
     */

    public static String faceString(String subjectName){
        if("".equals(subjectName) || subjectName == null) return null;
        String[] strSplit = subjectName.split("(\\s*\\(\\s*|\\s*\\（\\s*|\\s+)");
        return strSplit[0];
    }


    /**
     * 计算毕业时间
     * 参数：学制,入学年度
     * @param
     */
    public static Date getEndTime(int periodSystem, String yeah){
        //拼接
        String tempTime = yeah + "-9-1";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date classYeah = null;
        try {

            classYeah = format.parse(tempTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar now = Calendar.getInstance();
        now.setTime(classYeah);
        now.add(Calendar.YEAR, periodSystem);
        //毕业时间6月
        now.add(Calendar.MONTH, -2);
        return now.getTime();
    }

    public static void main(String[] args){

        Date time = getEndTime(3, "2016");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(format.format(time));
    }


    /**
     * 学科成绩工具类
     * 1---语文
     * 2---数学
     * 3---英语
     * 4---艺术
     * ...扩展
     */
    public static List<String> returnSubjectName(StudentScoreInfo title){
        List<String> item = new ArrayList<String>();
        item.add(StringHandler.faceString(title.getChineseScore()));
        item.add(StringHandler.faceString(title.getMathScore()));
        item.add(StringHandler.faceString(title.getEnglishScore()));

        item.add(StringHandler.faceString(title.getPhysicalScore()));
        item.add(StringHandler.faceString(title.getChemistryScore()));
        item.add(StringHandler.faceString(title.getBiologicalScore()));
    //扩展.....
    //科目名
        return item;
    }

    public static List<String> returnSubjectName2(ScoreModelTwoInfo s){
        List<String> item = new ArrayList<String>();
        item.add(s.getScoreChinese());
        item.add(s.getScoreMatch());
        item.add(s.getScoreEnglish());
        item.add(s.getScorePhysical());
        item.add(s.getScoreChemistry());
        item.add(s.getScoreHistory());
        item.add(s.getScoreBbs());

        //扩展.....
        //科目名
        return item;
    }


    //班级排名
    public static List<String> saveSubjectRankClass(ScoreModelTwoInfo item){
        if(item == null) return null;
        List<String> subjectScore = new ArrayList<String>();
        subjectScore.add(item.getChineseClassRank());
        subjectScore.add(item.getMatchClassRank());
        subjectScore.add(item.getEnglishClassRank());
        subjectScore.add(item.getPhysicalClassRank());
        subjectScore.add(item.getChemistryClassRank());
        subjectScore.add(item.getHistoryClassRank());
        subjectScore.add(item.getBbsClassRank());
        return subjectScore;
    }

    //年级排名
    public static List<String> saveSubjectRankGrade(ScoreModelTwoInfo item){
        if(item == null) return null;
        List<String> subjectScore = new ArrayList<String>();
        //语文年级排名
        subjectScore.add(item.getChineseGradeRank());
        //数学年级排名
        subjectScore.add(item.getMatchGradeRank());
        //英语年级排名
        subjectScore.add(item.getEnglishGradeRank());
        //物理年级排名
        subjectScore.add(item.getPhysicalGradeRank());
        //化学年级排名
        subjectScore.add(item.getChineseClassRank());
        //历史年级排名
        subjectScore.add(item.getHistoryGradeRank());
        //政治年级排名
        subjectScore.add(item.getBbsGradeRank());
        return subjectScore;
    }


    public static List<BigDecimal> saveSubjectScore2(ScoreModelTwoInfo item){
        if(item == null) return null;
        List<BigDecimal> subjectScore = new ArrayList<BigDecimal>();

        //语文
        if("".equals(item.getScoreChinese()) || item.getScoreChinese() == null){
            //语文分数
            subjectScore.add(new BigDecimal("0"));
        }else{
            //语文分数
            subjectScore.add(new BigDecimal(item.getScoreChinese().trim()));
        }

        //数学
        if("".equals(item.getScoreMatch()) || item.getScoreMatch() == null){
            //数学分数
            subjectScore.add(new BigDecimal("0"));
        }else{
            //数学分数
            subjectScore.add(new BigDecimal(item.getScoreMatch().trim()));
        }

        //英语
        if("".equals(item.getScoreEnglish()) || item.getScoreEnglish() == null){
            //英语分数
            subjectScore.add(new BigDecimal("0"));
        }else{
            //英语分数
            subjectScore.add(new BigDecimal(item.getScoreEnglish().trim()));
        }

        //物理
        if("".equals(item.getScorePhysical()) || item.getScorePhysical() == null){
            //物理分数
            subjectScore.add(new BigDecimal("0"));
        }else{
            //物理分数
            subjectScore.add(new BigDecimal(item.getScorePhysical()));
        }

        //化学
        if("".equals(item.getScoreChemistry()) || item.getScoreChemistry() == null){
            //化学分数
            subjectScore.add(new BigDecimal("0"));
        }else{
            //化学分数
            subjectScore.add(new BigDecimal(item.getScoreChemistry().trim()));
        }

        //历史
        if("".equals(item.getScoreHistory()) || item.getScoreHistory() == null){
            //历史分数
            subjectScore.add(new BigDecimal("0"));
        }else{
            //历史分数
            subjectScore.add(new BigDecimal(item.getScoreHistory().trim()));
        }

        //政治
        if("".equals(item.getScoreBbs()) || item.getScoreHistory() == null){
            //历史分数
            subjectScore.add(new BigDecimal("0"));
        }else{
            //历史分数
            subjectScore.add(new BigDecimal(item.getScoreBbs().trim()));
        }
/*



        if("/".equals(item.getScoreChinese())){
            //语文分数
            subjectScore.add(new BigDecimal("0"));
            //数学分数
            subjectScore.add(new BigDecimal(item.getScoreMatch()));
            //英语分数
            subjectScore.add(new BigDecimal(item.getScoreEnglish()));
            //物理分数
            subjectScore.add(new BigDecimal(item.getScorePhysical()));
            //化学分数
            subjectScore.add(new BigDecimal(item.getScoreChemistry()));
            //历史分数
            subjectScore.add(new BigDecimal(item.getScoreHistory()));
            //政治
            subjectScore.add(new BigDecimal(item.getScoreBbs()));
        }else{


        }*/

/*
        //语文分数
        subjectScore.add(new BigDecimal(item.getScoreChinese().trim()));
        //数学分数
        subjectScore.add(new BigDecimal(item.getScoreMatch().trim()));
        //英语分数
        subjectScore.add(new BigDecimal(item.getScoreEnglish().trim()));
        //物理分数
        subjectScore.add(new BigDecimal(item.getScorePhysical().trim()));
        //化学分数
        subjectScore.add(new BigDecimal(item.getScoreChemistry().trim()));
        //历史分数
        subjectScore.add(new BigDecimal(item.getScoreHistory().trim()));
        //政治
        subjectScore.add(new BigDecimal(item.getScoreBbs().trim()));

        */
        return subjectScore;


    }

    public static List<BigDecimal> saveSubjectScore(StudentScoreInfo item){
        if(item == null) return null;
        List<BigDecimal> subjectScore = new ArrayList<BigDecimal>();
        //语文分数
        subjectScore.add(new BigDecimal(item.getChineseScore()));
        //数学分数
        subjectScore.add(new BigDecimal(item.getMathScore()));
        //英语分数
        subjectScore.add(new BigDecimal(item.getEnglishScore()));
        //物理分数
        subjectScore.add(new BigDecimal(item.getPhysicalScore()));
        //化学分数
        subjectScore.add(new BigDecimal(item.getChemistryScore()));
        //生物分数
        subjectScore.add(new BigDecimal(item.getBiologicalScore()));
//        subjectScore.add(new BigDecimal(item.getArtScore()));
        return subjectScore;
    }


    /**
     * 获取总分成绩
     * 提取括号内的内容,包括英文括号,和总问括号
     * @param managers
     * @return
     */
    public static List<String> getPatternList(String managers){
        List<String> ls=new ArrayList<String>();
        //提取
        Pattern pattern = Pattern.compile("(?<=\\()(.+?)(?=\\))|(?<=\\（)(.+?)(?=\\）)");
        Matcher matcher = pattern.matcher(managers);
        while(matcher.find())
            ls.add(matcher.group());
        return ls;
    }


    /**
     * 分值检查
     * @param scoreInfo
     * @return
     */
    public static boolean isRight(List<StudentScoreInfo> scoreInfo, StudentScoreInfo title){
//        //获取语文
//        String chinese = faceString(title.getChineseScore());
        //获取语文分数上限
        float bigChineseScore = Float.parseFloat(getPatternList(title.getChineseScore()).get(0));
//        //获取数学
//        String math = faceString(title.getMathScore());
        //获取数学分数上限
        float bigMathScore = Float.parseFloat(getPatternList(title.getMathScore()).get(0));
//        //获取英语
//        String english = faceString(title.getEnglishScore());
        //获取英语分数上限
        float bigEnglishScore = Float.parseFloat(getPatternList(title.getEnglishScore()).get(0));
//        //获取艺术
////        String art = faceString(title.getArtScore());
        //获取艺术分数上限
        float bigArtScore = Float.parseFloat(getPatternList(title.getEnglishScore()).get(0));

        //检查
        for(int i = 1, len = scoreInfo.size(); i < len; i++){
            //获取语文分数
            float chineseScore = Float.parseFloat(scoreInfo.get(i).getChineseScore());
            //获取数学分数
            float mathScore = Float.parseFloat(scoreInfo.get(i).getMathScore());
            //获取英语分数
            float englishScore = Float.parseFloat(scoreInfo.get(i).getEnglishScore());
            //获取英语分数
//            float artScore = Float.parseFloat(scoreInfo.get(i).getArtScore());
            //分数检测
            if(chineseScore > bigChineseScore) return true;
            if(mathScore > bigMathScore) return true;
            if(englishScore > bigEnglishScore) return true;
//            if(artScore > bigArtScore) return true;
        }
        return false;
    }


    /**
     * 时间戳转时间
     */
    public Data toDateS(Date time){
        return null;
    }

//
//    public static void main(String[] args) {
//
//        StudentScoreInfo info = new StudentScoreInfo();
//        info.setChineseScore("语文(150)");
//        info.setArtScore("艺术(150)");
//        info.setMathScore("数学（150）");
//        info.setEnglishScore("英语(150)");
//        System.out.println("haha ");
//        Field[] fields = info.getClass().getDeclaredFields();
//        String[] fieldName = new String[fields.length];
//
//        for(int i = 0; i < fields.length; i++){
//            System.out.println(fields[i].getName());
//        }
//
//        List<String> list = getTeacherList(info.getMathScore());
//
//        for(String s : list){
//            System.out.println(s);
//        }
//
//    }

}
