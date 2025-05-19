package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;

@Data
@TableName("rule")
//车辆信息类

public class Rule {

    @Id
    //车架编号
    private Integer id;

    //规则编号
    @JsonProperty("warn_id")  // 映射JSON中的battery_type字段
    private Integer warnId;

    //名称
    private String name;

    //电池类型
    @JsonProperty("battery_type")
    private String batteryType;

    //预警规则
    @JsonProperty("warn_rule")
    private String warnRule;

}