package com.cj.witbasics.entity;

import com.cj.witcommon.utils.excle.IsNeeded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 人事信息导出 实体类
 * Created by XD on 2018/5/19.
 */

public class AdminInfoExport {
    /**
     * 教职工编号
     */
    @IsNeeded
    private String staffNumber;
    /**
     * 姓名
     */
    @IsNeeded
    private String fullName;
    /**
     * 英文名
     */
    @IsNeeded
    private String englishName;
    /**
     * 教职工来源
     */
    @IsNeeded
    private String staffSource;
    /**
     * 性别，0-保密，1-男，2-女，默认为0
     */
    @IsNeeded
    private String gender;
    /**
     * 出生日期
     */
    @IsNeeded
    private String birthDate;
    /**
     * 籍贯（精确到区县）
     */
    @IsNeeded
    private String placeOfOrigin;
    /**
     * 民族（前端单选）
     */
    @IsNeeded
    private String nation;
    /**
     * 政治面貌(01 中共党员 02 中共预备党员 03共青团员 04 民革党员 05 民盟盟员 06 民建会员 07 民进会员 08 农工党党员 09 致公党党员 10 九三学社社员 11 台盟盟员 12 无党派人士 13群众)
     */
    @IsNeeded
    private String politicalOutlook;
    /**
     * 入党团时间
     */
    @IsNeeded
    private String partyTime;
    /**
     * 参加工作时间
     */
    @IsNeeded
    private String workDate;
    /**
     * 参加教育工作时间
     */
    @IsNeeded
    private String participateWorkDate;
    /**
     * 原学历(前端单选）
     */
    @IsNeeded
    private String primaryEducation;

    /**
     * 原毕业时间
     */
    @IsNeeded
    private String primaryGraduateInstitutionsDate;

    /**
     * 原毕业院校
     */
    @IsNeeded
    private String primaryGraduateInstitutions;
    /**
     * 学历(1-博士研究生,2-硕士研究生,3-本科,4-专科,5-高中,6-初中,7-小学,8-其他)(前端单选）
     */
    @IsNeeded
    private String highestEducation;

    /**
     * 最高毕业时间
     */
    @IsNeeded
    private String highestGraduateInstitutionsDate;
    /**
     * 最高毕业院校
     */
    @IsNeeded
    private String highestGraduateInstitutions;
    /**
     * 原专业
     */
    @IsNeeded
    private String primaryMajor;
    /**
     * 职务
     */
    @IsNeeded
    private String masterDuties;
    /**
     * 职称(职业资格)
     */
    @IsNeeded
    private String professionalQualification;
    /**
     * 评职时间
     */
    @IsNeeded
    private String timeOfEvaluation;
    /**
     * 低一级评职时间
     */
    @IsNeeded
    private String juniorJobDate;
    /**
     * 普通话等级(前端单选）
     */
    @IsNeeded
    private String putonghuaGrade;


    /**
     * 普通话等级证书编号
     */
    @IsNeeded
    private String putonghuaGradeNumber;
    /**
     * 普通话等级取得时间
     */
    @IsNeeded
    private String putonghuaGradeDate;
    /**
     * 聘任年限
     */
    @IsNeeded
    private String appointmentYear;
    /**
     * 评骨干时间
     */
    @IsNeeded
    private String backboneTeacherDate;
    /**
     * 连续工龄
     */
    @IsNeeded
    private String continuityAge;
    /**
     * 教师资格证书编号
     */
    @IsNeeded
    private String teacherCardCode;

    /**
     * 教师资格种类
     */
    @IsNeeded
    private String teacherKind;

    /**
     * 教师资格学科
     */
    @IsNeeded
    private String teacherKindSubject;
    /**
     * 计算机等级(前端单选）
     */
    @IsNeeded
    private String computerLevel;
    /**
     * 计算机等级证书编号
     */
    @IsNeeded
    private String computerLevelNumber;

    /**
     * 计算机等级取得时间
     */
    @IsNeeded
    private String computerLevelDate;
    /**
     * 教龄
     */
    @IsNeeded
    private String seniorityAge;
    /**
     * 评职详细
     */
    @IsNeeded
    private String detailedEvaluation;
    /**
     * 来我校时间
     */
    @IsNeeded
    private String comeSchoolDate;
    /**
     * 家庭住址
     */
    @IsNeeded
    private String homeAddress;
    /**
     * 住宅电话
     */
    @IsNeeded
    private String residentialTelephone;
    /**
     * 手机
     */
    @IsNeeded
    private String mobilePhone;
    /**
     * 身份证号码
     */
    @IsNeeded
    private String idCardNo;
    /**
     * 骨干教师级别
     */
    @IsNeeded
    private String backboneTeacherLevel;
    /**
     * 合同起始时间
     */
    @IsNeeded
    private String startOfThe;

