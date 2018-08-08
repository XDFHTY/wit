package com.cj.witscorefind.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 得0分的学生学籍号
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterNumberByScoreEq0 {

    //学籍号
    private String registerNumber;
}
