package com.cj.witscorefind.service.Impl;

import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witcommon.utils.excle.exportExcelUtil;
import com.cj.witscorefind.entity.ExamClassSubject;
import com.cj.witscorefind.entity.ShortlistedResp;
import com.cj.witscorefind.entity.TeacherClassCurse;
import com.cj.witscorefind.mapper.TeachingFileMapper;
import com.cj.witscorefind.service.TeachingFileService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教学档案
 * Created by XD on 2018/6/13.
 */
@Service
public class TeachingFileServiceImpl implements TeachingFileService {

    private static final Logger log = LoggerFactory.getLogger(TeachingFileServiceImpl.class);

    @Autowired
    private TeachingFileMapper teachingFileMapper;

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
     * 根据教职工编号查询所教班级和课程
     *
     * @param staffNumber
     * @return
     */
    @Override
    public ApiResult findTeachingFile(String staffNumber) {
        //返回结果
        ApiResult apiResult = new ApiResult();
        //根据教职工编号查询所教班级和课程
        List<TeacherClassCurse> list = this.teachingFileMapper.findTeachingFile(staffNumber,toLong());

        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(list);
        return apiResult;
    }



    /**
     * 查询单个班的单科每次考试平均分信息
     * @param map
     * @return
     */
    @Override
    public ApiResult findExamInfo(Pager pager) {
        //返回结果
        ApiResult apiResult = new ApiResult();
        //设置学校id
        Map<String, Object> map = pager.getParameters();
        map.put("schoolId", toLong());
        pager.setParameters(map);

        //查询这个班级的这个课程  分页后所有的考试父id
        List<Long> examParentIds = this.teachingFileMapper.findExamParentIds(pager);
        //查询总条数
        int total = this.teachingFileMapper.findExamParentIdsTotal(pager);
        pager.setRecordTotal(total);
        if (examParentIds!=null && examParentIds.size()!=0 && examParentIds.get(0)!=null){
            //把考试父id作为查询条件
            pager.getParameters().put("examParentIds",examParentIds);
            //查询这几次考试这个班的这个学科的平均分信息
            List<ExamClassSubject> list = this.teachingFileMapper.findExamClassSub(pager);
            pager.setContent(list);

        }
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(pager);


        return apiResult;
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
    public ApiResult teachingFileExport(HttpServletResponse response, Pager pager, HttpServletRequest request) {
        //查询数据
        ApiResult apiResult = findExamInfo(pager);
        Pager p = (Pager) apiResult.getData();
        List<ExamClassSubject> list = p.getContent();

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
            if (list != null && list.size() != 0) {
                for (ExamClassSubject info : list) {
                    if (info != null) {
                        List<String> stringList = new ArrayList<>();
                        //设置考试名称
                        if (info.getExamName()==null){
                            stringList.add("");
                        }else {
                            stringList.add(info.getExamName());
                        }
                        //设置考试时间
                        if (info.getExamTime()==null){
                            stringList.add("");
                        }else {
                            //截取时间 只要年月日
                            String time = info.getExamTime();
                            stringList.add(time.substring(0,10));
                        }
                        //设置平均分
                        if (info.getAvgScore()==null){
                            stringList.add("");
                        }else {
                            stringList.add(String.valueOf(info.getAvgScore()));
                        }
                        //设置最高分
                        if (info.getMaxScore()==null){
                            stringList.add("");
                        }else {
                            stringList.add(String.valueOf(info.getMaxScore()));
                        }
                        //设置最低分
                        if (info.getMinScore()==null){
                            stringList.add("");
                        }else {
                            stringList.add(String.valueOf(info.getMinScore()));
                        }
                        //设置年级排名
                        if (info.getRank()==null){
                            stringList.add("");
                        }else {
                            stringList.add(String.valueOf(info.getRank()));
                        }
                        dataHandler.add(stringList);
                    }
                }
            }


            //设置表头
            String[] excelHeader0 = { "姓名："+p.getParameters().get("name") + "   教职工编号：" + p.getParameters().get("adminId") +
            "   班级类型：" + p.getParameters().get("classType") + "   班级：" + p.getParameters().get("className") + "   教授课程：" + p.getParameters().get("curriculumName")};
            String[] headnum0 = { "0,0,0,"+ (dataHandler.get(0).size()-1)};

            //第二行
            //表头第二行
            List<String> excle1 = new ArrayList<>();
            excle1.add("        考试名称       ");
            excle1.add("        考试时间        ");
            excle1.add("              平均分              ");
            excle1.add("        最高分        ");
            excle1.add("        最低分        ");
            excle1.add("        年级排名        ");
            List<String> num1 = new ArrayList<>();
            num1.add("1,1,0,0");
            num1.add("1,1,1,1");
            num1.add("1,1,2,2");
            num1.add("1,1,3,3");
            num1.add("1,1,4,4");
            num1.add("1,1,5,5");

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
                //导出信息

                String fileName = "教学档案";
                exportExcelUtil.exportExcel2(wb,0,"教学档案",style2,headers,dataHandler,response,fileName);
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

