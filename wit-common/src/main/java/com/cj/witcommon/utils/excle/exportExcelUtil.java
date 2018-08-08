package com.cj.witcommon.utils.excle;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class exportExcelUtil {




    /**
     * 二、生成xlsx格式的表格
     只需要修改
     1、XSSFWorkbook
     2、XSSFFont
     3、XSSFRichTextString
     4、XSSFCellStyle
     5、XSSFRow
     成为：HSSFWorkbook 、HSSFFont、HSSFRichTextString、HSSFCellStyle、HSSFRow即可
     * @param workbook
     * @param sheetNum
     * @param sheetTitle
     * @param headers
     * @param result
     * @param out
     * @throws Exception
     */


    public static void exportExcel(XSSFWorkbook workbook, int sheetNum,String sheetTitle, String[] headers,
                                   List<List<String>> result,
                                   OutputStream out) throws Exception {
// 第一步，创建一个webbook，对应一个Excel以xsl为扩展名文件
        XSSFSheet sheet = workbook.createSheet();
        workbook.setSheetName(sheetNum, sheetTitle);
//设置列宽度大小
        sheet.setDefaultColumnWidth((short) 20);
//第二步， 生成表格第一行的样式和字体
//        XSSFCellStyle style = workbook.createCellStyle();
// 设置这些样式
//        style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
//        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
////        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
//        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
//        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
// 生成一个字体
        XSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.BLACK.index);
//设置字体所在的行高度
        font.setFontHeightInPoints((short) 20);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
// 把字体应用到当前的样式
//        style.setFont(font);
// 指定当单元格内容显示不下时自动换行
//        style.setWrapText(true);
// 产生表格标题行
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = row.createCell((short) i);
//            cell.setCellStyle(style);
            XSSFRichTextString text = new XSSFRichTextString(headers[i]);
            cell.setCellValue(text.toString());
        }
