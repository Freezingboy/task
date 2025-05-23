package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.springboot.entity.dto.WarnSignalDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import nonapi.io.github.classgraph.json.Id;

import java.time.LocalDateTime;

@Data
@TableName("warn_signal")
public class WarnSignal {
    @Id
    //预警信号id
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    //车架编号
    @JsonProperty("car_id")  // 映射JSON中的car_id字段
    private Integer carId;
    //规则编号
    @JsonProperty("warn_id")  // 映射JSON中的warn_id字段
    private Integer warnId;
    //信号
    private String cwsignal;
    //处理状态 1代表处理了 0代表未处理
    @TableField(value = "signal_state")  // 映射JSON中的signal_state字段
    private int signalState;
    @JsonProperty("create_time")  // 映射JSON中的signal_state字段
    private LocalDateTime createTime;
    public WarnSignal(WarnSignalDto warnSignalDto){
        this.cwsignal=warnSignalDto.getSignal();
        this.carId=warnSignalDto.getCarId();
        this.warnId=warnSignalDto.getWarnId();
    }
    public WarnSignal(){

    }
}
