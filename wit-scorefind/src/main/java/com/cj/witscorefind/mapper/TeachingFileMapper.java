package com.cj.witscorefind.mapper;

import com.cj.witcommon.utils.entity.other.Pager;
import com.cj.witscorefind.entity.ExamClassSubject;
import com.cj.witscorefind.entity.TeacherClassCurse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 教学档案
 * Created by XD on 2018/6/13.
 */
public interface TeachingFileMapper {

    //根据教职工编号查询所教班级和课程
    List<TeacherClassCurse> findTeachingFile(@Param("staffNumber") String staffNumber,
                                             @Param("schoolId") Long aLong);

    //查询这个班级的这个课程  所有的考试父id
    List<Long> findExamParentIds(Pager pager);

    //查询这几次考试这个班的这个学科的平均分信息
    List<ExamClassSubject> findExamClassSub(Pager pager);

    //查询总条数
    int findExamParentIdsTotal(Pager pager);
}
