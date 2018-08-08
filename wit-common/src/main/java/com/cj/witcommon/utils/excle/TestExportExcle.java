package com.cj.witcommon.utils.excle;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestExportExcle {


    public static void main(String[] args) throws Exception {

        new TestExportExcle().export();

    }
    @SuppressWarnings("deprecation")
    public void export() throws Exception {
        String path="D:/file/测试Eexle导出1.xls";
        // 声明String数组，并初始化元素（表头名称）
        //第一行表头字段，合并单元格时字段跨几列就将该字段重复几次
        String[] excelHeader0 = { "2018年高2018届5月月考成绩单" };
        //  “0,2,0,0”  ===>  “起始行，截止行，起始列，截止列”
        String[] headnum0 = { "0,0,0,43" };

        //第二行表头字段，其中的空的双引号是为了补全表格边框
        String[] excelHeader1 = {
                "姓名",
                "学籍号",
                "总分","","","","","",
                "语文","","","","","",
                "数学","","","","","",
                "英语","","","","","",
                "物理","","","","","",
                "化学","","","","","",
                "生物","","","","",
        };
        // 合并单元格
        String[] headnum1 = {
                "1,2,0,0",
                "1,2,1,1",
                "1,1,2,7",
                "1,1,8,13",
                "1,1,14,19",
                "1,1,20,25",
                "1,1,26,31",
                "1,1,32,37",
                "1,1,38,43"
        };

        //第三行表头字段
        String[] excelHeader2 = {

                "0","1",
                "分数","年级排名","年级进步","班级排名","班级进步","档次",
                "分数","年级排名","年级进步","班级排名","班级进步","档次",
                "分数","年级排名","年级进步","班级排名","班级进步","档次",
                "分数","年级排名","年级进步","班级排名","班级进步","档次",
                "分数","年级排名","年级进步","班级排名","班级进步","档次",
                "分数","年级排名","年级进步","班级排名","班级进步","档次",
                "分数","年级排名","年级进步","班级排名","班级进步","档次"
        };

        String[] headnum2 = {
                "2,2,0,0","2,2,1,1",
                "2,2,2,2", "2,2,3,3", "2,2,4,4", "2,2,5,5", "2,2,6,6", "2,2,7,7",
                "2,2,8,8", "2,2,9,9", "2,2,10,10", "2,2,11,11", "2,2,12,12", "2,2,13,13",
                "2,2,14,14", "2,2,15,15", "2,2,16,16", "2,2,17,17", "2,2,18,18", "2,2,19,19",
                "2,2,20,20", "2,2,21,21", "2,2,22,22", "2,2,23,23", "2,2,24,24", "2,2,25,25",
                "2,2,26,26", "2,2,27,27", "2,2,28,28", "2,2,29,29", "2,2,30,30", "2,2,31,31",
                "2,2,32,32", "2,2,33,33", "2,2,34,34", "2,2,35,35", "2,2,36,36", "2,2,37,37",
                "2,2,38,38", "2,2,39,39", "2,2,40,40", "2,2,41,41", "2,2,42,42", "2,2,43,43"
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
//        headers.add(header3);

        //数据
        List result = new ArrayList();
        List<String> lists = new ArrayList<>();
        lists.add("s电饭锅");
        lists.add("s后台");
        lists.add("s是否");
        lists.add("s电饭锅");
        lists.add("s士大夫");
        lists.add("s士大夫");
        lists.add("s士大夫");
        lists.add("sgh");
        lists.add("se");
        lists.add("ssd");
        lists.add("12");
        result.add(lists);
        result.add(lists);
        result.add(lists);

        OutputStream out = new FileOutputStream(path);


//        exportExcelUtil.exportExcel2(wb,0,"测试多表头导出",style2,headers,result,out);



    }
}
