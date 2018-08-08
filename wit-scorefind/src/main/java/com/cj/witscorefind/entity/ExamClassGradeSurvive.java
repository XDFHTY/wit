package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 档次-成活率
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamClassGradeSurvive {

    //成活率名称
    private String surviveName;

    //成活率
    private String survive;
}
