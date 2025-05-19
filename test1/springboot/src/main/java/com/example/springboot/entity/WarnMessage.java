package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.springboot.entity.dto.WarnMessageDto;
import com.example.springboot.entity.dto.WarnSignalDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;


@Data
@TableName("warn_message")
public class WarnMessage {
    @Id
    //警告信息id
    private int id;
    //车架编号
    @JsonProperty("car_id")  // 映射JSON中的car_id字段
    private Integer carId;
    //电池类型
    @JsonProperty("battery_type")  // 映射JSON中的battery_type字段
    private String batteryType;
    //警告名
    @JsonProperty("warn_name")  // 映射JSON中的car_id字段
    private String warnName;
    //警告等级
    @JsonProperty("warn_level")  // 映射JSON中的car_id字段
    private Integer warnLevel;
    //信号id
    @JsonProperty("signal_id")  // 映射JSON中的car_id字段
    private String signalId;

    public WarnMessage(WarnMessageDto warnMessageDto){
        this.carId=warnMessageDto.getCarId();
        this.batteryType=warnMessageDto.getBatteryType();
        this.warnName=warnMessageDto.getWarnName();
        this.warnLevel=warnMessageDto.getWarnLevel();
    }
    public WarnMessage(){

    }
}