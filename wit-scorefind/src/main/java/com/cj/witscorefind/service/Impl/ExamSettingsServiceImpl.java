package com.cj.witscorefind.service.Impl;

import com.cj.witbasics.entity.*;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.entity.ApiResultUtil;
import com.cj.witcommon.entity.SchoolClassInfo;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witcommon.utils.excle.ImportExeclUtil;
import com.cj.witcommon.utils.excle.StudentStatistics;
import com.cj.witscorefind.entity.*;
import com.cj.witscorefind.entity.Grade;
import com.cj.witscorefind.entity.SchoolExamGrade;
import com.cj.witscorefind.mapper.ExamSettingsMapper;
import com.cj.witscorefind.service.ExamSettingsService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * 考试参数设置
 * Created by XD on 2018/6/2.
 */
@Service
@Transactional
public class ExamSettingsServiceImpl implements ExamSettingsService {

    @Autowired
    private ExamSettingsMapper examSettingsMapper;

    @Value("${school_id}")
    private String schoolId;

    /**
     * 转为Long
     *
     * @return
     */
    private Long toLong() {
        return Long.valueOf(this.schoolId);
    }


    /**
     * 查询所有考试名称和id
     *
     * @return
     */
    @Override
    public ApiResult findExamName() {
        ApiResult apiResult = new ApiResult();
        //查询结果集
        try {
            List<SchoolExamParent> list = this.examSettingsMapper.findExamName();
            apiResult.setCode(ApiCode.SUCCESS);
            apiResult.setMsg(ApiCode.SUCCESS_MSG);
            apiResult.setData(list);
        } catch (Exception e) {
            //失败
            e.printStackTrace();
            apiResult.setCode(ApiCode.FAIL);
            apiResult.setMsg(ApiCode.FAIL_MSG);
        }
        return apiResult;
    }

    /**
     * 查询考试信息 模糊条件
     *
     * @param p
     * @return
     */
    @Override
    public ApiResult findExamByVague(Pager p) {
        //返回结果
        ApiResult apiResult = new ApiResult();
        //设置学校id
        Map<String, Object> map = p.getParameters();
        map.put("schoolId", toLong());
        try {
            //查询结果集
            List list = this.examSettingsMapper.findExamByVague(p);
            //计数
            int count = this.examSettingsMapper.findExamByVagueTotal(p);
            if (list != null) {
                p.setContent(list);
            }
            p.setRecordTotal(count);
            apiResult.setCode(ApiCode.SUCCESS);
            apiResult.setMsg(ApiCode.SUCCESS_MSG);
            apiResult.setData(p);
        } catch (Exception e) {
            //失败
            e.printStackTrace();
            apiResult.setCode(ApiCode.FAIL);
            apiResult.setMsg(ApiCode.FAIL_MSG);
        }

        return apiResult;
    }

    /**
     * 查询这次考试的班级和考试课程
     *
     * @param p
     * @return
     */
    @Override
    public ApiResult findExamClass(Pager p) {
        //返回结果
        ApiResult apiResult = new ApiResult();
        //设置学校id
        Map<String, Object> map = p.getParameters();
        map.put("schoolId", toLong());
        try {
            //查询结果集
            List<SchoolClassInfo> list = this.examSettingsMapper.findExamClass(p);
            //计数
            int count = this.examSettingsMapper.findExamClassTotal(p);
            //封装
            p.setContent(list);
            p.setRecordTotal(count);
            apiResult.setCode(ApiCode.SUCCESS);
            apiResult.setMsg(ApiCode.SUCCESS_MSG);
            apiResult.setData(p);

        } catch (Exception e) {
            //失败
            e.printStackTrace();
            apiResult.setCode(ApiCode.FAIL);
            apiResult.setMsg(ApiCode.FAIL_MSG);
        }
        return apiResult;
    }

