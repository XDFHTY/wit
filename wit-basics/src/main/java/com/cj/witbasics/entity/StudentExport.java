package com.cj.witbasics.entity;

import com.cj.witcommon.utils.excle.IsNeeded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 学生信息导出 实体类
 * Created by XD on 2018/5/21.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentExport {

    /**
     * 校内学号
     */
    @IsNeeded
    private String schoolNumber;

    /**
     * 班内学号
     */
    @IsNeeded
    private String classNumber;

    /**
     * 会考号
     */
    @IsNeeded
    private String examinationNumber;

    /**
     * 学籍号
     */
    @IsNeeded
    private String registerNumber;

    /**
     * 姓名
     */
    @IsNeeded
    private String fullName;

    /**
     * 性别（0-保密，1-男，2-女,默认为0）
     */
    @IsNeeded
    private String gender;

    public void setGender(String gender) {
        if("男".equals(gender) || "1".equals(gender)){
            this.gender = "男";
        }else if("女".equals(gender) || "2".equals(gender)){
            this.gender = "女";
        }else {
            this.gender = "保密";
        }
    }



    //校区名称
    @IsNeeded
    private String basicIdName;



    //学段名称
    @IsNeeded
    private String periodIdName;



    //年级名称
    @IsNeeded
    private String gradeIdName;


    /**
     * 班级名称
     */
    private String classIdName;


    /**
     * 出生日期
     */
    @IsNeeded
    private String birthDate;

    /**
     * 有效证件类型(01-居民身份证、02-出生证、03-护照)
     */
    @IsNeeded
    private String cardType;

    /**
     * 有效证件号
     */
    @IsNeeded
    private String cardNumber;



    /**
     * 教育Id号
     */
    @IsNeeded
    private String educationId;

    /**
     * 全国学籍号
     */
    @IsNeeded
    private String nationalRegisterNumber;

    /**
     * 国别
     */
    @IsNeeded
    private String differentCountries;

    /**
     * 港澳台侨
     */
    @IsNeeded
    private String hongKong;

    /**
     * 户口所在地
     */
    @IsNeeded
    private String registeredResidence;

    /**
     * 户口所在地-详细地址
     */
    @IsNeeded
    private String registeredResidenceDetailed;

    /**
     * 政治面貌(01 中共党员 02 中共预备党员 03共青团员 04 民革党员 05 民盟盟员 06 民建会员 07 民进会员 08 农工党党员 09 致公党党员 10 九三学社社员 11 台盟盟员 12 无党派人士 13群众)
     */
    @IsNeeded
    private String politicalOutlook;

    /**
     * 民族(前端单选）
     */
    @IsNeeded
    private String nation;

    /**
     * 学生类别（前端单选）
     */
    @IsNeeded
    private String studentType;

    /**
     * 就读方式（前端单选）
     */
    @IsNeeded
    private String wayOfStudying;

    /**
     * 是否按本市户口学生对待(0-否，1-是)
     */
    @IsNeeded
    private String treatType;

    /**
     * 现住址
     */
    @IsNeeded
    private String presentAddress;

    /**
     * 家庭住址
     */
    @IsNeeded
    private String homeAddress;

    /**
     * 家庭住址(详细)
     */
    @IsNeeded
    private String homeAddressDetailed;

    /**
     * 通讯地址
     */
    @IsNeeded
    private String postalAddress;

    /**
     * 出生地
     */
    @IsNeeded
    private String nativeHeath;

    /**
     * 籍贯
     */
    @IsNeeded
    private String placeOfOrigin;

    /**
     * 健康状况（前端单选）
     */
    @IsNeeded
    private String healthCondition;

    /**
     * 户口性质
     */
    @IsNeeded
    private String householdRegistration;

    /**
     * 手机号
     */
    @IsNeeded
    private String mobilePhone;

    /**
     * 邮政编码
     */
    @IsNeeded
    private String zipCode;

    /**
     * 过敏史
     */
    @IsNeeded
    private String anaphylaxis;

    /**
     * 既往病史
     */
    @IsNeeded
    private String pastMedicalHistory;

    /**
     * 独生子女(0-否，1-是)
     */
    @IsNeeded
    private String oneChild;

    /**
     * 是否是军区子弟(0-否，1-是)
     */
    @IsNeeded
    private String militaryRegion;

    /**
     * 受过学前教育(0-否，1-是)
     */
    @IsNeeded
    private String preschoolEducation;

    /**
     * 留守儿童(0-否，1-是)
     */
    @IsNeeded
    private String leftBehindChildren;

    /**
     * 进城务工子女(0-否，1-是)
     */
    @IsNeeded
    private String workForChildren;

    /**
     * 姓名拼音
     */
    @IsNeeded
    private String namePinyin;

    /**
     * 英文名
     */
    @IsNeeded
    private String englishName;

    /**
     * 是否本市学籍(0-否，1-是)
     */
    @IsNeeded
    private String thisCityOsaas;

    /**
     * 招生类别（前端单选）
     */
    @IsNeeded
    private String enrolmentCategory;

    /**
     * 学籍辅号
     */
    @IsNeeded
    private String registerAssistNumber;

    /**
     * 随班就读
     */
    @IsNeeded
    private String learningInClass;

    /**
     * 学生状态
     */
    @IsNeeded
    private String studentState;

    /**
     * 是否残疾 0-否 1-是
     */
    @IsNeeded
    private String isDisability;

    /**
     * 居住地0—住校 1-走读 2-借宿 3-其他
     */
    @IsNeeded
    private String domicile;

    /**
     * 是否孤儿 0-否 1-是
     */
    @IsNeeded
    private String orphan;

    /**
     * 是否烈士优抚子女0-否 1-是
     */
    @IsNeeded
    private String martyrChild;

    /**
     * 是否需要资助0-否 1-是
     * 是否需要申请资助
     */
    @IsNeeded
    private String needSubsidy;

    /**
     * 是否享受一补0-否 1-是
     */
    @IsNeeded
    private String isSubsidy;

    /**
     * 入学方式（前端单选）
     */
    @IsNeeded
    private String enrolmentMode;

    /**
     * 学生来源
     */
    @IsNeeded
    private String studentSource;

    /**
     * 入学日期
     */
    @IsNeeded
    private String dateOfAdmission;

    /**
     * 学生来处
     */
    @IsNeeded
    private String studentsComeIn;

    /**
     * 原学校名称
     */
    @IsNeeded
    private String originalSchool;

}
