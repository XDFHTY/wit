package com.cj.witbasics.mapper;

import com.cj.witbasics.entity.*;
import com.cj.witcommon.entity.ClassGradeInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StudentScoreMapper {
    /**
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long scoreId);

    /**
     *
     * @mbggenerated
     */
    int insert(StudentScore record);

    /**
     *
     * @mbggenerated
     */
    int insertSelective(StudentScore record);

    /**
     *
     * @mbggenerated
     */
    StudentScore selectByPrimaryKey(Long scoreId);

    /**
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(StudentScore record);

    /**
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(StudentScore record);

    /**
     * 根据学籍号,科目ID查重
     */
    int selectByCountScoreId( Map map);

    /**
     * 批量插入
     */
    int insertBathInfo(List<StudentScore> list);

    /**
     * 成绩查询,班主任权限
     */
    List<ClassGradeInfo> selectPowerByHeadmaster(Long classId);

    /**
     * 成绩查询,科目教师权限
     * @param
     * @return
     */
    List<ClassGradeInfo> selectPowerBySubjectTeacher(@Param("list") List<ClassSubjectInfo> list);


    /**
     *根据学段ID,毕业届次查询
     */
    List<ClassGradeInfo> selectPowerByPeriodIdAndThetime(@Param("info") PeriodDirectorThetime info);

    /**
     * 分数查询
     */
    List<Map> selectByClassIdAndSubjectId(@Param("classId") Long classId,
                                          @Param("subjectId") Long subjectId);

    List<ClassGradeInfo> selectBathInfo(@Param("list") List<Long> classIdList);

    /**
     * 批量更新
     */
    int updateBatchInfo(@Param("list")  List<StudentScore> params);


    /**
     * 更新
     */
    int updateByPrimaryBySome(StudentScore record);


    /**
     * 根据ID,查询管理员
     */
    Admin selectAdminInfoById(Long adminId);

    /**
     * 根据角色Id，查询角色类
     */
    AdminRole selectAdminRoleByRoleId(Long roleId);

}