    /**
     * 各班单科参数设置
     *
     * @param e
     * @return
     */
    @Override
    public ApiResult addParametersBySubject(ExamSettingReq e, HttpServletRequest request) {
        //返回结果
        ApiResult apiResult = new ApiResult();
        //设置学校id
        e.setSchoolId(toLong());
        //获取创建人id
        Long adminId = (Long) request.getSession().getAttribute("adminId");
        //获取当前时间
        Date nowDate = new Date();

        //遍历各班
        List<Long> classIds = e.getClassIds();
        for (Long classId : classIds) {
            //查重（school_exam_grade表）
            SchoolExamGrade schoolExamGrade = this.examSettingsMapper.findSchoolExamGrade(e, classId);
            if (schoolExamGrade != null) {
                //修改参数（school_exam_grade表）
                schoolExamGrade.setOperatorId(adminId);
                schoolExamGrade.setDegree(e.getDegree());
                schoolExamGrade.setAdvantageScore(e.getAdvantageScore());
                //更新school_exam_grade表
                this.examSettingsMapper.updateSchoolExamGrade(schoolExamGrade);
                //根据id删除grade表信息
                this.examSettingsMapper.deleteGradeById(schoolExamGrade);
                //重新添加
                if (e.getGradeList() != null && e.getGradeList().size() != 0 && e.getGradeList().get(0) != null) {
                    //遍历 每一条就是要插入的一条记录
                    for (Map g : e.getGradeList()) {
                        //设置grade参数集合
                        Grade grade = new Grade();
                        grade.setExamGradeId(schoolExamGrade.getExamGradeId());
                        grade.setGradeType((String) g.get("type"));
                        grade.setGradeName((String) g.get("gradeName"));
                        String numMax = (String) g.get("numMax");
                        grade.setNumMax(Integer.parseInt(numMax));
                        String numMin = (String) g.get("numMin");
                        grade.setNumMin(Integer.parseInt(numMin));
                        //grade.setNumScore(e.getNumScore());
                        //设置任务数
                        if (e.getTaskList() != null && e.getTaskList().size() != 0 && e.getTaskList().get(0) != null) {
                            for (Map task : e.getTaskList()) {
                                //判断是否当前循环的班级
                                Integer c = (Integer) task.get("classId");

                                if (Long.valueOf(c).equals(classId)) {
                                    List<Map> list = (List<Map>) task.get("info");
                                    //判断档次
                                    for (Map m : list) {
                                        if (m.get("gradeName").equals(g.get("gradeName"))) {
                                            //设置任务数
                                            String taskNum = (String) m.get("taskNum");
                                            grade.setNumTask(new BigDecimal(taskNum));
                                            //设置分数线
                                            String numScore = (String) m.get("numScore");
                                            grade.setNumScore(new BigDecimal(numScore));
                                            break;
                                        }

                                    }
                                }
                            }
                        }
                        //判断类型是否为分数档次  过滤掉名词档次
                        if (!"1".equals(grade.getGradeType())) {
                            grade.setNumScore(null);
                            grade.setNumTask(null);
                        }
                        //插入grade信息
                        int j = this.examSettingsMapper.insertGrade(grade);

                        apiResult.setCode(ApiCode.SUCCESS);
                        apiResult.setMsg(ApiCode.SUCCESS_MSG);
                    }
                }

            } else {
                //添加
                //设置参数
                SchoolExamGrade seg = new SchoolExamGrade();
                seg.setSchoolId(e.getSchoolId());
                seg.setExamParentId(e.getExamParentId());
                seg.setClassPeriodId(e.getPeriodId());
                seg.setThetime(e.getThetime());
                seg.setClassId(classId);
                seg.setExamSubject(e.getSubjectName());
                seg.setGradeType("3");
                seg.setFounderId(adminId);
                seg.setCreateTime(nowDate);
                seg.setDegree(e.getDegree());
                seg.setAdvantageScore(e.getAdvantageScore());
                //根据id 查询 examId classTypeId subjectId
                Map<String, Object> map = this.examSettingsMapper.findClassInfo(seg);
                seg.setExamSubjectId((Long) map.get("subject_id"));
                seg.setClassTypeId((Integer) map.get("class_type_id"));
                seg.setExamId((Long) map.get("exam_id"));

                //插入参数基础信息(school_exam_grade表)  主键返回
                int i = this.examSettingsMapper.insertExamGrade(seg);
                if (i != 0) {
                    //插入成功
                    //返回主键
                    Long examGradeId = seg.getExamGradeId();

                    if (e.getGradeList() != null && e.getGradeList().size() != 0 && e.getGradeList().get(0) != null) {
                        //遍历 每一条就是要插入的一条记录
                        for (Map g : e.getGradeList()) {
                            //设置grade参数集合
                            Grade grade = new Grade();
                            grade.setExamGradeId(examGradeId);
                            grade.setGradeType((String) g.get("type"));
                            grade.setGradeName((String) g.get("gradeName"));
                            String numMax = (String) g.get("numMax");
                            grade.setNumMax(Integer.parseInt(numMax));
                            String numMin = (String) g.get("numMin");
                            grade.setNumMin(Integer.parseInt(numMin));
                            //grade.setNumScore(e.getNumScore());
                            //设置任务数
                            if (e.getTaskList() != null && e.getTaskList().size() != 0 && e.getTaskList().get(0) != null) {
                                for (Map task : e.getTaskList()) {
                                    //判断是否当前循环的班级
                                    Integer c = (Integer) task.get("classId");
                                    if (c.equals(classId.intValue())) {
                                        List<Map> list = (List<Map>) task.get("info");
                                        //判断档次
                                        for (Map m : list) {
                                            if (m.get("gradeName").equals(g.get("gradeName"))) {
                                                //设置任务数
                                                String taskNum = (String) m.get("taskNum");
                                                grade.setNumTask(new BigDecimal(taskNum));
                                                //设置分数线
                                                String numScore = (String) m.get("numScore");
                                                grade.setNumScore(new BigDecimal(numScore));
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            //判断类型是否为分数档次  过滤掉名词档次
                            if (!"1".equals(grade.getGradeType())) {
                                grade.setNumScore(null);
                                grade.setNumTask(null);
                            }
                            //插入grade信息
                            int j = this.examSettingsMapper.insertGrade(grade);
                        }
                    }
                    apiResult.setCode(ApiCode.SUCCESS);
                    apiResult.setMsg(ApiCode.SUCCESS_MSG);

                } else {
                    //插入失败
                    apiResult.setCode(ApiCode.FAIL);
                    apiResult.setMsg(ApiCode.FAIL_MSG);
                }

            }
        }

        return apiResult;
    }

    /**
     * 总分参数设置,整个年级的文科或理科的总分，可以设置难度系数，档次设置，总分分数线
     *
     * @param e
     * @param request
     * @return
     */
    @Override
    public ApiResult addParametersByTotal(ExamSettingReq e, HttpServletRequest request) {
        //返回结果
        ApiResult apiResult = new ApiResult();
        //设置学校id
        e.setSchoolId(toLong());
        //获取创建人id
        Long adminId = (Long) request.getSession().getAttribute("adminId");
        //获取当前时间
        Date nowDate = new Date();

        //查重
        SchoolExamGrade schoolExamGrade = this.examSettingsMapper.findExamByTotal(e);
        if (schoolExamGrade != null) {
            //更新
            //修改参数（school_exam_grade表）
            schoolExamGrade.setOperatorId(adminId);
            schoolExamGrade.setDegree(e.getDegree());
            //更新school_exam_grade表
            this.examSettingsMapper.updateSchoolExamGrade(schoolExamGrade);
            //根据id删除grade表信息
            this.examSettingsMapper.deleteGradeById(schoolExamGrade);
            //重新添加
            for (Map map : e.getGradeList()) {
                //设置grade参数
                map.put("examGradeId", schoolExamGrade.getExamGradeId());
                this.examSettingsMapper.insertGradeByMap(map);
            }
            apiResult.setCode(ApiCode.SUCCESS);
            apiResult.setMsg(ApiCode.SUCCESS_MSG);
        } else {
            //添加
            //参数设置
            SchoolExamGrade seg = new SchoolExamGrade();
            seg.setSchoolId(e.getSchoolId());
            seg.setExamParentId(e.getExamParentId());
            seg.setClassPeriodId(e.getPeriodId());
            seg.setThetime(e.getThetime());
            seg.setGradeType("1");
            seg.setFounderId(adminId);
            seg.setCreateTime(nowDate);
            seg.setDegree(e.getDegree());
            seg.setClassTypeId(e.getClassTypeId());
            seg.setExamSubject("总分");
            //插入参数基础信息(school_exam_grade表)  主键返回
            int i = this.examSettingsMapper.insertExamGrade(seg);
            if (i != 0) {
                //成功  主键返回
                Long examGradeId = seg.getExamGradeId();
                //插入grade表信息
                for (Map map : e.getGradeList()) {
                    //设置grade参数
                    map.put("examGradeId", examGradeId);
                    this.examSettingsMapper.insertGradeByMap(map);
                }
                apiResult.setCode(ApiCode.SUCCESS);
                apiResult.setMsg(ApiCode.SUCCESS_MSG);
            } else {
                //插入失败
                apiResult.setCode(ApiCode.FAIL);
                apiResult.setMsg(ApiCode.FAIL_MSG);
            }
        }
        return apiResult;
    }

    /**
     * 功能描述：按照年级（分类型）查询总分档次设置信息
     *
     * @param e
     * @param request
     * @return
     */
    @Override
    public ApiResult findGradeByTotal(ExamSettingReq e, HttpServletRequest request) {
        //返回结果
        ApiResult apiResult = new ApiResult();
        //设置学校id
        e.setSchoolId(toLong());
        //查询
        List<Grade> list = this.examSettingsMapper.findGradeByTotal(e);
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(list);
        return apiResult;
    }

    /**
     * 查询这次考试的学段和届次
     *
     * @param e
     * @param request
     * @return
     */
    @Override
    public ApiResult findThetimeByExam(ExamSettingReq e, HttpServletRequest request) {
        //返回结果
        ApiResult apiResult = new ApiResult();
        //设置学校id
        e.setSchoolId(toLong());
        List<PeriodThetime> list = this.examSettingsMapper.findThetimeByExam(e);
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        apiResult.setData(list);
        return apiResult;
    }

    /**
     * 班级总分各档任务数设置
     *
     * @param e
     * @param request
     * @return
     */
    @Override
    public ApiResult addParametersByTask(ExamSettingReq e, HttpServletRequest request) {
        //返回结果
        ApiResult apiResult = new ApiResult();
        //设置学校id
        e.setSchoolId(toLong());
        //获取创建人id
        Long adminId = (Long) request.getSession().getAttribute("adminId");
        //获取当前时间
        Date nowDate = new Date();
        //查询总分档次设置信息
        List<Grade> list = this.examSettingsMapper.findGradeByTotal(e);
        if (list == null || list.size() == 0 || list.get(0) == null) {
            apiResult.setCode(ApiCode.FAIL);
            apiResult.setMsg(ApiCode.FAIL_MSG);
            return apiResult;
        }
        //遍历班级id
        for (Long classId : e.getClassIds()) {
            //查重
            SchoolExamGrade schoolExamGrade = this.examSettingsMapper.findExamByClass(e, classId);
            if (schoolExamGrade != null) {
                //更新
                //修改参数（school_exam_grade表）
                schoolExamGrade.setOperatorId(adminId);
                schoolExamGrade.setDegree(e.getDegree());
                //更新school_exam_grade表
                this.examSettingsMapper.updateSchoolExamGrade(schoolExamGrade);
                //根据id删除grade表信息
                this.examSettingsMapper.deleteGradeById(schoolExamGrade);
                //重新添加
                for (Map task : e.getTaskList()) {
                    //判断是否当前班级
                    Integer c = (Integer) task.get("classId");
                    if (c.equals(classId.intValue())) {
                        //获取详细信息
                        List<Map> list1 = (List<Map>) task.get("info");
                        for (Map m : list1) {
                            //创建插入对象
                            Grade grade = new Grade();
                            grade.setExamGradeId(schoolExamGrade.getExamGradeId());
                            for (Grade g : list) {
                                //判断属于哪个档次
                                if (g.getGradeName().equals(m.get("gradeName"))) {
                                    //设置档次名称
                                    grade.setGradeName((String) m.get("gradeName"));
                                    //设置档次种类 分数or名次
                                    grade.setGradeType((String) m.get("type"));
                                    //设置分数档次
                                    grade.setNumMin(g.getNumMin());
                                    grade.setNumMax(g.getNumMax());
                                    grade.setNumScore(g.getNumScore());
                                    //设置任务数
                                    String taskNum = (String) m.get("taskNum");
                                    grade.setNumTask(new BigDecimal(taskNum));
                                    break;
                                }
                            }
                            //插入grade信息
                            int j = this.examSettingsMapper.insertGrade(grade);
                        }
                    }
                }
            } else {
                //添加
                //参数设置
                SchoolExamGrade seg = new SchoolExamGrade();
                seg.setSchoolId(e.getSchoolId());
                seg.setExamParentId(e.getExamParentId());
                seg.setClassPeriodId(e.getPeriodId());
                seg.setThetime(e.getThetime());
                seg.setGradeType("2");
                seg.setFounderId(adminId);
                seg.setCreateTime(nowDate);
                seg.setClassTypeId(e.getClassTypeId());
                seg.setExamSubject("总分");
                seg.setClassId(classId);
                //插入参数基础信息(school_exam_grade表)  主键返回
                int i = this.examSettingsMapper.insertExamGrade(seg);
                if (i != 0) {
                    //插入成功
                    //返回主键
                    Long examGradeId = seg.getExamGradeId();
                    for (Map task : e.getTaskList()) {
                        //判断是否当前班级
                        Integer c = (Integer) task.get("classId");
                        if (c.equals(classId.intValue())) {
                            //获取详细信息
                            List<Map> list1 = (List<Map>) task.get("info");
                            for (Map m : list1) {
                                //创建插入对象
                                Grade grade = new Grade();
                                grade.setExamGradeId(examGradeId);
                                for (Grade g : list) {
                                    //判断属于哪个档次
                                    if (g.getGradeName().equals(m.get("gradeName"))) {
                                        //设置档次名称
                                        grade.setGradeName((String) m.get("gradeName"));
                                        //设置档次种类 分数or名次
                                        grade.setGradeType((String) m.get("type"));
                                        //设置分数档次
                                        grade.setNumMin(g.getNumMin());
                                        grade.setNumMax(g.getNumMax());
                                        grade.setNumScore(g.getNumScore());
                                        //设置任务数
                                        String taskNum = (String) m.get("taskNum");
                                        grade.setNumTask(new BigDecimal(taskNum));
                                        break;
                                    }
                                }
                                //插入grade信息
                                int j = this.examSettingsMapper.insertGrade(grade);
                            }
                        }
                    }
                }
            }
        }
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        return apiResult;
    }

    /**
     * 设置年级单科分数线
     *
     * @param e
     * @param request
     * @return
     */
    @Override
    public ApiResult addParametersScore(ExamSettingReq e, HttpServletRequest request) {
        //返回结果
        ApiResult apiResult = new ApiResult();

        //获取创建人id
        Long adminId = (Long) request.getSession().getAttribute("adminId");
        //基础参数设置
        SchoolExamGrade schoolExamGrade = new SchoolExamGrade();
        schoolExamGrade.setSchoolId(toLong());
        schoolExamGrade.setExamParentId(e.getExamParentId());
        schoolExamGrade.setClassPeriodId(e.getPeriodId());
        schoolExamGrade.setThetime(e.getThetime());
        schoolExamGrade.setClassTypeId(e.getClassTypeId());
        schoolExamGrade.setGradeType("4");
        schoolExamGrade.setFounderId(adminId);
        schoolExamGrade.setCreateTime(new Date());
        //遍历  每一条就是要插入的信息
        for (Map map : e.getGradeList()) {
            //设置subjectId
            String subjectId = (String) map.get("subjectId");
            schoolExamGrade.setExamSubjectId(Long.valueOf(subjectId));
            //查重
            SchoolExamGrade seg = this.examSettingsMapper.findExamByScore(schoolExamGrade);
            if (seg != null) {
                //更新
                seg.setOperatorId(adminId);
                String advantageScore = (String) map.get("advantageScore");
                Double sore = Double.valueOf(advantageScore);
                seg.setAdvantageScore(BigDecimal.valueOf(sore));
                //更新
                this.examSettingsMapper.updateSchoolExamGrade(seg);
            } else {
                //添加
                //课程名称，优势分数线
                schoolExamGrade.setExamSubject((String) map.get("subjectName"));
                String advantageScore = (String) map.get("advantageScore");
                Double sore = Double.valueOf(advantageScore);
                schoolExamGrade.setAdvantageScore(BigDecimal.valueOf(sore));
                //添加
                this.examSettingsMapper.insertExamGrade(schoolExamGrade);
            }
        }
        apiResult.setCode(ApiCode.SUCCESS);
        apiResult.setMsg(ApiCode.SUCCESS_MSG);
        return apiResult;
        }


    /**
     * 考试参数导入
     * @param file
     * @param in
     * @param operatorId
     * @return
     */
    @Override
    public ApiResult examImportInfo(MultipartFile file, InputStream in, Long operatorId) {
        ApiResult apiResult = new ApiResult();
        Date createTime = new Date();//创建的时间
        Long schoolId = toLong();//学校id
        //返回提示信息
        StringBuffer msg = new StringBuffer();

        try{
            String fileName = file.getOriginalFilename(); //获取文件名
            Workbook workbook = ImportExeclUtil.chooseWorkbook(fileName, in);
            int sheets = workbook.getNumberOfSheets(); //获取sheet数量

            //读取第一个sheet的第二行第1,2,3，获取考试名称，学段，届次
            ImportExamName exmaName = new ImportExamName();//创建对象
            ImportExamName readDateT1 =
                    ImportExeclUtil.readDateT(workbook, exmaName, in, 0, new Integer[]{2, 1},new Integer[]{2, 2},new Integer[]{2, 3});
            //根据考试名称查询考试父节点信息
            SchoolExamParent schoolExamParent = this.examSettingsMapper.findExamParentByName(exmaName.getExamName());
            if (schoolExamParent==null){
                //考试名称不存在
                apiResult.setCode(ApiCode.import_failed);
                apiResult.setData(exmaName);
                apiResult.setMsg("考试名称：" + exmaName.getExamName() + " 不存在，无法导入");
                return apiResult;
            }
            //根据学段名称查询学段信息
            SchoolPeriod schoolPeriod = this.examSettingsMapper.findPeriodByName(exmaName.getPeriodName(),schoolId);
            if (schoolPeriod==null){
                //学段名称不存在
                apiResult.setCode(ApiCode.import_failed);
                apiResult.setData(exmaName);
                apiResult.setMsg("学段名称：" + exmaName.getPeriodName() + " 不存在，无法导入");
                return apiResult;
            }

            //查询学段下是否包含此届次
            SchoolPeriodClassThetime spct = this.examSettingsMapper.findThetimeByPeriodId(schoolPeriod.getPeriodId(),
                    schoolId,exmaName.getThetime());
            if (spct==null){
                //学段下不包含此届次
                apiResult.setCode(ApiCode.import_failed);
                apiResult.setData(exmaName);
                apiResult.setMsg("届次名称：" + exmaName.getThetime() + " 学段下不包含此届次，无法导入");
                return apiResult;
            }

            //查询本次考试当前学段是否参加
            SchoolExam schoolExam = this.examSettingsMapper.findExamBySubName(schoolPeriod.getPeriodId(),exmaName.getThetime(),
                    schoolId,schoolExamParent.getExamParentId());
            if (schoolExam == null){
                //本次考试当前学段未参加
                apiResult.setCode(ApiCode.import_failed);
                apiResult.setData(exmaName);
                apiResult.setMsg("届次：" + exmaName.getThetime() + " 本次考试不包含此届次，无法导入");
                return apiResult;
            }



//////////////////////////////////单科设置///////////////////////////////////////////////////////////////////////

            //读取第一个sheet的第五行1，2，3列，获取考试课程，优势分数线，难度系数
            ImportExamSubject importExamSubject = new ImportExamSubject();
            try {
                ImportExamSubject readDateT2 =
                        ImportExeclUtil.readDateT(workbook, importExamSubject, in, 0,
                                new Integer[]{5, 1},new Integer[]{5, 2},new Integer[]{5, 3});
            }catch (Exception e){
                //优势分数线输入错误，无法 转换为 BigDecimal
                apiResult.setCode(ApiCode.import_failed);
                apiResult.setMsg("优势分数线输入错误，无法导入");
                return apiResult;
            }
            //输入了课程   进单科参数设置
            if (importExamSubject.getSubjectName()!=null && !"".equals(importExamSubject.getSubjectName()))
            {

                //读取第一个sheet的第7行开始读取
                ImportExamSubInfo SubInfo = new ImportExamSubInfo();
                List<ImportExamSubInfo> readBaseInfo = ImportExeclUtil.readDateListT(workbook, SubInfo, 7, 0, 0);

                //取出classId
                Set<Integer> classIds = new HashSet();
                for (ImportExamSubInfo info : readBaseInfo){
                    //设置参数
                    info.setExamParentId(schoolExamParent.getExamParentId());
                    info.setSchoolId(toLong());
                    info.setPeriodId(schoolPeriod.getPeriodId());
                    info.setThetime(exmaName.getThetime());
                    info.setAdvantageScore(importExamSubject.getAdvantageScore());
                    info.setDegree(importExamSubject.getDegree());

                    if (info.getClassNumber()!=null && !"".equals(info.getClassNumber())) {
                        Integer classId = this.examSettingsMapper.findClassIdByNum(info);
                        if (classId == null) {
                            //该届次下无此班号
                            apiResult.setCode(ApiCode.import_failed);
                            apiResult.setData(info);
                            apiResult.setMsg("班号：" + info.getClassNumber() + " 该届次下无此班号，无法导入");
                            return apiResult;
                        }
                        classIds.add(classId);
                        info.setClassId(classId);
                        info.setSubjectName(importExamSubject.getSubjectName());

                    //查询本次考试这个班级 考没有考这门课程
                    Long examId = this.examSettingsMapper.findExamIdByClassId(info);
                    if (examId==null){
                        //这个班没有这门课程的考试
                        apiResult.setCode(ApiCode.import_failed);
                        apiResult.setData(info);
                        apiResult.setMsg("班号：" + info.getClassNumber() + " 课程：" + info.getSubjectName()+" 没有当前班级的此课程考试，无法导入");
                        return apiResult;
                    }
                    info.setExamId(examId);
                  }
                }
                //遍历班级列表
                for (Integer classId:classIds){
                    for (ImportExamSubInfo info : readBaseInfo){
                        if (info.getClassId().equals(classId)){
                            //查重
                            SchoolExamGrade schoolExamGrade = this.examSettingsMapper.findSchoolExamGradeImport(info);
                            if (schoolExamGrade!=null){
                                //重复   修改(删除原来的数据)
                                //修改参数（school_exam_grade表）
                                schoolExamGrade.setOperatorId(operatorId);
                                schoolExamGrade.setDegree(info.getDegree());
                                schoolExamGrade.setAdvantageScore(info.getAdvantageScore());
                                //更新school_exam_grade表
                                this.examSettingsMapper.updateSchoolExamGrade(schoolExamGrade);
                                //根据id删除grade表信息
                                this.examSettingsMapper.deleteGradeById(schoolExamGrade);
                                //重新添加grade表信息
                                for (ImportExamSubInfo info1 : readBaseInfo){
                                    if (info1.getClassNumber()!=null && !"".equals(info1.getClassNumber())){
                                        if (Long.valueOf(info1.getClassId()).equals(schoolExamGrade.getClassId())){
                                            Long examGradeId = this.examSettingsMapper.findExamGradeId(info1);
                                            info1.setExamGradeId(examGradeId);
                                            //添加grade表  名次信息
                                            this.examSettingsMapper.insertGradeByImportMc(info1);

                                            //添加grade表  分数信息
                                            this.examSettingsMapper.insertGradeByImportScore(info1);
                                        }
                                    }
                                }
                            }else {
                                //添加对象
                                SchoolExamGrade seg = new SchoolExamGrade();
                                //查询classTypeId  subjectId
                                Map<String, Object> map = this.examSettingsMapper.findClassIdAndSubjectId(info);
                                //设置参数
                                seg.setSchoolId(info.getSchoolId());
                                seg.setExamParentId(info.getExamParentId());
                                seg.setClassPeriodId(info.getPeriodId());
                                seg.setThetime(info.getThetime());
                                seg.setClassId(Long.valueOf(info.getClassId()));
                                seg.setExamSubject(info.getSubjectName());
                                seg.setExamId(info.getExamId());
                                seg.setGradeType("3");
                                seg.setFounderId(operatorId);
                                seg.setCreateTime(createTime);
                                seg.setDegree(info.getDegree());
                                seg.setAdvantageScore(info.getAdvantageScore());
                                seg.setExamSubjectId((Long) map.get("subject_id"));
                                seg.setClassTypeId((Integer) map.get("class_type_id"));

                                //插入参数基础信息(school_exam_grade表)
                                int i = this.examSettingsMapper.insertExamGrade(seg);

                                //插入grade表信息
                                for (ImportExamSubInfo info1 : readBaseInfo){
                                    if (info1.getClassNumber()!=null && !"".equals(info1.getClassNumber())){
                                        if (info1.getClassId().equals(classId)){
                                            Long examGradeId = this.examSettingsMapper.findExamGradeId(info1);
                                            info1.setExamGradeId(examGradeId);
                                            //添加grade表  名次信息
                                            this.examSettingsMapper.insertGradeByImportMc(info1);

                                            //添加grade表  分数信息
                                            this.examSettingsMapper.insertGradeByImportScore(info1);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }

                msg.append(" 单科设置") ;
            }

//////////////////////////////////总分设置///////////////////////////////////////////////////////////////////////

            //读取第二个sheet的第五行1,2列，获取班级类型和难度系数
            ImportExamClassType importExamClassType = new ImportExamClassType();
            try {
                ImportExamClassType readDateT3 =
                        ImportExeclUtil.readDateT(workbook, importExamClassType, in, 1,
                                new Integer[]{3, 1},new Integer[]{3, 2});
            }catch (Exception e){
                //优势分数线输入错误，无法 转换为 BigDecimal
                apiResult.setCode(ApiCode.import_failed);
                apiResult.setMsg("难度系数输入错误，无法导入");
                return apiResult;
            }
            //输入了班级类型   进总分参数设置
            if (importExamClassType.getClassType()!=null && !"".equals(importExamClassType.getClassType()))
            {
                //判断班级类型   获取班级类型id
                Integer classTypeId = this.examSettingsMapper.findClassTypeIdByName(schoolId,importExamClassType.getClassType());
                if (classTypeId==null){
                    apiResult.setCode(ApiCode.import_failed);
                    apiResult.setData(importExamClassType);
                    apiResult.setMsg("班级类型：" + importExamClassType.getClassType() + " 不存在，无法导入");
                    return apiResult;
                }

                //读取二个sheet的第7行开始读取
                ImportExamTotalInfo tatolInfo = new ImportExamTotalInfo();
                List<ImportExamTotalInfo> readBaseInfo = ImportExeclUtil.readDateListT(workbook, tatolInfo, 5, 0, 1);

                //查重
                ImportExamTotalInfo importExamTotalInfo = new ImportExamTotalInfo();
                importExamTotalInfo.setSchoolId(toLong());
                importExamTotalInfo.setExamParentId(schoolExamParent.getExamParentId());
                importExamTotalInfo.setPeriodId(schoolPeriod.getPeriodId());
                importExamTotalInfo.setThetime(exmaName.getThetime());
                importExamTotalInfo.setClassTypeId(classTypeId);
                //总分查重
                SchoolExamGrade schoolExamGrade = this.examSettingsMapper.findExamByTotalImpl(importExamTotalInfo);
                if (schoolExamGrade!=null){
                    //修改
                    schoolExamGrade.setOperatorId(operatorId);
                    schoolExamGrade.setDegree(importExamClassType.getDegree());
                    //更新school_exam_grade表
                    this.examSettingsMapper.updateSchoolExamGrade(schoolExamGrade);
                    //根据id删除grade表信息
                    this.examSettingsMapper.deleteGradeById(schoolExamGrade);
                    //重新添加
                    //添加grade表信息
                    for (ImportExamTotalInfo info:readBaseInfo){
                        if (info.getMcName()!=null && !"".equals(info.getMcName())){
                            info.setExamGradeId(schoolExamGrade.getExamGradeId());
                            //添加grade表  名次信息
                            this.examSettingsMapper.insertGradeByImportTotalMc(info);

                            //添加grade表  分数信息
                            this.examSettingsMapper.insertGradeByImportTotalScore(info);
                        }
                    }
                }else {
                    //添加
                    //设置参数
                    SchoolExamGrade seg = new SchoolExamGrade();
                    seg.setSchoolId(importExamTotalInfo.getSchoolId());
                    seg.setExamParentId(importExamTotalInfo.getExamParentId());
                    seg.setClassPeriodId(importExamTotalInfo.getPeriodId());
                    seg.setThetime(importExamTotalInfo.getThetime());
                    seg.setGradeType("1");
                    seg.setFounderId(operatorId);
                    seg.setCreateTime(createTime);
                    seg.setDegree(importExamClassType.getDegree());
                    seg.setClassTypeId(importExamTotalInfo.getClassTypeId());
                    seg.setExamSubject("总分");
                    //插入参数基础信息(school_exam_grade表)  主键返回
                    int i = this.examSettingsMapper.insertExamGrade(seg);

                    //添加grade表信息
                    for (ImportExamTotalInfo info:readBaseInfo){
                        if (info.getMcName()!=null && !"".equals(info.getMcName())){
                            info.setExamGradeId(seg.getExamGradeId());
                            //添加grade表  名次信息
                            this.examSettingsMapper.insertGradeByImportTotalMc(info);

                            //添加grade表  分数信息
                            this.examSettingsMapper.insertGradeByImportTotalScore(info);
                        }
                    }

                }

                msg.append(" 总分设置") ;
            }

//////////////////////////////////////班级总分任务数设置////////////////////////////////////////////////////////////
            //读取第三个sheet  从第三行开始
            ImportExamClassTotalTask totalTask = new ImportExamClassTotalTask();
            List<ImportExamClassTotalTask> readBaseInfo = ImportExeclUtil.readDateListT(workbook, totalTask, 3, 0, 2);
            if (readBaseInfo!=null && readBaseInfo.size()!=0 && readBaseInfo.get(0)!=null){
                Integer classNumber = readBaseInfo.get(0).getClassNumber();
                //输入了第一个班号，进入班级总分任务数设置
                if (classNumber!=null && !"".equals(classNumber)){
                    //取出classId
                    Set<Integer> classIds = new HashSet();
                    for (ImportExamClassTotalTask info : readBaseInfo){
                        //设置参数
                        info.setExamParentId(schoolExamParent.getExamParentId());
                        info.setSchoolId(toLong());
                        info.setPeriodId(schoolPeriod.getPeriodId());
                        info.setThetime(exmaName.getThetime());
                        if (info.getClassNumber()!=null && !"".equals(info.getClassNumber())) {
                            Integer classId = this.examSettingsMapper.findClassIdByTotal(info);
                            if (classId == null) {
                                //该届次下无此班号
                                apiResult.setCode(ApiCode.import_failed);
                                apiResult.setData(info);
                                apiResult.setMsg("班号：" + info.getClassNumber() + " 该届次下无此班号，无法导入");
                                return apiResult;
                            }
                            classIds.add(classId);
                            info.setClassId(classId);
                        }
                    }

                    for (Integer classId:classIds){
                        //获取班级类型id
                        Integer classTypeId = this.examSettingsMapper.findClassTypeIdByClassId(schoolId,classId);
                        //查询总分档次设置信息
                        //创建查询对象
                        Map map = new HashMap();
                        map.put("schoolExamParent",schoolExamParent.getExamParentId());
                        map.put("schoolId",schoolId);
                        map.put("thetime",exmaName.getThetime());
                        map.put("periodId",schoolPeriod.getPeriodId());
                        map.put("typeId",classTypeId);
                        //查询这个班级的年级总分信息
                        List<Grade> list = this.examSettingsMapper.findGradeByTotalImport(map);
                        if (list == null || list.size() == 0 || list.get(0) == null) {
                            //根据类型id 查询类型名称
                            String classType = this.examSettingsMapper.findClassTypeNameById(schoolId,classTypeId);
                            apiResult.setCode(ApiCode.import_failed);
                            apiResult.setMsg("请先完成班级类型为："+ classType +" 的总分设置");
                            return apiResult;
                        }

                        //查重
                        map.put("classId",classId);
                        SchoolExamGrade schoolExamGrade = this.examSettingsMapper.findExamByClassImport(map);
                        if (schoolExamGrade!=null){
                            //修改
                            schoolExamGrade.setOperatorId(operatorId);
                            //更新school_exam_grade表
                            this.examSettingsMapper.updateSchoolExamGrade(schoolExamGrade);
                            //根据id删除grade表信息
                            this.examSettingsMapper.deleteGradeById(schoolExamGrade);
                            //重新添加

                            for (ImportExamClassTotalTask info : readBaseInfo){

                                if (info.getClassId().equals(classId)){
                                    //判断档次名称是否匹配
                                    boolean iszq = false;
                                    for (Grade grade:list){
                                        if (info.getScoreName().equals(grade.getGradeName())){
                                            iszq = true;
                                            break;
                                        }
                                    }
                                    if (iszq==false){
                                        //不匹配
                                        apiResult.setCode(ApiCode.import_failed);
                                        apiResult.setData(info);
                                        apiResult.setMsg("班号："+ info.getClassNumber() +",总分分数档次名称：" + info.getScoreName() + " 与总分设置中的分数档次名称不匹配，无法导入");
                                        return apiResult;
                                    }

                                    for (Grade grade:list){
                                        if (grade.getGradeName().equals(info.getScoreName())){
                                            info.setExamGradeId(schoolExamGrade.getExamGradeId());
                                            info.setMaxScore(BigDecimal.valueOf(grade.getNumMax()));
                                            info.setMinScore(BigDecimal.valueOf(grade.getNumMin()));
                                            info.setScore(grade.getNumScore());

                                            //插入grade信息
                                            int j = this.examSettingsMapper.insertGradeImport(info);
                                        }
                                    }
                                }
                            }


                        }else {
                            //添加
                            //参数设置
                            SchoolExamGrade seg = new SchoolExamGrade();
                            seg.setSchoolId(schoolId);
                            seg.setExamParentId(schoolExamParent.getExamParentId());
                            seg.setClassPeriodId(schoolPeriod.getPeriodId());
                            seg.setThetime(exmaName.getThetime());
                            seg.setGradeType("2");
                            seg.setFounderId(operatorId);
                            seg.setCreateTime(createTime);
                            seg.setClassTypeId(classTypeId);
                            seg.setExamSubject("总分");
                            seg.setClassId(Long.valueOf(classId));
                            //插入参数基础信息(school_exam_grade表)  主键返回
                            int i = this.examSettingsMapper.insertExamGrade(seg);


                            //插入grade表信息
                            for (ImportExamClassTotalTask info : readBaseInfo){

                                if (info.getClassId().equals(classId)){
                                    //判断档次名称是否匹配
                                    boolean iszq = false;
                                    for (Grade grade:list){
                                        if (info.getScoreName().equals(grade.getGradeName())){
                                            iszq = true;
                                            break;
                                        }
                                    }
                                    if (iszq==false){
                                        //不匹配
                                        apiResult.setCode(ApiCode.import_failed);
                                        apiResult.setData(info);
                                        apiResult.setMsg("班号："+ info.getClassNumber() +",总分分数档次名称：" + info.getScoreName() + " 与总分设置中的分数档次名称不匹配，无法导入");
                                        return apiResult;
                                    }

                                    for (Grade grade:list){
                                        if (grade.getGradeName().equals(info.getScoreName())){
                                            info.setExamGradeId(seg.getExamGradeId());
                                            info.setMaxScore(BigDecimal.valueOf(grade.getNumMax()));
                                            info.setMinScore(BigDecimal.valueOf(grade.getNumMin()));
                                            info.setScore(grade.getNumScore());

                                            //插入grade信息
                                            int j = this.examSettingsMapper.insertGradeImport(info);
                                        }
                                    }
                                }
                            }

                        }
                    }

                    msg.append(" 班级总分任务数设置") ;
                }
            }


///////////////////////////////////单科优势分数线设置////////////////////////////////////////////////////////

            //读取第四个sheet 第三行第一列  获取班级类型
            ImportExamClassType classType = new ImportExamClassType();
            ImportExamClassType readDateT4 =
                        ImportExeclUtil.readDateT(workbook, classType, in, 3,
                                new Integer[]{3, 1},new Integer[]{3,2});
            //输入了班级类型   进单科优势分数线设置
            if (classType.getClassType()!=null && !"".equals(classType.getClassType())){
                //判断班级类型   获取班级类型id
                Integer classTypeId = this.examSettingsMapper.findClassTypeIdByName(schoolId,classType.getClassType());
                if (classTypeId==null){
                    apiResult.setCode(ApiCode.import_failed);
                    apiResult.setData(classType);
                    apiResult.setMsg("班级类型：" + classType.getClassType() + " 不存在，无法导入");
                    return apiResult;
                }
                //基础参数设置
                SchoolExamGrade schoolExamGrade = new SchoolExamGrade();
                schoolExamGrade.setSchoolId(toLong());
                schoolExamGrade.setExamParentId(schoolExamParent.getExamParentId());
                schoolExamGrade.setClassPeriodId(schoolPeriod.getPeriodId());
                schoolExamGrade.setThetime(exmaName.getThetime());
                schoolExamGrade.setClassTypeId(classTypeId);
                schoolExamGrade.setGradeType("4");
                schoolExamGrade.setFounderId(operatorId);
                schoolExamGrade.setCreateTime(createTime);

                //读取第四个sheet  第五行开始
                ImportExamOneSubInfo oneSubInfo = new ImportExamOneSubInfo();
                List<ImportExamOneSubInfo> readBaseInfo1 = ImportExeclUtil.readDateListT(workbook, oneSubInfo, 5, 0, 3);
                //遍历
                for (ImportExamOneSubInfo info:readBaseInfo1){
                    if (info.getSubjectName() != null && !"".equals(info.getSubjectName())) {
                        //设置参数
                        info.setExamParentId(schoolExamParent.getExamParentId());
                        info.setSchoolId(toLong());
                        info.setThetime(exmaName.getThetime());
                        //判断这次考试有没有这个课程  获取课程id
                        Long subjectId = this.examSettingsMapper.findSubjectIdByName(info);
                        if (subjectId==null){
                            apiResult.setCode(ApiCode.import_failed);
                            apiResult.setData(info);
                            apiResult.setMsg("考试课程：" + info.getSubjectName() + " 当前考试未包含此课程，无法导入");
                            return apiResult;
                        }

                        //查重
                        schoolExamGrade.setExamSubjectId(subjectId);
                        SchoolExamGrade seg = this.examSettingsMapper.findExamByScore(schoolExamGrade);
                        if (seg != null) {
                            //更新
                            seg.setOperatorId(operatorId);
                            seg.setAdvantageScore(info.getAdvantageScore());
                            //更新
                            this.examSettingsMapper.updateSchoolExamGrade(seg);
                        } else {
                            //添加
                            //课程名称，优势分数线
                            schoolExamGrade.setExamSubject(info.getSubjectName());
                            schoolExamGrade.setAdvantageScore(info.getAdvantageScore());
                            //添加
                            this.examSettingsMapper.insertExamGrade(schoolExamGrade);
                        }
                    }
                }
                msg.append(" 单科优势分数线设置");
            }



        }catch (Exception e){
            apiResult.setCode(ApiCode.import_failed);
            apiResult.setMsg(ApiCode.import_failed_MSG);
            e.printStackTrace();
            return apiResult;
        }
        apiResult.setCode(ApiCode.SUCCESS);
        if ("".equals(msg.toString())){
            apiResult.setMsg("已完成：无");
        }else {
            apiResult.setMsg("已完成：" + msg);
        }
        return apiResult;
    }

}