    /**
     * 工资岗位
     */
    @IsNeeded
    private String postWage;

    /**
     * 合同结束时间
     */
    @IsNeeded
    private String contractEndTime;
    /**
     * 曾用名
     */
    @IsNeeded
    private String nameUsedBefore;
    /**
     * 办公电话
     */
    @IsNeeded
    private String officePhone;
    /**
     * 家庭邮编
     */
    @IsNeeded
    private String familyZipCode;
    /**
     * 华侨，0-否，1-是
     */
    @IsNeeded
    private String overseasChinese;


    /**
     * 所属部门
     */
    @IsNeeded
    private String theDepartment;
    /**
     * 专任教师
     */
    @IsNeeded
    private String fullTimeTeacher;

    /**
     * 班主任,0-否，1-是
     */
    @IsNeeded
    private String classHeadmaster;

    /**
     * 班主任年限
     */
    @IsNeeded
    private String classHeadmasterYear;
    /**
     * 身份
     */
    @IsNeeded
    private String identity;
    /**
     * 外语语种(前端单选）
     */
    @IsNeeded
    private String foreignLanguage;

    /**
     * 外语水平(前端单选）
     */
    @IsNeeded
    private String foreignLanguageLevel;

    /**
     * 原学制(前端单选）
     */
    @IsNeeded
    private String primarySchoolSystem;
    /**
     * 最高学制（前端单选）
     */
    @IsNeeded
    private String highestSchoolSystem;
    /**
     * 最高学位(前端单选）
     */
    @IsNeeded
    private String highestAcademicDegree;
    /**
     * 学位数量
     */
    @IsNeeded
    private String academicDegreeNumber;
    //TODO:工作岗位字段没有
    /**
     * 工作岗位
     */
    @IsNeeded
    private String post;
    /**
     * 专业技术岗位分类
     */
    @IsNeeded
    private String expertiseType;

    /**
     * 任教学科级别
     */
    @IsNeeded
    private String teachingLevel;
    /**
     * 任教学科
     */
    @IsNeeded
    private String teachingSubject;
    /**
     * 校区名字
     */
    @IsNeeded
    private String schoolCampus;
    /**
     * 专业技术资格
     */
    @IsNeeded
    private String professionalTechnology;

    /**
     * 专业技术资格取得时间
     */
    @IsNeeded
    private String professionalTechnologyDate;
    /**
     * 英语口语等级(前端单选）
     */
    @IsNeeded
    private String spokenEnglish;

    /**
     * 英语口语等级证书编号
     */
    @IsNeeded
    private String spokenEnglishNumber;
    /**
     * 英语口语等级取得时间
     */
    @IsNeeded
    private String spokenEnglishDate;
    /**
     * 学位类别(前端单选）
     */
    @IsNeeded
    private String academicDegreeType;
    /**
     * 校龄
     */
    @IsNeeded
    private String schoolAge;
    /**
     * 是否随军家属，0-否，1-是
     */
    @IsNeeded
    private String campFamily;
    /**
     * 是否教代会代表，0-否，1-是
     */
    @IsNeeded
    private String teachersCongress;
    /**
     * 是否双肩挑0-否 1-是
     */
    @IsNeeded
    private String isTwoShoulders;
    /**
     * 实职级别
     */
    @IsNeeded
    private String substantiveLevel;

    /**
     * 薪级工资
     */
    @IsNeeded
    private String salaryScale;
    /**
     * 工作岗位(副)
     */
    @IsNeeded
    private String postAttach;
    //TODO:工作岗位(副)字段没有

    /**
     * 岗位工资(副)
     */
    @IsNeeded
    private String postWageAttach;

    /**
     * 薪级工资(副)
     */
    @IsNeeded
    private String salaryScaleAttach;
    /**
     * 紧急联系人
     */
    @IsNeeded
    private String emergencyContact;
    /**
     * 备注
     */
    @IsNeeded
    private String remarks;
    /**
     * 任教学段
     */
    @IsNeeded
    private String teachingSection;

