package com.example.springboot.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;
@Data
public class WarnSignalDto {
    //车架编号
    private Integer carId;
    //规则编号
    private Integer warnId;
    //信号
    private String signal;
}
