package com.cj.witscorefind.service;

import com.cj.witscorefind.entity.SchoolExamParents;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * 学生成绩档案
 */
public interface StudentScoreArchivesService {

    //查询学生成绩档案
    public List<SchoolExamParents> findAllExamScore(String registerNumber);

    //导出学生成绩档案
    public void exportAllExamScore(HttpSession session , HttpServletResponse response, String registerNumber) throws Exception;
}
