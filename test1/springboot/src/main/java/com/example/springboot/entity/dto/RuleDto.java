package com.example.springboot.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RuleDto {
    //左操作数
    private Double left;
    //右操作数
    private Double right;
    //左操作符
    @JsonProperty("left_operator")  // 映射JSON中的left_operator字段
    private String leftOperator;
    //右操作符
    @JsonProperty("right_operator")  // 映射JSON中的right_operator字段
    private String rightOperator;

    //警告等级（-1代表不报警 0及以上代表报警等级）
    private Integer level;
}
