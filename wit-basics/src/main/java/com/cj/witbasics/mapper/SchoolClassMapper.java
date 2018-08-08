package com.cj.witbasics.mapper;

import com.cj.witbasics.entity.ClassSubjectInfo;
import com.cj.witbasics.entity.SchoolClass;
import com.cj.witcommon.entity.PeriodAndThetime;
import com.cj.witcommon.entity.SchoolClassInfo;
import com.cj.witcommon.entity.SchoolPeriodInfo;
import com.cj.witcommon.utils.entity.other.Pager;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

public interface SchoolClassMapper {
    /**
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Long classId);

    /**
     * @mbggenerated
     */
    int insert(SchoolClass record);

    /**
     * @mbggenerated
     */
    int insertSelective(SchoolClass record);

    /**
     * @mbggenerated
     */
    SchoolClass selectByPrimaryKey(Long classId);

    /**
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(SchoolClass record);

    int updateByPrimaryKeySelective2(SchoolClass record);

    /**
     * @mbggenerated
     */
    int updateByPrimaryKey(SchoolClass record);


    //根据班级号修改信息
    int updateByClassNumberSelective(SchoolClass schoolClass);

    //查询班级ID
    long selectByClassNumber(Integer classNumber);

    //查询信息总数
    int selectCountClassInfo(Map<String, Object> map);

    //返回结果
    List<SchoolClassInfo> selectByInfoForLsit(Map<String, Object> map);

    //根据学段ID，判断该学段下有无班级
    int selectCountPeriodId(Long periodId);

    //判断该班级类型下，是否存在班级
    int selectCountClassType(int classTypeId);

    //判断该班级层次下，是否存在班级
    int selectCountClassLevel(int classLevel);

    //根据实体List,批量插入信息
//    int insertBathInfo(SchoolClass info);

    //根据学校ID，返回所有学段和对于的年级信息
    List<SchoolPeriodInfo> findPeriodAndGradeInfo(Long schoolId);

    //根据校区名称查询校区ID
    Long selectByClassName(String classCampus); //待定

    //根据学段ID,查询班级
    List<SchoolClassInfo> selectByPeriodId(@Param("classPeriodId") Long classPeriodId,
                                       @Param("pager") Pager pager);

    int selectCountByPeriodId(Integer periodId);

    //根据年级筛选
    SchoolClassInfo selectByPeriodAndGrade(@Param("sClass") SchoolClassInfo sClass,
                                       @Param("gradeAge") int gradeAge,
                                       @Param("pager") Pager pager);

    //第三层筛选
    SchoolClassInfo selectByVagueParam(@Param("sClass")SchoolClassInfo sclass,
                                             @Param("vague") String vague,
                                             @Param("pager")Pager pager);

    //根据学段届次返回班级
    List<SchoolClassInfo> selectByByPeriodAndThetime(@Param("periodId") Integer periodId,
                                                     @Param("thetime") String thetime,
                                                     @Param("pager") Pager pager);
    //根据学段届次模糊条件返回班级
    List<SchoolClassInfo> selectByByPeriodAndThetimeAndVague(@Param("periodId") Integer periodId,
                                                     @Param("thetime") String thetime,
                                                     @Param("vague") String vague,
                                                     @Param("pager") Pager pager);

    //考试管理，根据学段届次
    List<SchoolClass> selectByByPeriodAndThetimeExam(@Param("periodId") Integer periodId,
                                             @Param("thetime") String thetime);

    //计数
    int selectCountByPeriodIdAndThetime(@Param("periodId") Integer periodId,
                                        @Param("thetime") String thetime);
    //计数
    int selectCountByPeriodAndThetimeAndVague(@Param("periodId") Integer periodId,
                                              @Param("thetime") String thetime,
                                              @Param("vague") String vague);

    //考试模块,查询班级
    SchoolClassInfo findAllClassForYeah(@Param("sClass")SchoolClassInfo sClass, @Param("gradeAge") int gradeAge);

    //获取年级下的班级
    List<SchoolClassInfo> selectByPeriodIdNoPager(Long periodId);

    //根据班级查重
    int selectCountByClassNumber(int classNumber);

    //返回所有无班主任的班级
    List<Map> selectAllNoHeadmaster();

    //置空
    int updateByHeadmasterId(Long classId);


    /*********************************************************/
    /*********************************************************/

    //查询具有班主任权限的角色
    List<Map> findHasPowerForHeadmaster(@Param("vague") String vague);

    //查询具有年级主任权限的角色
    List<Map> findHasPowerForDirector(@Param("vague") String vague);

    //根据学段计数
    String selectInfoByPeriodInfo(String periodName);

    //查重，根据班级号s
    int selectByCountClassNumber(int classNumber);

    /////////////////////////////////////////////////////////
    //根据学段筛选
    List<SchoolClassInfo> selectByPeriodIdUBW(Pager pager);

    //根据届次筛选
    SchoolClassInfo selectByPrimaryKeyByPeriodAndThetime(@Param("info") SchoolClassInfo schoolClassInfo,
                                                         @Param("thetime") String thetime/*,,
                                                         @Param("pager") Pager pager*/);

    //根据模糊查询
    SchoolClassInfo selectByVagueParamUBW(  Pager pager);

    /**
     * =========================================================
     */
    //查询学段、届次、班级信息 树形结构
    public List<PeriodAndThetime> findAllPeriodAndThetimeAndClassBySchoolId(Long schoolId);


    //根据班级id和课程id查询任课教师
    ClassSubjectInfo findTeacher(@Param("classId") Long classId, @Param("subjectId") Long subjectId);

    //根据班级id和课程id 添加教师
    int insertTeacher(@Param("classId")Long classId,@Param("subjectId") Long subjectId,@Param("adminId") Long adminId);

    //修改班级课程的教师
    int updateTeacher(ClassSubjectInfo info);
}

