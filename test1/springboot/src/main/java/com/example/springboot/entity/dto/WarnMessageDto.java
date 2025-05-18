package com.example.springboot.entity.dto;

import lombok.Data;


@Data
public class WarnMessageDto {
    //车架编号
    private Integer carId;
    //电池类型
    private String batteryType;
    //警告名
    private String warnName;
    //警告等级
    private int warnLevel;
}