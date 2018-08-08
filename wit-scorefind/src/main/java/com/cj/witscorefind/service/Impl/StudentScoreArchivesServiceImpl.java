package com.cj.witscorefind.service.Impl;

import com.cj.witcommon.utils.excle.exportExcelUtil;
import com.cj.witscorefind.entity.ExamSubjectScore;
import com.cj.witscorefind.entity.SchoolExamParents;
import com.cj.witscorefind.mapper.ScoreMapper;
import com.cj.witscorefind.mapper.StudentScoreArchivesMapper;
import com.cj.witscorefind.service.StudentScoreArchivesService;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StudentScoreArchivesServiceImpl implements StudentScoreArchivesService {


    @Autowired
    private StudentScoreArchivesMapper studentScoreArchivesMapper;

    @Autowired
    private ScoreMapper scoreMapper;
    @Override
    public List<SchoolExamParents> findAllExamScore(String registerNumber) {
        Map map1 = new HashMap();
        map1.put("registerNumber",registerNumber);
        //查询此学生参加并且有成绩的考试
        List<SchoolExamParents> schoolExamParents =  scoreMapper.findAllExamByClassOrStudent(map1);

        List<Long> examParentIds = new ArrayList<>();
        //提取出考试父节点ID
        for (SchoolExamParents schoolExamParents1 : schoolExamParents){
            examParentIds.add(schoolExamParents1.getExamParentId());
        }

        map1.put("examParentIds",examParentIds);
        map1.put("subjectId","total");

        //根据考试父ID集合及学籍号查询学生总分
        List<SchoolExamParents> schoolExamParentsTotal = studentScoreArchivesMapper.findExamsStudentTotalScore(map1);

        //根据考试父ID集合及学籍号查询学生各科成绩
        List<SchoolExamParents> schoolExamParentsSubject = studentScoreArchivesMapper.findExamsStudentSubjectScore(map1);

        //查询该生 学段 班级
        Map<String,String> map = this.studentScoreArchivesMapper.findPeriodNameAndClassName(map1);



        for (SchoolExamParents schoolExamParents1 : schoolExamParentsTotal){

            schoolExamParents1.setPeriodName(map.get("period_name"));//添加学段
            schoolExamParents1.setClassName(map.get("class_name"));//添加班级

            for (SchoolExamParents schoolExamParents2 : schoolExamParentsSubject){

                if(schoolExamParents1.getExamParentId() == schoolExamParents2.getExamParentId()){
                    schoolExamParents1.setExamSubjectScores(schoolExamParents2.getExamSubjectScores());
                }
            }
        }

        return schoolExamParentsTotal;
    }

    //导出
    @Override
    public void exportAllExamScore(HttpSession session, HttpServletResponse response, String registerNumber) throws Exception {

        Map map1 = new HashMap();
        map1.put("registerNumber",registerNumber);
        //查询此学生参加并且有成绩的考试
        List<SchoolExamParents> schoolExamParents =  scoreMapper.findAllExamByClassOrStudent(map1);

        List<Long> examParentIds = new ArrayList<>();
        //提取出考试父节点ID
        for (SchoolExamParents schoolExamParents1 : schoolExamParents){
            examParentIds.add(schoolExamParents1.getExamParentId());
        }

        map1.put("examParentIds",examParentIds);
        map1.put("subjectId","total");

        //根据考试父ID集合及学籍号查询学生总分
        List<SchoolExamParents> schoolExamParentsTotal = studentScoreArchivesMapper.findExamsStudentTotalScore(map1);

        //根据考试父ID集合及学籍号查询学生各科成绩
        List<SchoolExamParents> schoolExamParentsSubject = studentScoreArchivesMapper.findExamsStudentSubjectScore(map1);

        for (SchoolExamParents schoolExamParents1 : schoolExamParentsTotal){

            for (SchoolExamParents schoolExamParents2 : schoolExamParentsSubject){

                if(schoolExamParents1.getExamParentId() == schoolExamParents2.getExamParentId()){
                    schoolExamParents1.setExamSubjectScores(schoolExamParents2.getExamSubjectScores());
                }
            }
        }

        //根据学籍号查询学生姓名
        String fullName = studentScoreArchivesMapper.findFullNameByRegisterNumber(registerNumber);

        //TODO:数据处理
        List<List<String>> listRow = new ArrayList<>();
        List<String> row = new ArrayList<>();


        //表头第二行数据
        Set<String> excle1 = new LinkedHashSet();
        excle1.add("考试名称");
        excle1.add("考试时间");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SchoolExamParents schoolExamParents0 : schoolExamParentsTotal){
            excle1.add(schoolExamParents0.getSubjectName());
            row = new ArrayList<>();
            row.add(schoolExamParents0.getExamName());
            row.add(sdf.format(schoolExamParents0.getCreateTime()));
            row.add(schoolExamParents0.getTotalScore()+"");
            for (ExamSubjectScore examSubjectScore : schoolExamParents0.getExamSubjectScores()){
                excle1.add(examSubjectScore.getSubjectName());
                row.add(examSubjectScore.getScore()+"");
            }
            listRow.add(row);
        }

        //表头第一行
        String[] header0 = { "姓名："+fullName+"  学籍号："+registerNumber+"  学习成绩档案" };
        String[] headNum0 = { "0,0,0,"+ (listRow.get(0).size()-1)};

        //表头第二行
        String[] header1 = excle1.toArray(new String[0]);
        List<String> num1 = new ArrayList<>();
        for (int i = 0; i <excle1.size()-1 ; i++) {
            num1.add("1,1,"+i+","+i);
        }
        String[] headNum1 = num1.toArray(new String[0]);


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

        Map<String,Object> head0 = new HashMap<>();
        head0.put("excelHeader",header0);
        head0.put("headnum",headNum0);
        head0.put("style",style);

        Map<String,Object> head1 = new HashMap<>();
        head1.put("excelHeader",header1);
        head1.put("headnum",headNum1);
        head1.put("style",style);


        headers.add(head0);
        headers.add(head1);


        String fileName = "学生学习成绩档案";

        exportExcelUtil.exportExcel2(wb,0,"学生学习成绩档案",style2,headers,listRow,response,fileName);

    }
}
