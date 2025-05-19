package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;

@Data
@TableName("vehicle")
//车辆信息类

public class Vehicle {

    //随机生成
    private String vid;
    @Id
    //车架编号
    private Integer id;

    //电池类型
    @JsonProperty("battery_type")  // 映射JSON中的battery_type字段
    private String batteryType;

    //总里程
    @JsonProperty("total_mil")
    private Integer totalMil;

    //电池健康状态
    @JsonProperty("bh_state")
    private Integer bhState;

}