    /*public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getStaffSource() {
        return staffSource;
    }

    public void setStaffSource(String staffSource) {
        this.staffSource = staffSource;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if("0".equals(gender)){
            this.gender = "保密";
        }if("1".equals(gender)) {
            this.gender = "男";
        }if("2".equals(gender)) {
            this.gender = "女";
        }
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPlaceOfOrigin() {
        return placeOfOrigin;
    }

    public void setPlaceOfOrigin(String placeOfOrigin) {
        this.placeOfOrigin = placeOfOrigin;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getPoliticalOutlook() {
        return politicalOutlook;
    }
    *//**
     * 政治面貌(01 中共党员 02 中共预备党员 03共青团员 04 民革党员
     * 05 民盟盟员 06 民建会员 07 民进会员 08 农工党党员 09 致公党党员
     * 10 九三学社社员 11 台盟盟员 12 无党派人士 13群众)
     *//*
    public void setPoliticalOutlook(String politicalOutlook) {
        if("1".equals(politicalOutlook)){
            this.politicalOutlook = "中共党员";
        }if("2".equals(politicalOutlook)) {
            this.politicalOutlook = "中共预备党员";
        }if("3".equals(politicalOutlook)) {
            this.politicalOutlook = "共青团员";
        }if("4".equals(politicalOutlook)) {
            this.politicalOutlook = "民革党员";
        }if("5".equals(politicalOutlook)) {
            this.politicalOutlook = "民盟盟员";
        }if("6".equals(politicalOutlook)) {
            this.politicalOutlook = "民建会员";
        }if("7".equals(politicalOutlook)) {
            this.politicalOutlook = "民进会员";
        }if("8".equals(politicalOutlook)) {
            this.politicalOutlook = "农工党党员";
        }if("9".equals(politicalOutlook)) {
            this.politicalOutlook = "致公党党员";
        }if("10".equals(politicalOutlook)) {
            this.politicalOutlook = "九三学社社员";
        }if("11".equals(politicalOutlook)) {
            this.politicalOutlook = "台盟盟员";
        }if("12".equals(politicalOutlook)) {
            this.politicalOutlook = "无党派人士";
        }if("13".equals(politicalOutlook)) {
            this.politicalOutlook = "群众";
        }
    }

    public String getPartyTime() {
        return partyTime;
    }

    public void setPartyTime(String partyTime) {
        this.partyTime = partyTime;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getParticipateWorkDate() {
        return participateWorkDate;
    }

    public void setParticipateWorkDate(String participateWorkDate) {
        this.participateWorkDate = participateWorkDate;
    }

    public String getPrimaryEducation() {
        return primaryEducation;
    }

    public void setPrimaryEducation(String primaryEducation) {
        this.primaryEducation = primaryEducation;
    }

    public String getPrimaryGraduateInstitutionsDate() {
        return primaryGraduateInstitutionsDate;
    }

    public void setPrimaryGraduateInstitutionsDate(String primaryGraduateInstitutionsDate) {
        this.primaryGraduateInstitutionsDate = primaryGraduateInstitutionsDate;
    }

    public String getPrimaryGraduateInstitutions() {
        return primaryGraduateInstitutions;
    }

    public void setPrimaryGraduateInstitutions(String primaryGraduateInstitutions) {
        this.primaryGraduateInstitutions = primaryGraduateInstitutions;
    }

    public String getHighestEducation() {
        return highestEducation;
    }
    *//**
     * 学历(1-博士研究生,2-硕士研究生,3-本科,4-专科,5-高中,6-初中,7-小学,8-其他)(前端单选）
     *//*
    public void setHighestEducation(String highestEducation) {
        if("1".equals(highestEducation)){
            this.highestEducation = "博士研究生";
        }if("2".equals(highestEducation)) {
            this.highestEducation = "硕士研究生";
        }if("3".equals(highestEducation)) {
            this.highestEducation = "本科";
        }if("4".equals(highestEducation)) {
            this.highestEducation = "专科";
        }if("5".equals(highestEducation)) {
            this.highestEducation = "高中";
        }if("6".equals(highestEducation)) {
            this.highestEducation = "初中";
        }if("7".equals(highestEducation)) {
            this.highestEducation = "小学";
        }if("8".equals(highestEducation)) {
            this.highestEducation = "其他";
        }
    }

    public String getHighestGraduateInstitutionsDate() {
        return highestGraduateInstitutionsDate;
    }

    public void setHighestGraduateInstitutionsDate(String highestGraduateInstitutionsDate) {
        this.highestGraduateInstitutionsDate = highestGraduateInstitutionsDate;
    }

    public String getHighestGraduateInstitutions() {
        return highestGraduateInstitutions;
    }

    public void setHighestGraduateInstitutions(String highestGraduateInstitutions) {
        this.highestGraduateInstitutions = highestGraduateInstitutions;
    }

    public String getPrimaryMajor() {
        return primaryMajor;
    }

    public void setPrimaryMajor(String primaryMajor) {
        this.primaryMajor = primaryMajor;
    }

    public String getMasterDuties() {
        return masterDuties;
    }

    public void setMasterDuties(String masterDuties) {
        this.masterDuties = masterDuties;
    }

    public String getProfessionalQualification() {
        return professionalQualification;
    }

    public void setProfessionalQualification(String professionalQualification) {
        this.professionalQualification = professionalQualification;
    }

    public String getTimeOfEvaluation() {
        return timeOfEvaluation;
    }

    public void setTimeOfEvaluation(String timeOfEvaluation) {
        this.timeOfEvaluation = timeOfEvaluation;
    }

    public String getJuniorJobDate() {
        return juniorJobDate;
    }

    public void setJuniorJobDate(String juniorJobDate) {
        this.juniorJobDate = juniorJobDate;
    }

    public String getPutonghuaGrade() {
        return putonghuaGrade;
    }

    public void setPutonghuaGrade(String putonghuaGrade) {
        this.putonghuaGrade = putonghuaGrade;
    }

    public String getPutonghuaGradeNumber() {
        return putonghuaGradeNumber;
    }

    public void setPutonghuaGradeNumber(String putonghuaGradeNumber) {
        this.putonghuaGradeNumber = putonghuaGradeNumber;
    }

    public String getPutonghuaGradeDate() {
        return putonghuaGradeDate;
    }

    public void setPutonghuaGradeDate(String putonghuaGradeDate) {
        this.putonghuaGradeDate = putonghuaGradeDate;
    }

    public String getAppointmentYear() {
        return appointmentYear;
    }

    public void setAppointmentYear(String appointmentYear) {
        this.appointmentYear = appointmentYear;
    }

    public String getBackboneTeacherDate() {
        return backboneTeacherDate;
    }

    public void setBackboneTeacherDate(String backboneTeacherDate) {
        this.backboneTeacherDate = backboneTeacherDate;
    }

    public String getContinuityAge() {
        return continuityAge;
    }

    public void setContinuityAge(String continuityAge) {
        this.continuityAge = continuityAge;
    }

    public String getTeacherCardCode() {
        return teacherCardCode;
    }

    public void setTeacherCardCode(String teacherCardCode) {
        this.teacherCardCode = teacherCardCode;
    }

    public String getTeacherKind() {
        return teacherKind;
    }

    public void setTeacherKind(String teacherKind) {
        this.teacherKind = teacherKind;
    }

    public String getTeacherKindSubject() {
        return teacherKindSubject;
    }

    public void setTeacherKindSubject(String teacherKindSubject) {
        this.teacherKindSubject = teacherKindSubject;
    }

    public String getComputerLevel() {
        return computerLevel;
    }

    public void setComputerLevel(String computerLevel) {
        this.computerLevel = computerLevel;
    }

    public String getComputerLevelNumber() {
        return computerLevelNumber;
    }

    public void setComputerLevelNumber(String computerLevelNumber) {
        this.computerLevelNumber = computerLevelNumber;
    }

    public String getComputerLevelDate() {
        return computerLevelDate;
    }

    public void setComputerLevelDate(String computerLevelDate) {
        this.computerLevelDate = computerLevelDate;
    }

    public String getSeniorityAge() {
        return seniorityAge;
    }

    public void setSeniorityAge(String seniorityAge) {
        this.seniorityAge = seniorityAge;
    }

    public String getDetailedEvaluation() {
        return detailedEvaluation;
    }

    public void setDetailedEvaluation(String detailedEvaluation) {
        this.detailedEvaluation = detailedEvaluation;
    }

    public String getComeSchoolDate() {
        return comeSchoolDate;
    }

    public void setComeSchoolDate(String comeSchoolDate) {
        this.comeSchoolDate = comeSchoolDate;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getResidentialTelephone() {
        return residentialTelephone;
    }

    public void setResidentialTelephone(String residentialTelephone) {
        this.residentialTelephone = residentialTelephone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getBackboneTeacherLevel() {
        return backboneTeacherLevel;
    }

    public void setBackboneTeacherLevel(String backboneTeacherLevel) {
        this.backboneTeacherLevel = backboneTeacherLevel;
    }

    public String getStartOfThe() {
        return startOfThe;
    }

    public void setStartOfThe(String startOfThe) {
        this.startOfThe = startOfThe;
    }

    public String getPostWage() {
        return postWage;
    }

    public void setPostWage(String postWage) {
        this.postWage = postWage;
    }

    public String getContractEndTime() {
        return contractEndTime;
    }

    public void setContractEndTime(String contractEndTime) {
        this.contractEndTime = contractEndTime;
    }

    public String getNameUsedBefore() {
        return nameUsedBefore;
    }

    public void setNameUsedBefore(String nameUsedBefore) {
        this.nameUsedBefore = nameUsedBefore;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getFamilyZipCode() {
        return familyZipCode;
    }

    public void setFamilyZipCode(String familyZipCode) {
        this.familyZipCode = familyZipCode;
    }

    public String getOverseasChinese() {
        return overseasChinese;
    }

    public void setOverseasChinese(String overseasChinese) {
        if("0".equals(overseasChinese)){
            this.overseasChinese = "否";
        }if("1".equals(overseasChinese)) {
            this.overseasChinese = "是";
        }
    }


    public String getTheDepartment() {
        return theDepartment;
    }

    public void setTheDepartment(String theDepartment) {
        this.theDepartment = theDepartment;
    }

    public String getFullTimeTeacher() {
        return fullTimeTeacher;
    }

    public void setFullTimeTeacher(String fullTimeTeacher) {
        this.fullTimeTeacher = fullTimeTeacher;
    }

    public String getClassHeadmaster() {
        return classHeadmaster;
    }

    public void setClassHeadmaster(String classHeadmaster) {
        if("0".equals(classHeadmaster)){
            this.classHeadmaster = "否";
        }if("1".equals(classHeadmaster)) {
            this.classHeadmaster = "是";
        }
    }

    public String getClassHeadmasterYear() {
        return classHeadmasterYear;
    }

    public void setClassHeadmasterYear(String classHeadmasterYear) {
        this.classHeadmasterYear = classHeadmasterYear;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getForeignLanguage() {
        return foreignLanguage;
    }

    public void setForeignLanguage(String foreignLanguage) {
        this.foreignLanguage = foreignLanguage;
    }

    public String getForeignLanguageLevel() {
        return foreignLanguageLevel;
    }

    public void setForeignLanguageLevel(String foreignLanguageLevel) {
        this.foreignLanguageLevel = foreignLanguageLevel;
    }

    public String getPrimarySchoolSystem() {
        return primarySchoolSystem;
    }

    public void setPrimarySchoolSystem(String primarySchoolSystem) {
        this.primarySchoolSystem = primarySchoolSystem;
    }

    public String getHighestSchoolSystem() {
        return highestSchoolSystem;
    }

    public void setHighestSchoolSystem(String highestSchoolSystem) {
        this.highestSchoolSystem = highestSchoolSystem;
    }

    public String getHighestAcademicDegree() {
        return highestAcademicDegree;
    }

    public void setHighestAcademicDegree(String highestAcademicDegree) {
        this.highestAcademicDegree = highestAcademicDegree;
    }

    public String getAcademicDegreeNumber() {
        return academicDegreeNumber;
    }

    public void setAcademicDegreeNumber(String academicDegreeNumber) {
        this.academicDegreeNumber = academicDegreeNumber;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getExpertiseType() {
        return expertiseType;
    }

    public void setExpertiseType(String expertiseType) {
        this.expertiseType = expertiseType;
    }

    public String getTeachingLevel() {
        return teachingLevel;
    }

    public void setTeachingLevel(String teachingLevel) {
        this.teachingLevel = teachingLevel;
    }

    public String getTeachingSubject() {
        return teachingSubject;
    }

    public void setTeachingSubject(String teachingSubject) {
        this.teachingSubject = teachingSubject;
    }

    public String getSchoolCampus() {
        return schoolCampus;
    }

    public void setSchoolCampus(String schoolCampus) {
        this.schoolCampus = schoolCampus;
    }

    public String getProfessionalTechnology() {
        return professionalTechnology;
    }

    public void setProfessionalTechnology(String professionalTechnology) {
        this.professionalTechnology = professionalTechnology;
    }

    public String getProfessionalTechnologyDate() {
        return professionalTechnologyDate;
    }

    public void setProfessionalTechnologyDate(String professionalTechnologyDate) {
        this.professionalTechnologyDate = professionalTechnologyDate;
    }

    public String getSpokenEnglish() {
        return spokenEnglish;
    }

    public void setSpokenEnglish(String spokenEnglish) {
        this.spokenEnglish = spokenEnglish;
    }

    public String getSpokenEnglishNumber() {
        return spokenEnglishNumber;
    }

    public void setSpokenEnglishNumber(String spokenEnglishNumber) {
        this.spokenEnglishNumber = spokenEnglishNumber;
    }

    public String getSpokenEnglishDate() {
        return spokenEnglishDate;
    }

    public void setSpokenEnglishDate(String spokenEnglishDate) {
        this.spokenEnglishDate = spokenEnglishDate;
    }

    public String getAcademicDegreeType() {
        return academicDegreeType;
    }

    public void setAcademicDegreeType(String academicDegreeType) {
        this.academicDegreeType = academicDegreeType;
    }

    public String getSchoolAge() {
        return schoolAge;
    }

    public void setSchoolAge(String schoolAge) {
        this.schoolAge = schoolAge;
    }

    public String getCampFamily() {
        return campFamily;
    }

    public void setCampFamily(String campFamily) {
        if("0".equals(campFamily)){
            this.campFamily = "否";
        }if("1".equals(campFamily)) {
            this.campFamily = "是";
        }
    }

    public String getTeachersCongress() {
        return teachersCongress;
    }

    public void setTeachersCongress(String teachersCongress) {
        if("0".equals(teachersCongress)){
            this.teachersCongress = "否";
        }if("1".equals(teachersCongress)) {
            this.teachersCongress = "是";
        }
    }

    public String getIsTwoShoulders() {
        return isTwoShoulders;
    }

    public void setIsTwoShoulders(String isTwoShoulders) {
        if("0".equals(isTwoShoulders)){
            this.isTwoShoulders = "否";
        }if("1".equals(isTwoShoulders)) {
            this.isTwoShoulders = "是";
        }
    }

    public String getSubstantiveLevel() {
        return substantiveLevel;
    }

    public void setSubstantiveLevel(String substantiveLevel) {
        this.substantiveLevel = substantiveLevel;
    }

    public String getSalaryScale() {
        return salaryScale;
    }

    public void setSalaryScale(String salaryScale) {
        this.salaryScale = salaryScale;
    }

    public String getPostAttach() {
        return postAttach;
    }

    public void setPostAttach(String postAttach) {
        this.postAttach = postAttach;
    }

    public String getPostWageAttach() {
        return postWageAttach;
    }

    public void setPostWageAttach(String postWageAttach) {
        this.postWageAttach = postWageAttach;
    }

    public String getSalaryScaleAttach() {
        return salaryScaleAttach;
    }

    public void setSalaryScaleAttach(String salaryScaleAttach) {
        this.salaryScaleAttach = salaryScaleAttach;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTeachingSection() {
        return teachingSection;
    }

    public void setTeachingSection(String teachingSection) {
        this.teachingSection = teachingSection;
    }
*/


    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getStaffSource() {
        return staffSource;
    }

