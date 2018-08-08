package com.cj.witbasics.service.Impl;

import com.cj.witbasics.entity.*;
import com.cj.witbasics.mapper.*;
import com.cj.witbasics.service.SchoolExamService;
import com.cj.witcommon.entity.*;
import com.cj.witcommon.utils.common.StringHandler;
import com.cj.witcommon.utils.entity.other.Pager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class SchoolExamServiceImpl implements SchoolExamService{
    private static final Logger log = LoggerFactory.getLogger(SchoolExamServiceImpl.class);

    @Autowired(required = false)
    private SchoolGradeMapper gradeMapper;

    @Autowired(required = false)
    private SchoolExamMapper examMapper;

    @Autowired(required = false)
    private SchoolClassMapper classMapper;

    @Autowired(required = false)
    private SchoolSubjectMapper subjectMapper;

    @Autowired(required = false)
    private SchoolPeriodMapper periodMapper;


    @Value("${school_id}")
    private String schoolId;

    @Autowired(required = false)
    private SchoolExamParentMapper schoolExamParentMapper;

    @Autowired
    private PeriodDirectorThetimeMapper periodDirectorThetimeMapper;

    /**
     * 转为Long
     * @return
     */
    private Long toLong(){
        return Long.valueOf(this.schoolId);
    }



    /**
     * 查询届次
     * @param schoolId
     * @return
     */
    @Override
    public List<Map> findAllGradeName(Long schoolId) {
        List<Map> param = examMapper.findAllPeriodAndGrade(schoolId);
        for(Map map : param){
            SchoolPeriod period = this.periodMapper.selectByPrimaryKey((Long)map.get("periodId"));
            map.put("periodName", period.getPeriodName());
        }
        return param;
    }



    /**
     *
     * 根据传入的信息,对应班级信息
     * 班级信息
     */
    @Override
    @Transactional
    public List findAllUnderGradeClass(List<PeriodAndGrade> param) {
        List<SubjectUnit> results = new ArrayList<SubjectUnit>();
        for(PeriodAndGrade temp : param){
            String thetime = temp.getThetime();
            thetime += "-7-1";
            List<SchoolClass> tempResult = this.classMapper.selectByByPeriodAndThetimeExam(temp.getPeriodId(), thetime);
            //学段信息
            SchoolPeriod period = this.periodMapper.selectByPrimaryKey(temp.getPeriodId().longValue());
            //返回结果封装
            SubjectUnit unit = new SubjectUnit();
            unit.setPeriodId(period.getPeriodId());
            unit.setPeriodName(period.getPeriodName());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                unit.setThetime(format.parse(thetime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            List item = new ArrayList();
            for(SchoolClass sclass : tempResult){
                SubjectDetail detail = new SubjectDetail();
                detail.setClassId(sclass.getClassId());
                detail.setClassName(sclass.getClassName());
                detail.setClassType(sclass.getClassType());
                item.add(detail);
                unit.setClassInfo(item);
            }
            results.add(unit);
        }
        return results;
    }


    /**
     * 查询班级对应的课程
     */
    @Override
    @Transactional
    public List findAllSubjectInfo(List<Long> classId) {
        List result = new ArrayList();
        for(Long id : classId){
            List<SchoolClassInfo> underPeriodClass = this.subjectMapper.findSubjectInfo(id);
            for(SchoolClassInfo sc : underPeriodClass){
                result.add(sc);
            }
        }
        return result;
    }

    /**
     * 查询考试名称
     * @param
     * @return
     */
    @Override
    public List<Map> findExamName(Long schoolId) {
        List<Map> result = this.examMapper.selectBySchoolId(schoolId);
        return result;
    }


    /**
     * 模糊查询,考试类别,考试对象
     * @param
     * @param vague
     * @return
     */
    @Override
    public Pager findExamOfVague(String examName, String vague, Pager pager) {
//        int total = this.examMapper.selectCountIdAndVague(examName, vague);
        List<SchoolExam> result = this.examMapper.selectByIdAndVague(examName, vague, pager);
        pager.setRecordTotal(result.size());
        pager.setContent(result);
        return pager;
    }

    //==================================================================================================================
    @Override
    public List<SchoolExam> findAllSchoolExamByThetime(String thetime) {
        return examMapper.findAllSchoolExamByThetime(thetime);
    }

    @Override
    public List<SchoolExamParent> findAllSchoolExamParentByParameter(Pager p) {
        return schoolExamParentMapper.findAllSchoolExamParentByParameter(p);
    }

    @Override
    public List<ExamPeriod> findAllSchoolExamThetimeBySchoolExamParent(Long examParentId,HttpSession session) {
        List<ExamPeriod> examPeriods = examMapper.findAllSchoolExamThetimeBySchoolExamParent(examParentId);
        //权限校验，只展示此人管理的年级、班级、课程的资源
        List<RolePeriodThetimeClassSubject> list = checkRole(session);
        if(list.size()>0){
            //处理学段、届次、班级、课程
            for (RolePeriodThetimeClassSubject rolePeriodThetimeClassSubject : list){
                //遍历学段
                Iterator<ExamPeriod> itPeriod = examPeriods.iterator();
                while(itPeriod.hasNext()) {

                    ExamPeriod examPeriod = itPeriod.next();

                    //匹配学段ID，不一致则删除
                    if(rolePeriodThetimeClassSubject.getPeriodId() != examPeriod.getClassPeriodId()){
                        itPeriod.remove();
                    }else {
                        //遍历届次
                        Iterator<ExamThetime> itThetime = examPeriod.getThetimes().iterator();
                        while (itThetime.hasNext()){

                            ExamThetime examThetime = itThetime.next();

                            //匹配届次
                            if(!rolePeriodThetimeClassSubject.getThetime().equals(examThetime.getThetime())){
                                itThetime.remove();
                            }else {
                                //如果存在班级类型
                                if(rolePeriodThetimeClassSubject.getClassTypeId() != null){
                                    //遍历班级类型
                                    Iterator<ExamClassType> itClassType = examThetime.getClassTypes().iterator();
                                    while (itClassType.hasNext()){
                                        ExamClassType examClassType = itClassType.next();

                                        //匹配班级类型ID
                                        if(rolePeriodThetimeClassSubject.getClassTypeId() != examClassType.getClassTypeId()){
                                            itClassType.remove();
                                        }else {
                                            //如果存在班级层次
                                            if(rolePeriodThetimeClassSubject.getClassLevelId() != null){
                                                //遍历班级层次
                                                Iterator<ExamClassLevel> itClassLevel = examClassType.getClassLevels().iterator();
                                                while (itClassLevel.hasNext()){
                                                    ExamClassLevel examClassLevel = itClassLevel.next();

                                                    //匹配班级层次
                                                    if(rolePeriodThetimeClassSubject.getClassLevelId() != examClassLevel.getLevelId()){
                                                        itClassLevel.remove();
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



        return examPeriods;
    }

    @Override
    public List<ExamClassPeriod> findAllSchoolExamClassByExamParentIdAndThetime(Map map) {
        return examMapper.findAllSchoolExamClassByExamParentIdAndThetime(map);
    }

    @Override
    public List<ExamClassPeriod> findAllSchoolExamThetimeAndSubjectByExamParentIdAndThetime(Map map) {
        return examMapper.findAllSchoolExamThetimeAndSubjectByExamParentIdAndThetime(map);
    }

    @Override
    public List<ExamClass> findClassAndSubjectByCondition(Map map,HttpSession session) {
        List<ExamClass> examClasses = examMapper.findClassAndSubjectByCondition(map);
        //权限校验，只展示
        List<RolePeriodThetimeClassSubject> list = checkRole(session);

        if(list.size()>0){
            for(RolePeriodThetimeClassSubject rolePeriodThetimeClassSubject : list){
                //遍历班级
                Iterator<ExamClass> itClass = examClasses.iterator();
                while (itClass.hasNext()){

                    ExamClass examClass = itClass.next();
                    //匹配班级
                    if(rolePeriodThetimeClassSubject.getClassId() != examClass.getClassId()){
                        itClass.remove();
                    }else {
                        //如果课程存在
                        if(examClass.getSubjects() != null){
                            //遍历课程
                            Iterator<ExamSubject> itSubject = examClass.getSubjects().iterator();
                            while (itSubject.hasNext()){
                                ExamSubject examSubject = itSubject.next();

                                //匹配课程
                                if(rolePeriodThetimeClassSubject.getSubjectId() != examSubject.getExamSubjectId()){
                                    itSubject.remove();
                                }
                            }
                        }
                    }


                }
            }
        }




        return examClasses;
    }


    //分页查询考试列表信息
    @Override
    public Pager findAllExamMsg(Pager pager) {
        List<List<?>> lists = examMapper.findAllExamMsg(pager);

        //设置数据
        pager.setContent(lists.get(0));

        List<Map> list = (List<Map>) lists.get(1);

        Long total = (Long) list.get(0).get("total");
        //设置总条数
        pager.setRecordTotal(total.intValue());

        return pager;
    }

    /**
     * 新增考试
     */
    @Override
//    @Transactional
    public ApiResult addSchoolExamInfo(ExamParam examInfo, Long adminId) {

        ApiResult result = new ApiResult();
        //父节点ID
        Long parentId = null;
        int flag_a = this.schoolExamParentMapper.selectByExamName(examInfo.getExamName());
        if(flag_a <= 0){
            /////////////////////////父节点////////////////////////////
            SchoolExamParent schoolExamParent = new SchoolExamParent();
            schoolExamParent.setExamName(examInfo.getExamName());
            schoolExamParent.setCreateTime(examInfo.getExamTime());//将考试父节点创建时间更新为考试时间（修改数据库定义）
            schoolExamParentMapper.insertSelective(schoolExamParent);
            //父节点ID
            parentId = schoolExamParent.getExamParentId();
            //////////////////////////父节点////////////////////////////
        }else{
            log.error("数据已存在");
            result.setCode(ApiCode.error_duplicated_data);
            result.setMsg(ApiCode.error_duplicated_data_MSG);
            return result;
        }
        //===================================================================

        //插入集合
        List<SchoolExam> examList = new ArrayList<SchoolExam>();
        //获取班级ID,科目ID集合
        List<ExamClassSubject> listClass = examInfo.getParams();
        //创建时间
        Date time = new Date();
        //查重逻辑
        for(ExamClassSubject e : listClass){
            Integer classId = e.getClassId();
            //第二层
            List<SubjectForTea> subject = e.getSubject();
            for(SubjectForTea s : subject){
                //考试科目
                String examSubject = s.getSubjectName();
                Date start = examInfo.getExamTime();
                //根据考试科目,班级ID,查重,增加时间
                int isCopy = this.examMapper.selectCountBySubjectNameAndClassId(classId, examSubject, examInfo.getExamName());
                if(isCopy > 0){
                    //存在记录
                    log.error("数据已存在");
                    result.setCode(ApiCode.error_duplicated_data);
                    result.setMsg(ApiCode.error_duplicated_data_MSG);
                    return result;
                }
                //封装结果集合
                SchoolExam exam = new SchoolExam();
                exam.setExamTypeName(examInfo.getExamTypeName());
                exam.setExamTime(examInfo.getExamTime());
                exam.setExamName(examInfo.getExamName());
                exam.setSchoolId(toLong());
                SchoolClass info = classMapper.selectByPrimaryKey(classId.longValue());
                //设置届次
                exam.setThetime(info.getThetime());
                //设置父节点ID
                if(parentId != null) exam.setExamParentId(parentId);
                //设置考试年级
                exam.setCreateTime(time);
                exam.setFounderId(adminId);
                exam.setOperatorId(adminId);
                //数据
                exam.setExamObject(examInfo.getExamObject());
                exam.setClassId(classId);
                exam.setExamSubjectId(s.getSubjectId());
                exam.setExamSubject(s.getSubjectName());
                examList.add(exam);
            }
        }
        //批量插入
        int success = this.examMapper.insertBatchInfoByU(examList);
        //新增考试父节点信息
        if(success > 0){
            ApiResultUtil.fastResultHandler(result, true, null, null, null);
        }else{
            ApiResultUtil.fastResultHandler(result, true, null, null, null);
            ApiResultUtil.fastResultHandler(result, false, ApiCode.error_create_failed, ApiCode.FAIL_MSG, null);
        }
        return result;
    }




    //权限校验，返回届次，班级，课程集合
    public  List<RolePeriodThetimeClassSubject> checkRole(HttpSession session){
        Long adminId = (Long)session.getAttribute("adminId");
        Long roleId = (Long)session.getAttribute("roleId");
        List<RolePeriodThetimeClassSubject> list = new ArrayList<>();
        switch (roleId.intValue()){
            case 1:break;
            case 2:break;
            case 3:break;
            case 4:
                //班级主任
                //查询其管理的 学段-届次-班级 集合
                list = examMapper.findPeriodIdAndThetimeDoClass(adminId);
                break;
            case 5:
                //科目教师
                //查询其管理的 学段-届次-班级-课程 集合
                list = examMapper.findPeriodIdAndThetimeDoSubject(adminId);


                break;
            case 6:break;
            case 21:
                //年级主任
                //检查其管理的 学段-届次 集合
                list = examMapper.findPeriodIdAndThetimeDoYesr(adminId);


                break;
        }

        return list;
    }


}
