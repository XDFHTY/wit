package com.cj.witbasics.service.Impl;

import com.cj.witbasics.entity.*;
import com.cj.witbasics.mapper.ExportMapper;
import com.cj.witbasics.service.AdminInfoService;
import com.cj.witcommon.entity.ApiCode;
import com.cj.witcommon.entity.ApiResult;
import com.cj.witcommon.utils.excle.exportExcelUtil;
import com.cj.witcommon.utils.map.MapUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 人事信息导出
 * Created by XD on 2018/5/19.
 */
@Service
public class AdminInfoServiceImpl implements AdminInfoService {

    private static final Logger log = LoggerFactory.getLogger(SchoolSubjectServiceImpl.class);

    @Autowired
    private ExportMapper exportMapper;

    /**
     * 人事信息导出（全部导出）
     * @param response
     */
    @Override
    public ApiResult exportAdminInfo(HttpServletResponse response) {
        OutputStream out = null;
        ApiResult result = new ApiResult();
        try {
            //获取流
            try {
                out = response.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //设置标题
            String[] titles = {"教职工编号*","姓名*","英文名","教职工来源","性别","出生日期（yyyy-MM-dd）","籍贯","民族","政治面貌","入党团时间","参加工作时间","参加教育工作时间","原始学历","原始毕业时间","原始毕业学校","最高学历","最高毕业时间","最高毕业学校","原专业","职务","职称(职业资格)","评职时间","低一级评职时间","普通话等级","普通话等级证书编号","普通话等级取得时间","聘任年限","评骨干时间","连续工龄","教师资格证书编号","教师资格种类","教师资格学科","计算机等级","计算机等级证书编号","计算机等级取得时间","教龄","评职详细","来我校时间","家庭详细住址","住宅电话","手机","身份证号码","骨干教师级别","合同起始时间","工资岗位","合同结束时间","曾用名","办公电话","家庭邮编","是否华侨","所属部门","是否专任教师","是否班主任","班主任年限","身份","外语语种","外语水平","原学制","最高学制","最高学位","学位数量","工作岗位","专业技术岗位分类","任教学科级别","任教学科","校区","专业技术资格","专业技术资格取得时间","英语口语等级","英语口语等级证书编号","英语口语等级取得时间","学位类别","校龄","是否随军家属","是否教代会代表","是否双肩挑","实职级别","薪级工资","工作岗位(副)","岗位工资(副)","薪级工资(副)","紧急联系人","备注","任教学段"};
            //Excel结果集
            List<List<String>> dataHandler = new ArrayList<List<String>>();
            //查询结果集
            List<AdminInfoExport> data = this.exportMapper.selectAdminInfo();

            for(AdminInfoExport info : data){
              // List<String> list = addAdminInfoToList(info);
              // dataHandler.add(list);

                //反射按顺序获取所有值
                List<String> list = ObjectToListUtlis(info);
                dataHandler.add(list);
            }
            //创建工作薄
            XSSFWorkbook workbook = new XSSFWorkbook();
            try {
                //导出信息
                exportExcelUtil.exportExcel(workbook, 0, "人事导出信息", titles, dataHandler, out);
                workbook.write(out);
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

    /**
     * 角色信息全部导出
     * @param response
     * @return
     */
    @Override
    public ApiResult exportAdminRole(HttpServletResponse response) {
        OutputStream out = null;
        exportExcelUtil gwu = new exportExcelUtil();
        ApiResult result = new ApiResult();
        try {
            //获取流
            try {
                out = response.getOutputStream();
                /*String savePath = "D:/file/subjectInfoTable.xlsx";
                out = new FileOutputStream(savePath);*/
            } catch (IOException e) {
                e.printStackTrace();
            }
            //设置标题
            String[] titles = {"id","角色名称","角色分类","状态","创建时间"};
            //Excel结果集
            List<List<String>> dataHandler = new ArrayList<List<String>>();
            //查询结果集
            List<AdminRoleExport> data = this.exportMapper.selectAdminRole();

            for(AdminRoleExport info : data){
                //使用反射获取实体类所有key和value 顺序不对
                /*List<String> rowData  = new ArrayList<String>();
                try {
                    PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
                    PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(info);
                    for (int i = 0; i < descriptors.length; i++) {
                        String name = descriptors[i].getName();
                        System.out.println(name);
                        if (!"class".equals(name)) {
                            //System.out.println(name+":"+ propertyUtilsBean.getNestedProperty(info, name));
                            rowData.add((String) propertyUtilsBean.getNestedProperty(info, name));
                        }
                    }
                    dataHandler.add(rowData);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                /*List<String> rowData  = new ArrayList<String>();
                //实体类转map
                Map<String, String> map =  MapUtil.bean2Map(info);
                Set<String> keys = map.keySet();
                for(String key:keys){
                    System.out.println("key值："+key+" value值："+map.get(key));
                    rowData.add(map.get(key));
                }
                dataHandler.add(rowData);*/

                //反射获取所有实体类所有值
                List<String> list = ObjectToListUtlis(info);
                dataHandler.add(list);

            }
            //创建工作薄
            XSSFWorkbook workbook = new XSSFWorkbook();
            try {
                //导出信息
                gwu.exportExcel(workbook, 0, "角色导出信息", titles, dataHandler, out);
                workbook.write(out);
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

    /**
     * 学生信息导出
     * @param response
     * @return
     */
    @Override
    public ApiResult exportStudentOsaas(HttpServletResponse response) {
        OutputStream out = null;
        ApiResult result = new ApiResult();
        try {
            //获取流
            try {
                out = response.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //设置标题
            String[] titles = {"校内学号","班内学号","会考号","学籍号","姓名","性别","校区","学段","年级","班级","出生日期","有效证件类型","有效证件号","教育Id号","全国学籍号","国别","港澳台侨外","户口所在地","户口所在地-详细地址","政治面貌","民族","学生类别","就读方式","是否按本市户口学生对待","现住址","家庭住址","家庭住址(详细)","通讯地址","出生地","籍贯","健康状况","户口性质","手机号","邮政编码","过敏史","既往病史","独生子女","是否是军区子弟","受过学前教育","留守儿童","进城务工子女","姓名拼音","英文名","是否本市学籍","招生类别","学籍辅号","随班就读","学生状态","残疾类型","居住地","孤儿","烈士优抚子女","是否需要申请资助","是否享受一补","入学方式","学生来源","入学日期","学生来处","原学校名称"};
            //Excel结果集
            List<List<String>> dataHandler = new ArrayList<List<String>>();
            //查询结果集
            List<StudentExport> data = this.exportMapper.selectStudentOsaas();

            for(StudentExport info : data){
                //原始add方式
                //List<String> list = addStudentToList(info);
                //dataHandler.add(list);

                //反射获取所有实体类所有值
                List<String> list = ObjectToListUtlis(info);
                dataHandler.add(list);
            }
            //创建工作薄
            XSSFWorkbook workbook = new XSSFWorkbook();
            try {
                //导出信息
                exportExcelUtil.exportExcel(workbook, 0, "学生导出信息", titles, dataHandler, out);
                workbook.write(out);
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

    /**
     * 把AdminInfoExport实体类所有值添加到list
     * @param info
     * @return
     */
    public static List<String> addAdminInfoToList(AdminInfoExport info){
        List<String> list = new ArrayList<>();

        if(info.getStaffNumber()==null){list.add("");}else {list.add(info.getStaffNumber());}
        if(info.getFullName()==null){list.add("");}else { list.add(info.getFullName());}
        if(info.getEnglishName()==null){list.add("");}else { list.add(info.getEnglishName());}
        if(info.getStaffSource()==null){list.add("");}else { list.add(info.getStaffSource());}
        if(info.getGender()==null){list.add("");}else { list.add(info.getGender());}
        if(info.getBirthDate()==null){list.add("");}else { list.add(info.getBirthDate());}
        if(info.getPlaceOfOrigin()==null){list.add("");}else { list.add(info.getPlaceOfOrigin());}
        if(info.getNation()==null){list.add("");}else { list.add(info.getNation());}
        if(info.getPoliticalOutlook()==null){list.add("");}else { list.add(info.getPoliticalOutlook());}
        if(info.getPartyTime()==null){list.add("");}else { list.add(info.getPartyTime());}
        if(info.getWorkDate()==null){list.add("");}else { list.add(info.getWorkDate());}
        if(info.getParticipateWorkDate()==null){list.add("");}else { list.add(info.getParticipateWorkDate());}
        if(info.getPrimaryEducation()==null){list.add("");}else { list.add(info.getPrimaryEducation());}
        if(info.getPrimaryGraduateInstitutionsDate()==null){list.add("");}else { list.add(info.getPrimaryGraduateInstitutionsDate());}
        if(info.getPrimaryGraduateInstitutions()==null){list.add("");}else { list.add(info.getPrimaryGraduateInstitutions());}
        if(info.getHighestEducation()==null){list.add("");}else { list.add(info.getHighestEducation());}
        if(info.getHighestGraduateInstitutionsDate()==null){list.add("");}else { list.add(info.getHighestGraduateInstitutionsDate());}
        if(info.getHighestGraduateInstitutions()==null){list.add("");}else { list.add(info.getHighestGraduateInstitutions());}
        if(info.getPrimaryMajor()==null){list.add("");}else { list.add(info.getPrimaryMajor());}
        if(info.getMasterDuties()==null){list.add("");}else { list.add(info.getMasterDuties());}
        if(info.getProfessionalQualification()==null){list.add("");}else { list.add(info.getProfessionalQualification());}
        if(info.getTimeOfEvaluation()==null){list.add("");}else { list.add(info.getTimeOfEvaluation());}
        if(info.getJuniorJobDate()==null){list.add("");}else { list.add(info.getJuniorJobDate());}
        if(info.getPutonghuaGrade()==null){list.add("");}else { list.add(info.getPutonghuaGrade());}
        if(info.getPutonghuaGradeNumber()==null){list.add("");}else { list.add(info.getPutonghuaGradeNumber());}
        if(info.getPutonghuaGradeDate()==null){list.add("");}else { list.add(info.getPutonghuaGradeDate());}
        if(info.getAppointmentYear()==null){list.add("");}else { list.add(info.getAppointmentYear());}
        if(info.getBackboneTeacherDate()==null){list.add("");}else { list.add(info.getBackboneTeacherDate());}
        if(info.getContinuityAge()==null){list.add("");}else { list.add(info.getContinuityAge());}
        if(info.getTeacherCardCode()==null){list.add("");}else { list.add(info.getTeacherCardCode());}
        if(info.getTeacherKind()==null){list.add("");}else { list.add(info.getTeacherKind());}
        if(info.getTeacherKindSubject()==null){list.add("");}else { list.add(info.getTeacherKindSubject());}
        if(info.getComputerLevel()==null){list.add("");}else { list.add(info.getComputerLevel());}
        if(info.getComputerLevelNumber()==null){list.add("");}else { list.add(info.getComputerLevelNumber());}
        if(info.getComputerLevelDate()==null){list.add("");}else { list.add(info.getComputerLevelDate());}
        if(info.getSeniorityAge()==null){list.add("");}else { list.add(info.getSeniorityAge());}
        if(info.getDetailedEvaluation()==null){list.add("");}else { list.add(info.getDetailedEvaluation());}
        if(info.getComeSchoolDate()==null){list.add("");}else { list.add(info.getComeSchoolDate());}
        if(info.getHomeAddress()==null){list.add("");}else { list.add(info.getHomeAddress());}
        if(info.getResidentialTelephone()==null){list.add("");}else { list.add(info.getResidentialTelephone());}
        if(info.getMobilePhone()==null){list.add("");}else { list.add(info.getMobilePhone());}
        if(info.getIdCardNo()==null){list.add("");}else { list.add(info.getIdCardNo());}
        if(info.getBackboneTeacherLevel()==null){list.add("");}else { list.add(info.getBackboneTeacherLevel());}
        if(info.getStartOfThe()==null){list.add("");}else { list.add(info.getStartOfThe());}
        if(info.getPostWage()==null){list.add("");}else { list.add(info.getPostWage());}
        if(info.getContractEndTime()==null){list.add("");}else { list.add(info.getContractEndTime());}
        if(info.getNameUsedBefore()==null){list.add("");}else { list.add(info.getNameUsedBefore());}
        if(info.getOfficePhone()==null){list.add("");}else { list.add(info.getOfficePhone());}
        if(info.getFamilyZipCode()==null){list.add("");}else { list.add(info.getFamilyZipCode());}
        if(info.getOverseasChinese()==null){list.add("");}else { list.add(info.getOverseasChinese());}
        if(info.getTheDepartment()==null){list.add("");}else { list.add(info.getTheDepartment());}
        if(info.getFullTimeTeacher()==null){list.add("");}else { list.add(info.getFullTimeTeacher());}
        if(info.getClassHeadmaster()==null){list.add("");}else { list.add(info.getClassHeadmaster());}
        if(info.getClassHeadmasterYear()==null){list.add("");}else { list.add(info.getClassHeadmasterYear());}
        if(info.getIdentity()==null){list.add("");}else { list.add(info.getIdentity());}
        if(info.getForeignLanguage()==null){list.add("");}else { list.add(info.getForeignLanguage());}
        if(info.getForeignLanguageLevel()==null){list.add("");}else { list.add(info.getForeignLanguageLevel());}
        if(info.getPrimarySchoolSystem()==null){list.add("");}else { list.add(info.getPrimarySchoolSystem());}
        if(info.getHighestSchoolSystem()==null){list.add("");}else { list.add(info.getHighestSchoolSystem());}
        if(info.getHighestAcademicDegree()==null){list.add("");}else { list.add(info.getHighestAcademicDegree());}
        if(info.getAcademicDegreeNumber()==null){list.add("");}else { list.add(info.getAcademicDegreeNumber());}
        if(info.getPost()==null){list.add("");}else { list.add(info.getPost());}
        if(info.getExpertiseType()==null){list.add("");}else { list.add(info.getExpertiseType());}
        if(info.getTeachingLevel()==null){list.add("");}else { list.add(info.getTeachingLevel());}
        if(info.getTeachingSubject()==null){list.add("");}else { list.add(info.getTeachingSubject());}
        if(info.getSchoolCampus()==null){list.add("");}else { list.add(info.getSchoolCampus());}
        if(info.getProfessionalTechnology()==null){list.add("");}else { list.add(info.getProfessionalTechnology());}
        if(info.getProfessionalTechnologyDate()==null){list.add("");}else { list.add(info.getProfessionalTechnologyDate());}
        if(info.getSpokenEnglish()==null){list.add("");}else { list.add(info.getSpokenEnglish());}
        if(info.getSpokenEnglishNumber()==null){list.add("");}else { list.add(info.getSpokenEnglishNumber());}
        if(info.getSpokenEnglishDate()==null){list.add("");}else { list.add(info.getSpokenEnglishDate());}
        if(info.getAcademicDegreeType()==null){list.add("");}else { list.add(info.getAcademicDegreeType());}
        if(info.getSchoolAge()==null){list.add("");}else { list.add(info.getSchoolAge());}
        if(info.getCampFamily()==null){list.add("");}else { list.add(info.getCampFamily());}
        if(info.getTeachersCongress()==null){list.add("");}else { list.add(info.getTeachersCongress());}
        if(info.getIsTwoShoulders()==null){list.add("");}else { list.add(info.getIsTwoShoulders());}
        if(info.getSubstantiveLevel()==null){list.add("");}else { list.add(info.getSubstantiveLevel());}
        if(info.getSalaryScale()==null){list.add("");}else { list.add(info.getSalaryScale());}
        if(info.getPostAttach()==null){list.add("");}else { list.add(info.getPostAttach());}
        if(info.getPostWageAttach()==null){list.add("");}else { list.add(info.getPostWageAttach());}
        if(info.getSalaryScaleAttach()==null){list.add("");}else { list.add(info.getSalaryScaleAttach());}
        if(info.getEmergencyContact()==null){list.add("");}else { list.add(info.getEmergencyContact());}
        if(info.getRemarks()==null){list.add("");}else { list.add(info.getRemarks());}
        if(info.getTeachingSection()==null){list.add("");}else { list.add(info.getTeachingSection());}

        return list;
    }

    /**
     * 把studentExport实体类所有值添加到List
     * @param info
     * @return
     */
    public static List<String> addStudentToList(StudentExport info){
        List<String> list = new ArrayList<>();
        if(info.getSchoolNumber()==null){list.add("");}else{list.add(info.getSchoolNumber());}
        if(info.getClassNumber()==null){list.add("");}else{list.add(info.getClassNumber());}
        if(info.getExaminationNumber()==null){list.add("");}else{list.add(info.getExaminationNumber());}
        if(info.getRegisterNumber()==null){list.add("");}else{list.add(info.getRegisterNumber());}
        if(info.getFullName()==null){list.add("");}else{list.add(info.getFullName());}
        if(info.getGender()==null){list.add("");}else{list.add(info.getGender());}
        if(info.getBasicIdName()==null){list.add("");}else{list.add(info.getBasicIdName());}
        if(info.getPeriodIdName()==null){list.add("");}else{list.add(info.getPeriodIdName());}
        if(info.getGradeIdName()==null){list.add("");}else{list.add(info.getGradeIdName());}
        if(info.getClassIdName()==null){list.add("");}else{list.add(info.getClassIdName());}
        if(info.getBirthDate()==null){list.add("");}else{list.add(info.getBirthDate());}
        if(info.getCardType()==null){list.add("");}else{list.add(info.getCardType());}
        if(info.getCardNumber()==null){list.add("");}else{list.add(info.getCardNumber());}
        if(info.getEducationId()==null){list.add("");}else{list.add(info.getEducationId());}
        if(info.getNationalRegisterNumber()==null){list.add("");}else{list.add(info.getNationalRegisterNumber());}
        if(info.getDifferentCountries()==null){list.add("");}else{list.add(info.getDifferentCountries());}
        if(info.getHongKong()==null){list.add("");}else{list.add(info.getHongKong());}
        if(info.getRegisteredResidence()==null){list.add("");}else{list.add(info.getRegisteredResidence());}
        if(info.getRegisteredResidenceDetailed()==null){list.add("");}else{list.add(info.getRegisteredResidenceDetailed());}
        if(info.getPoliticalOutlook()==null){list.add("");}else{list.add(info.getPoliticalOutlook());}
        if(info.getNation()==null){list.add("");}else{list.add(info.getNation());}
        if(info.getStudentType()==null){list.add("");}else{list.add(info.getStudentType());}
        if(info.getWayOfStudying()==null){list.add("");}else{list.add(info.getWayOfStudying());}
        if(info.getTreatType()==null){list.add("");}else{list.add(info.getTreatType());}
        if(info.getPresentAddress()==null){list.add("");}else{list.add(info.getPresentAddress());}
        if(info.getHomeAddress()==null){list.add("");}else{list.add(info.getHomeAddress());}
        if(info.getHomeAddressDetailed()==null){list.add("");}else{list.add(info.getHomeAddressDetailed());}
        if(info.getPostalAddress()==null){list.add("");}else{list.add(info.getPostalAddress());}
        if(info.getNativeHeath()==null){list.add("");}else{list.add(info.getNativeHeath());}
        if(info.getPlaceOfOrigin()==null){list.add("");}else{list.add(info.getPlaceOfOrigin());}
        if(info.getHealthCondition()==null){list.add("");}else{list.add(info.getHealthCondition());}
        if(info.getHouseholdRegistration()==null){list.add("");}else{list.add(info.getHouseholdRegistration());}
        if(info.getMobilePhone()==null){list.add("");}else{list.add(info.getMobilePhone());}
        if(info.getZipCode()==null){list.add("");}else{list.add(info.getZipCode());}
        if(info.getAnaphylaxis()==null){list.add("");}else{list.add(info.getAnaphylaxis());}
        if(info.getPastMedicalHistory()==null){list.add("");}else{list.add(info.getPastMedicalHistory());}
        if(info.getOneChild()==null){list.add("");}else{list.add(info.getOneChild());}
        if(info.getMilitaryRegion()==null){list.add("");}else{list.add(info.getMilitaryRegion());}
        if(info.getPreschoolEducation()==null){list.add("");}else{list.add(info.getPreschoolEducation());}
        if(info.getLeftBehindChildren()==null){list.add("");}else{list.add(info.getLeftBehindChildren());}
        if(info.getWorkForChildren()==null){list.add("");}else{list.add(info.getWorkForChildren());}
        if(info.getNamePinyin()==null){list.add("");}else{list.add(info.getNamePinyin());}
        if(info.getEnglishName()==null){list.add("");}else{list.add(info.getEnglishName());}
        if(info.getThisCityOsaas()==null){list.add("");}else{list.add(info.getThisCityOsaas());}
        if(info.getEnrolmentCategory()==null){list.add("");}else{list.add(info.getEnrolmentCategory());}
        if(info.getRegisterAssistNumber()==null){list.add("");}else{list.add(info.getRegisterAssistNumber());}
        if(info.getLearningInClass()==null){list.add("");}else{list.add(info.getLearningInClass());}
        if(info.getStudentState()==null){list.add("");}else{list.add(info.getStudentState());}
        if(info.getIsDisability()==null){list.add("");}else{list.add(info.getIsDisability());}
        if(info.getDomicile()==null){list.add("");}else{list.add(info.getDomicile());}
        if(info.getOrphan()==null){list.add("");}else{list.add(info.getOrphan());}
        if(info.getMartyrChild()==null){list.add("");}else{list.add(info.getMartyrChild());}
        if(info.getNeedSubsidy()==null){list.add("");}else{list.add(info.getNeedSubsidy());}
        if(info.getIsSubsidy()==null){list.add("");}else{list.add(info.getIsSubsidy());}
        if(info.getEnrolmentMode()==null){list.add("");}else{list.add(info.getEnrolmentMode());}
        if(info.getStudentSource()==null){list.add("");}else{list.add(info.getStudentSource());}
        if(info.getDateOfAdmission()==null){list.add("");}else{list.add(info.getDateOfAdmission());}
        if(info.getStudentsComeIn()==null){list.add("");}else{list.add(info.getStudentsComeIn());}
        if(info.getOriginalSchool()==null){list.add("");}else{list.add(info.getOriginalSchool());}

        return list;
    }



    /**
     * 取出实体类所有值 按顺序存入list
     * @param obj
     * @return list
     * @throws Exception
     */
    public static List<String> ObjectToListUtlis(Object obj) throws Exception {
        List<String> list = new ArrayList<>();
        //获取全路径
        String s = obj.getClass().getName();
        //反射加载类
        Class c = Class.forName(s);
        //获取全部字段的值
        Field[] fields = c.getDeclaredFields();
        for (Field field:fields){
            //打破封装  能获取private属性值
            field.setAccessible(true);
            String value = (String) field.get(obj);
            //判断值是否为null  null则添加空字符串
            if(value == null){
                list.add("");
            }else {
                list.add(value);
            }
        }
        return list;
    }
}