    public void setStaffSource(String staffSource) {
        this.staffSource = staffSource;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPlaceOfOrigin() {
        return placeOfOrigin;
    }

    public void setPlaceOfOrigin(String placeOfOrigin) {
        this.placeOfOrigin = placeOfOrigin;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getPoliticalOutlook() {
        return politicalOutlook;
    }

    public void setPoliticalOutlook(String politicalOutlook) {
        this.politicalOutlook = politicalOutlook;
    }

    public String getPartyTime() {
        return partyTime;
    }

    public void setPartyTime(String partyTime) {
        this.partyTime = partyTime;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getParticipateWorkDate() {
        return participateWorkDate;
    }

    public void setParticipateWorkDate(String participateWorkDate) {
        this.participateWorkDate = participateWorkDate;
    }

    public String getPrimaryEducation() {
        return primaryEducation;
    }

    public void setPrimaryEducation(String primaryEducation) {
        this.primaryEducation = primaryEducation;
    }

    public String getPrimaryGraduateInstitutionsDate() {
        return primaryGraduateInstitutionsDate;
    }

    public void setPrimaryGraduateInstitutionsDate(String primaryGraduateInstitutionsDate) {
        this.primaryGraduateInstitutionsDate = primaryGraduateInstitutionsDate;
    }

    public String getPrimaryGraduateInstitutions() {
        return primaryGraduateInstitutions;
    }

    public void setPrimaryGraduateInstitutions(String primaryGraduateInstitutions) {
        this.primaryGraduateInstitutions = primaryGraduateInstitutions;
    }

    public String getHighestEducation() {
        return highestEducation;
    }

    public void setHighestEducation(String highestEducation) {
        this.highestEducation = highestEducation;
    }

    public String getHighestGraduateInstitutionsDate() {
        return highestGraduateInstitutionsDate;
    }

    public void setHighestGraduateInstitutionsDate(String highestGraduateInstitutionsDate) {
        this.highestGraduateInstitutionsDate = highestGraduateInstitutionsDate;
    }

    public String getHighestGraduateInstitutions() {
        return highestGraduateInstitutions;
    }

    public void setHighestGraduateInstitutions(String highestGraduateInstitutions) {
        this.highestGraduateInstitutions = highestGraduateInstitutions;
    }

    public String getPrimaryMajor() {
        return primaryMajor;
    }

    public void setPrimaryMajor(String primaryMajor) {
        this.primaryMajor = primaryMajor;
    }

    public String getMasterDuties() {
        return masterDuties;
    }

    public void setMasterDuties(String masterDuties) {
        this.masterDuties = masterDuties;
    }

    public String getProfessionalQualification() {
        return professionalQualification;
    }

    public void setProfessionalQualification(String professionalQualification) {
        this.professionalQualification = professionalQualification;
    }

    public String getTimeOfEvaluation() {
        return timeOfEvaluation;
    }

    public void setTimeOfEvaluation(String timeOfEvaluation) {
        this.timeOfEvaluation = timeOfEvaluation;
    }

    public String getJuniorJobDate() {
        return juniorJobDate;
    }

    public void setJuniorJobDate(String juniorJobDate) {
        this.juniorJobDate = juniorJobDate;
    }

    public String getPutonghuaGrade() {
        return putonghuaGrade;
    }

    public void setPutonghuaGrade(String putonghuaGrade) {
        this.putonghuaGrade = putonghuaGrade;
    }

    public String getPutonghuaGradeNumber() {
        return putonghuaGradeNumber;
    }

    public void setPutonghuaGradeNumber(String putonghuaGradeNumber) {
        this.putonghuaGradeNumber = putonghuaGradeNumber;
    }

    public String getPutonghuaGradeDate() {
        return putonghuaGradeDate;
    }

    public void setPutonghuaGradeDate(String putonghuaGradeDate) {
        this.putonghuaGradeDate = putonghuaGradeDate;
    }

    public String getAppointmentYear() {
        return appointmentYear;
    }

    public void setAppointmentYear(String appointmentYear) {
        this.appointmentYear = appointmentYear;
    }

    public String getBackboneTeacherDate() {
        return backboneTeacherDate;
    }

    public void setBackboneTeacherDate(String backboneTeacherDate) {
        this.backboneTeacherDate = backboneTeacherDate;
    }

    public String getContinuityAge() {
        return continuityAge;
    }

    public void setContinuityAge(String continuityAge) {
        this.continuityAge = continuityAge;
    }

    public String getTeacherCardCode() {
        return teacherCardCode;
    }

    public void setTeacherCardCode(String teacherCardCode) {
        this.teacherCardCode = teacherCardCode;
    }

    public String getTeacherKind() {
        return teacherKind;
    }

    public void setTeacherKind(String teacherKind) {
        this.teacherKind = teacherKind;
    }

    public String getTeacherKindSubject() {
        return teacherKindSubject;
    }

    public void setTeacherKindSubject(String teacherKindSubject) {
        this.teacherKindSubject = teacherKindSubject;
    }

    public String getComputerLevel() {
        return computerLevel;
    }

    public void setComputerLevel(String computerLevel) {
        this.computerLevel = computerLevel;
    }

    public String getComputerLevelNumber() {
        return computerLevelNumber;
    }

    public void setComputerLevelNumber(String computerLevelNumber) {
        this.computerLevelNumber = computerLevelNumber;
    }

    public String getComputerLevelDate() {
        return computerLevelDate;
    }

    public void setComputerLevelDate(String computerLevelDate) {
        this.computerLevelDate = computerLevelDate;
    }

    public String getSeniorityAge() {
        return seniorityAge;
    }

    public void setSeniorityAge(String seniorityAge) {
        this.seniorityAge = seniorityAge;
    }

    public String getDetailedEvaluation() {
        return detailedEvaluation;
    }

    public void setDetailedEvaluation(String detailedEvaluation) {
        this.detailedEvaluation = detailedEvaluation;
    }

    public String getComeSchoolDate() {
        return comeSchoolDate;
    }

    public void setComeSchoolDate(String comeSchoolDate) {
        this.comeSchoolDate = comeSchoolDate;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getResidentialTelephone() {
        return residentialTelephone;
    }

    public void setResidentialTelephone(String residentialTelephone) {
        this.residentialTelephone = residentialTelephone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getBackboneTeacherLevel() {
        return backboneTeacherLevel;
    }

    public void setBackboneTeacherLevel(String backboneTeacherLevel) {
        this.backboneTeacherLevel = backboneTeacherLevel;
    }

    public String getStartOfThe() {
        return startOfThe;
    }

    public void setStartOfThe(String startOfThe) {
        this.startOfThe = startOfThe;
    }

    public String getPostWage() {
        return postWage;
    }

    public void setPostWage(String postWage) {
        this.postWage = postWage;
    }

    public String getContractEndTime() {
        return contractEndTime;
    }

    public void setContractEndTime(String contractEndTime) {
        this.contractEndTime = contractEndTime;
    }

    public String getNameUsedBefore() {
        return nameUsedBefore;
    }

    public void setNameUsedBefore(String nameUsedBefore) {
        this.nameUsedBefore = nameUsedBefore;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getFamilyZipCode() {
        return familyZipCode;
    }

    public void setFamilyZipCode(String familyZipCode) {
        this.familyZipCode = familyZipCode;
    }

    public String getOverseasChinese() {
        return overseasChinese;
    }

    public void setOverseasChinese(String overseasChinese) {
        this.overseasChinese = overseasChinese;
    }

    public String getTheDepartment() {
        return theDepartment;
    }

    public void setTheDepartment(String theDepartment) {
        this.theDepartment = theDepartment;
    }

    public String getFullTimeTeacher() {
        return fullTimeTeacher;
    }

    public void setFullTimeTeacher(String fullTimeTeacher) {
        this.fullTimeTeacher = fullTimeTeacher;
    }

    public String getClassHeadmaster() {
        return classHeadmaster;
    }

    public void setClassHeadmaster(String classHeadmaster) {
        this.classHeadmaster = classHeadmaster;
    }

    public String getClassHeadmasterYear() {
        return classHeadmasterYear;
    }

    public void setClassHeadmasterYear(String classHeadmasterYear) {
        this.classHeadmasterYear = classHeadmasterYear;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getForeignLanguage() {
        return foreignLanguage;
    }

    public void setForeignLanguage(String foreignLanguage) {
        this.foreignLanguage = foreignLanguage;
    }

    public String getForeignLanguageLevel() {
        return foreignLanguageLevel;
    }

    public void setForeignLanguageLevel(String foreignLanguageLevel) {
        this.foreignLanguageLevel = foreignLanguageLevel;
    }

    public String getPrimarySchoolSystem() {
        return primarySchoolSystem;
    }

    public void setPrimarySchoolSystem(String primarySchoolSystem) {
        this.primarySchoolSystem = primarySchoolSystem;
    }

    public String getHighestSchoolSystem() {
        return highestSchoolSystem;
    }

    public void setHighestSchoolSystem(String highestSchoolSystem) {
        this.highestSchoolSystem = highestSchoolSystem;
    }

    public String getHighestAcademicDegree() {
        return highestAcademicDegree;
    }

    public void setHighestAcademicDegree(String highestAcademicDegree) {
        this.highestAcademicDegree = highestAcademicDegree;
    }

    public String getAcademicDegreeNumber() {
        return academicDegreeNumber;
    }

    public void setAcademicDegreeNumber(String academicDegreeNumber) {
        this.academicDegreeNumber = academicDegreeNumber;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getExpertiseType() {
        return expertiseType;
    }

    public void setExpertiseType(String expertiseType) {
        this.expertiseType = expertiseType;
    }

    public String getTeachingLevel() {
        return teachingLevel;
    }

    public void setTeachingLevel(String teachingLevel) {
        this.teachingLevel = teachingLevel;
    }

    public String getTeachingSubject() {
        return teachingSubject;
    }

    public void setTeachingSubject(String teachingSubject) {
        this.teachingSubject = teachingSubject;
    }

    public String getSchoolCampus() {
        return schoolCampus;
    }

    public void setSchoolCampus(String schoolCampus) {
        this.schoolCampus = schoolCampus;
    }

    public String getProfessionalTechnology() {
        return professionalTechnology;
    }

    public void setProfessionalTechnology(String professionalTechnology) {
        this.professionalTechnology = professionalTechnology;
    }

    public String getProfessionalTechnologyDate() {
        return professionalTechnologyDate;
    }

    public void setProfessionalTechnologyDate(String professionalTechnologyDate) {
        this.professionalTechnologyDate = professionalTechnologyDate;
    }

    public String getSpokenEnglish() {
        return spokenEnglish;
    }

    public void setSpokenEnglish(String spokenEnglish) {
        this.spokenEnglish = spokenEnglish;
    }

    public String getSpokenEnglishNumber() {
        return spokenEnglishNumber;
    }

    public void setSpokenEnglishNumber(String spokenEnglishNumber) {
        this.spokenEnglishNumber = spokenEnglishNumber;
    }

    public String getSpokenEnglishDate() {
        return spokenEnglishDate;
    }

    public void setSpokenEnglishDate(String spokenEnglishDate) {
        this.spokenEnglishDate = spokenEnglishDate;
    }

    public String getAcademicDegreeType() {
        return academicDegreeType;
    }

    public void setAcademicDegreeType(String academicDegreeType) {
        this.academicDegreeType = academicDegreeType;
    }

    public String getSchoolAge() {
        return schoolAge;
    }

    public void setSchoolAge(String schoolAge) {
        this.schoolAge = schoolAge;
    }

    public String getCampFamily() {
        return campFamily;
    }

    public void setCampFamily(String campFamily) {
        this.campFamily = campFamily;
    }

    public String getTeachersCongress() {
        return teachersCongress;
    }

    public void setTeachersCongress(String teachersCongress) {
        this.teachersCongress = teachersCongress;
    }

    public String getIsTwoShoulders() {
        return isTwoShoulders;
    }

    public void setIsTwoShoulders(String isTwoShoulders) {
        this.isTwoShoulders = isTwoShoulders;
    }

    public String getSubstantiveLevel() {
        return substantiveLevel;
    }

    public void setSubstantiveLevel(String substantiveLevel) {
        this.substantiveLevel = substantiveLevel;
    }

    public String getSalaryScale() {
        return salaryScale;
    }

    public void setSalaryScale(String salaryScale) {
        this.salaryScale = salaryScale;
    }

    public String getPostAttach() {
        return postAttach;
    }

    public void setPostAttach(String postAttach) {
        this.postAttach = postAttach;
    }

    public String getPostWageAttach() {
        return postWageAttach;
    }

    public void setPostWageAttach(String postWageAttach) {
        this.postWageAttach = postWageAttach;
    }

    public String getSalaryScaleAttach() {
        return salaryScaleAttach;
    }

    public void setSalaryScaleAttach(String salaryScaleAttach) {
        this.salaryScaleAttach = salaryScaleAttach;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTeachingSection() {
        return teachingSection;
    }

    public void setTeachingSection(String teachingSection) {
        this.teachingSection = teachingSection;
    }
}