// 第三步：遍历集合数据，产生数据行，开始插入数据
        if (result != null) {
            int index = 1;
            for (List<String> m : result) {
                row = sheet.createRow(index);
                int cellIndex = 0;
                for (String str : m) {
                    XSSFCell cell = row.createCell((int) cellIndex);
                    cell.setCellValue(str.toString());
                    cellIndex++;
                }
                index++;
            }
        }
    }


    public static void exportExcel2(HSSFWorkbook wb,
                                    int sheetNum,
                                    String sheetTitle,
                                    HSSFCellStyle styles,
                                    List<Map<String,Object>> headers,
                                    List<List<String>> result,
                                    HttpServletResponse response,
                                    String fileName) throws Exception {

        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        OutputStream out = response.getOutputStream();
        response.setHeader("content-Type", "application/vnd.ms-excel;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "; filename*=utf-8''" + fileName);
        response.setCharacterEncoding("UTF-8");




// 第一步，创建一个webbook，对应一个Excel以xsl为扩展名文件
        HSSFSheet sheet = wb.createSheet();
        wb.setSheetName(sheetNum, sheetTitle);
        //设置列宽度大小
        sheet.setDefaultColumnWidth((short) 15*1);
        //设置行高
        sheet.setDefaultRowHeight((short)(25*20));





        //生成表头
        for (int n = 0; n <headers.size() ; n++) {
            //生成第n行
            HSSFRow row = sheet.createRow(n);
            //第n行样式
            HSSFCellStyle style = (HSSFCellStyle)headers.get(n).get("style");

            //第n行表头
            String[] excelHeader = (String[]) headers.get(n).get("excelHeader");

            for (int i = 0; i < excelHeader.length; i++) {

                sheet.autoSizeColumn(i, false);// 根据字段长度自动调整列的宽度
                HSSFCell cell = row.createCell(i+n);
//                cell.setCellValue(excelHeader[i]);
//                cell.setCellStyle(style);
//                if (i >= 0 && i < excelHeader.length) {
                for (int j = 0; j < excelHeader.length; j++) {

                    // 从第j列开始填充
                    cell = row.createCell(j);

                    // 填充excelHeader1[j]第j个元素
                    cell.setCellValue(excelHeader[j]);
                    cell.setCellStyle(style);
                }

//                }
            }
            //第n行动态合并单元格
            String[] headnum = (String[]) headers.get(n).get("headnum");

            for (int i = 0; i < headnum.length; i++) {

                sheet.autoSizeColumn(i, true);
                String[] temp = headnum[i].split(",");
                Integer startrow = Integer.parseInt(temp[0]);
                Integer overrow = Integer.parseInt(temp[1]);
                Integer startcol = Integer.parseInt(temp[2]);
                Integer overcol = Integer.parseInt(temp[3]);
                sheet.addMergedRegion(new CellRangeAddress(startrow, overrow, startcol, overcol));
            }






        }
        HSSFRow row = sheet.createRow(headers.size());
        //遍历集合数据，产生数据行，开始插入数据
        if (result != null) {
            int index = headers.size();
            for (List<String> m : result) {
                row = sheet.createRow(index);
                int cellIndex = 0;
                for (String str : m) {
                    if(index<100 && (index-headers.size())%7==0){
                        sheet.autoSizeColumn(cellIndex,true);

                    }
                    HSSFCell cell = row.createCell((int) cellIndex);
                    if(str ==null){
                        cell.setCellValue("");

                    }else {

                        cell.setCellValue(str.toString());
                    }
                    cell.setCellStyle(styles);

                    cellIndex++;
                }
                index++;
            }
        }
        System.out.println("==================================导出完成");

        wb.write(out);
        out.flush();
        out.close();
    }


    public void testPOI(){
        exportExcelUtil gwu=null;
        String path=null;
        try {
            gwu=new exportExcelUtil();
            path="D:/file/测试Eexle导出.xlsx";
//1、输出的文件地址及名称
            OutputStream out = new FileOutputStream(path);
//2、sheet表中的标题行内容，需要输入excel的汇总数据
            String[] summary = { "系统名称", "活动名称","门店号" ,"日报时间","发券数量","使用数量"};
            List<List<String>> summaryData = new ArrayList<List<String>>();
            List<SummaryInfo> _listSummary=new ArrayList<SummaryInfo>();

            for (SummaryInfo sum:_listSummary) {
                List<String> rowData = new ArrayList<String>();
                rowData.add(sum.getXtmc());
                rowData.add(sum.getHdmc());
                rowData.add(sum.getMdh());
                rowData.add(sum.getCreatTime());
                rowData.add(String.valueOf(sum.getHandoutTotal()));
                rowData.add(String.valueOf(sum.getUseTotal()));
                summaryData.add(rowData);
            }

            String[] water = { "系统名称", "门店号" ,"门店名称","小票号","活动编号"
                    ,"活动名称","发券数量","商品条码","商品名称","购买数量"
                    ,"发券时间","分类代码","是否领赠","数据是否为真"};
            List<List<String>> waterData = new ArrayList<List<String>>();
            List<GenerWater> _listWater=new ArrayList<GenerWater>();
//            for (GenerWater wat:_listWater) {
//                List<String> rowData = new ArrayList<String>();
//                rowData.add(wat.getXtmc());
//                rowData.add(wat.getMdh());
//                rowData.add(wat.getMdmc());
//                rowData.add(wat.getXph());
//                rowData.add(wat.getHdbh());
//                rowData.add(wat.getHdmc());
//                rowData.add(wat.getFqsl());
//                rowData.add(wat.getSptm());
//                rowData.add(wat.getSpmc());
//                rowData.add(wat.getSl());
//                rowData.add(wat.getFqsj());
//                rowData.add(wat.getFldm());
//                rowData.add(wat.getSflq());
//                rowData.add(wat.getReal());
//                waterData.add(rowData);
//            }
//3、生成格式是xlsx可存储103万行数据，如果是xls则只能存不到6万行数据
            XSSFWorkbook workbook = new XSSFWorkbook();
//第一个表格内容
            gwu.exportExcel(workbook, 0, "日报汇总", summary, summaryData, out);
//第二个表格内容
            gwu.exportExcel(workbook, 1, "部分流水数据", water, waterData, out);
//将所有的数据一起写入，然后再关闭输入流。
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
//        String path="D:/file/测试Eexle导出1.xls";
//        new exportExcelUtil().exportoExcel1(path);
//        exportExcelUtil e = new exportExcelUtil();
//        e.testPOI();

//        StudentBaseInfo studentBaseInfo = new StudentBaseInfo();
//        studentBaseInfo.setId(1);
//        studentBaseInfo.setName("卡德加");
//        studentBaseInfo.setAge(33);
//
//        try {
//            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
//            PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(studentBaseInfo);
//            for (int i = 0; i < descriptors.length; i++) {
//                String name = descriptors[i].getName();
//                if (!"class".equals(name)) {
//                    System.out.println(name+":"+ propertyUtilsBean.getNestedProperty(studentBaseInfo, name));
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

//    /** 带分类标题导出Excel的方法
//     *
//     * @param title
//     *            excel中的sheet名称
//     * @param header_2
//     *            两列的头的标题
//     * @param header_cate
//     *            分类列
//     * @param cate_num
//     *            分类列行数
//     * @param header_1
//     *            分类列行数
//     * @param columns
//     *                列名
//     * @param result
//     *            结果集
//     * @param out
//     *            输出流
//     * @param pattern
//     *            时间格式
//     */
//    @SuppressWarnings("deprecation")
//    public void exportoExcel1(String path) throws Exception {
//
//
////1、输出的文件地址及名称
//        OutputStream out = new FileOutputStream(path);
//
//        String title = "水质监测月报表";
//
//        //第一列标题
//        String[] header_col1={"姓名","学籍号"};
//
//        //第二行标题
//        String[] header_row2={"源水","出厂水","末梢水"};
//        int[] header_row2_num={5,5,4};
//        //第三行标题
//        String[] header_row3 = { "原水检验次数", "原水最大值", "原水最小值", "原水平均值", "出厂水检验次数", "出厂水最大值", "出厂水最小值", "出厂水平均值", "出厂水标准值", "出厂水检验次数", "管网末梢水最大值", "管网末梢水最小值", "管网末梢水平均值", "管网末梢水标准值"};
//
//        String[] columns = { "itmnm","numre", "count01", "tstv01Max", "tstv01Min", "tstv01Avg", "count02", "tstv02Max", "tstv02Min", "tstv02Avg", "ostnv", "count03", "tstv03Max", "tstv03Min", "tstv03Avg", "estnv"};
//
//        // 声明一个工作薄
//        HSSFWorkbook workbook = new HSSFWorkbook();
//        // 生成一个表格
//        HSSFSheet sheet = workbook.createSheet(title);
//        // 设置表格默认列宽度为20个字节
//        sheet.setDefaultColumnWidth((short) 15);
//        sheet.setDefaultRowHeight((short) (30*20));
//
//        // 生成一个样式
//        HSSFCellStyle style = workbook.createCellStyle();
//        // 设置单元格背景色，设置单元格背景色以下两句必须同时设置
//        style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index); // 设置填充色
//        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 设置填充样式
//
//        // 设置单元格上、下、左、右的边框线
//        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
//        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
//        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
//        // 设置居中
//        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        // 生成一个字体
//        HSSFFont font = workbook.createFont();
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//        // 把字体应用到当前的样式
//        style.setFont(font);
//
//        // 指定当单元格内容显示不下时自动换行
//        style.setWrapText(true);
//
//        // 声明一个画图的顶级管理器
//        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
//
//        // 以下可以用于设置导出的数据的样式
//        // 产生表格标题行
//        // 表头的样式
//        HSSFCellStyle titleStyle = workbook.createCellStyle();// 创建样式对象
//        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);// 水平居中
//        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
//        // 设置字体
//        HSSFFont titleFont = workbook.createFont(); // 创建字体对象
//        titleFont.setFontHeightInPoints((short) 15); // 设置字体大小
//        titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 设置粗体
//        // titleFont.setFontName("黑体"); // 设置为黑体字
//        titleStyle.setFont(titleFont);
//        //第0行
//        CellRangeAddress cra=new CellRangeAddress(0, (short) 0, 0, (short) (columns.length - 1));//参数:起始行号，终止行号， 起始列号，终止列号
//        sheet.addMergedRegion(cra);
//
//        Row row = sheet.createRow(0);
//        Cell row0=row.createCell(0);
//        row0.setCellValue(title);
//        row0.setCellStyle(titleStyle);
//
//        for(int i=0; i<header_col1.length; i++){
//            cra=new CellRangeAddress(1, 1, i, i);
//            sheet.addMergedRegion(cra);
//        }
//        int sum1 = 0;
//        int sum2 = 0;
//        for(int i=0; i<header_row2.length; i++){
//            sum1 += header_row2_num[i];
//            cra=new CellRangeAddress(1, 1, 2+sum2, 2-1+sum1);   //
//            sheet.addMergedRegion(cra);
//            sum2 += header_row2_num[i];
//        }
//
//        //第1行
//        row = sheet.createRow(1);
//
//        for(int i=0; i<header_col1.length; i++){
//            final Cell cell = row.createCell(i);
//            cell.setCellStyle(style);
//            cell.setCellValue(header_col1[i]);
//        }
//
//        int sum = 0;
//        for(int i=0; i<header_row2.length; i++){
//            final Cell cell = row.createCell(1+sum);    //
//            cell.setCellStyle(style);
//            cell.setCellValue(header_row2[i]);
//            sum += header_row2_num[i];
//        }
//
//
//        //第2行
//        row = sheet.createRow(2);
//        for(int i=0; i<header_row3.length; i++){
//            final Cell cell = row.createCell(i+1);  //
//            cell.setCellStyle(style);
//            cell.setCellValue(header_row3[i]);
//        }
//
//// 遍历集合数据，产生数据行
//        if (result != null) {
//            int index = 3;
//            for (T t : result) {
//                row = sheet.createRow(index);
//                index++;
//                for (short i = 0; i < columns.length; i++) {
//                    Cell cell = row.createCell(i);
//                    String fieldName = columns[i];
//                    String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
//                    Class tCls = t.getClass();
//                    Method getMethod = tCls.getMethod(getMethodName, new Class[] {});
//                    Object value = getMethod.invoke(t, new Class[] {});
//                    String textValue = null;
//                    if (value == null) {
//                        textValue = "";
//                    } else if (value instanceof Date) {
//                        Date date = (Date) value;
//                        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
//                        textValue = sdf.format(date);
//                    } else if (value instanceof byte[]) {
//                        // 有图片时，设置行高为60px;
//                        row.setHeightInPoints(60);
//                        // 设置图片所在列宽度为80px,注意这里单位的一个换算
//                        sheet.setColumnWidth(i, (short) (35.7 * 80));
//                        // sheet.autoSizeColumn(i);
//                        byte[] bsValue = (byte[]) value;
//                        HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 6, index, (short) 6,
//                                index);
//                        anchor.setAnchorType(2);
//                        patriarch.createPicture(anchor, workbook.addPicture(bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
//                    } else {
//                        // 其它数据类型都当作字符串简单处理
//                        textValue = value.toString();
//                    }
//                    if (textValue != null) {
//                        Pattern p = Pattern.compile("^//d+(//.//d+)?$");
//                        Matcher matcher = p.matcher(textValue);
//                        if (matcher.matches()) {
//                            // 是数字当作double处理
//                            cell.setCellValue(Double.parseDouble(textValue));
//                        } else {
//                            HSSFRichTextString richString = new HSSFRichTextString(textValue);
//                            cell.setCellValue(richString);
//                        }
//                    }
//                }
//            }
//        }
//
//        workbook.write(out);
//        out.flush();
//        out.close();
//    }


}




