package com.cj.witscorefind.service;

import com.cj.witcommon.entity.ApiResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

public interface StudentScoreService {


    //验证excle参数是否正确
    public Map checkExcle(MultipartFile multipartFile);

    /**
     * 批量导入
     */
    ApiResult bathImportInfo(MultipartFile file, InputStream in, Map params, Long operatorId, Integer modelNumber);


}
