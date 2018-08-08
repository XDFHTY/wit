package com.cj.witscorefind.service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

public interface SurviveService {


    //成活率统计
    public Object findSurvive(long newExamParentId, long oldExamParentId,
                              long periodId, String thetime,
                              long classTypeId,
                              BigDecimal upScore, BigDecimal downScore);


    //成活率导出
    public void exportSurvive(long newExamParentId, long oldExamParentId,
                              long periodId, String thetime,
                              long classTypeId,
                              BigDecimal upScore, BigDecimal downScore,
                              HttpServletResponse response) throws Exception;
}
