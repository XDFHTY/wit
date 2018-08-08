package com.cj.witscorefind.service.Impl;

import com.cj.witbasics.entity.*;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.PeriodAndThetime;
import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.mapper.GradeMapper;
import com.cj.witscorefind.mapper.SchoolExamGradeMapper;
import com.cj.witscorefind.service.ParameterSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ParameterSettingServiceImpl implements ParameterSettingService {

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private SchoolExamGradeMapper schoolExamGradeMapper;

    @Value("${school_id}")
    String schoolId;

    @Override
    public int addExamGrade(List<SchoolExamGrade> schoolExamGrades, HttpSession session) {

        Long adminId = (Long) session.getAttribute("adminId");
        Long roleId = (Long) session.getAttribute("roleId");
        Date date = new Date();
        //校验操作权限
        if(!checkPower(schoolExamGrades,session)){
            return ApiCode.http_status_unauthorized;
        }

        int i = 0;


        for (SchoolExamGrade schoolExamGrade : schoolExamGrades){
            schoolExamGrade.setFounderId(adminId);
            schoolExamGrade.setCreateTime(date);
            schoolExamGrade.setSchoolId(Long.parseLong(schoolId));

            //判断这场 考试ID-课程 参数是否已存在，存在则禁止插入数据
            SchoolExamGrade schoolExamGrade1 = schoolExamGradeMapper.findSchoolExamGradeByExamSubject(schoolExamGrade);
            if(schoolExamGrade1 != null){
                return ApiCode.error_create_failed;
            }

            i = schoolExamGradeMapper.insertSelective(schoolExamGrade);
            Long examGradeId = schoolExamGrade.getExamGradeId();

            for(Grade grade : schoolExamGrade.getGrades()){
                grade.setExamGradeId(examGradeId);
            }

            gradeMapper.addGrade(schoolExamGrade.getGrades());


        }
        return i;
    }

    @Override
    public Pager findScoreGrade(Pager p) {
        //查询总条数
        p.setRecordTotal(schoolExamGradeMapper.findScoreGradeTotal(p));

        //查询结果
        p.setContent(schoolExamGradeMapper.findScoreGrade(p));

        return p;
    }

    //修改
    @Override
    public int updateScoreGrade(List<SchoolExamGrade> schoolExamGrades, HttpSession session) {
        Long adminId = (Long) session.getAttribute("adminId");
        Long roleId = (Long) session.getAttribute("roleId");
        Date date = new Date();
        //校验操作权限
        if(!checkPower(schoolExamGrades,session)){
            return ApiCode.http_status_unauthorized;
        }
        int i = 0;
        for (SchoolExamGrade schoolExamGrade : schoolExamGrades){

            //判断这场 考试ID-课程 参数是否已存在，存在则更新grade数据
            SchoolExamGrade schoolExamGrade1 = schoolExamGradeMapper.findSchoolExamGradeByExamSubject(schoolExamGrade);
            if(schoolExamGrade1 != null){  //档次设置数据存在，更新内容
                //根据 exam_grade_id 清空原档次数据
                gradeMapper.delGrade(schoolExamGrade1.getExamGradeId());

                //插入新数据
                for(Grade grade : schoolExamGrade.getGrades()){
                    grade.setExamGradeId(schoolExamGrade1.getExamGradeId());
                }

                i = gradeMapper.addGrade(schoolExamGrade.getGrades());

            }



        }
        return i;
    }


    //年级主任、班主任操作权限校验
    private boolean checkPower(List<SchoolExamGrade> schoolExamGrades, HttpSession session){

        Long adminId = (Long) session.getAttribute("adminId");
        Long roleId = (Long) session.getAttribute("roleId");
        Date date = new Date();
        //校验操作权限
        if(roleId == 21){  //年级主任
            //查询此年级主任管理的学段、届次
            PeriodDirectorThetime periodDirectorThetime = schoolExamGradeMapper.findPeriodDirectorThetimeByAdminId(adminId);
            if(periodDirectorThetime != null){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                String thetime = sdf.format(periodDirectorThetime.getThetime());
                for (SchoolExamGrade schoolExamGrade :schoolExamGrades){
                    //匹配学段ID和届次
                    if(thetime.equals(schoolExamGrade.getThetime()) && periodDirectorThetime.getPeriodId().intValue() == schoolExamGrade.getClassPeriodId()){

                        return true;
                    }else {
                        return false;
                    }
                }
            }else {
                return false;
            }
            return false;
        }else if(roleId == 4){  //班级主任
            //查询此班级主任管理的班级
            SchoolPeriodClassThetime schoolPeriodClassThetime = schoolExamGradeMapper.findSchoolPeriodClassThetimeByAdminId(adminId);
            if(schoolPeriodClassThetime != null){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                String thetime = sdf.format(schoolPeriodClassThetime.getThetime());

                for (SchoolExamGrade schoolExamGrade :schoolExamGrades){
                    //匹配学段ID和届次和班级ID
                    if(thetime.equals(schoolExamGrade.getThetime())
                            && schoolPeriodClassThetime.getPeriodId().intValue() == schoolExamGrade.getClassPeriodId()
                            && schoolPeriodClassThetime.getClassId() == schoolExamGrade.getClassId()){
                        return true;

                    }else {
                        return false;
                    }
                }
                return false;
            }else {
                return false;
            }
        }else {

            //不是年级主任和班主任
            return false;
        }

    }
}
