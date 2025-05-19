package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.example.springboot.entity.Rule;
import com.example.springboot.entity.WarnSignal;
import com.example.springboot.entity.dto.WarnMessageDto;
import com.example.springboot.entity.dto.WarnSignalDto;
import com.example.springboot.utils.Result;

import java.net.UnknownHostException;
import java.util.List;

public interface WarnSignalService extends IService<WarnSignal> {
    Result add(WarnSignalDto warnSignalDto);

    Result get(String id);

    Result updateWarnSignal(WarnSignal warnSignal);

    Result delete(String id);

    Result getAll();

    Result addList(List<WarnSignalDto> warnSignalDtos);

    Result warn(List<WarnSignalDto> warnSignalDtos) throws Exception;
    Result warn1() throws Exception;
     List<WarnMessageDto> handleWarnSignal(WarnSignal warnSignal);

    Result getByCarId(String carId);
}
