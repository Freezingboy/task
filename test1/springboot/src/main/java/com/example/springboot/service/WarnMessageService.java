package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.entity.WarnMessage;
import com.example.springboot.utils.Result;


public interface WarnMessageService extends IService<WarnMessage> {
    Result add(WarnMessage warnMessage);

    Result get(String id);

    Result updateWarnMessage(WarnMessage warnMessage);

    Result delete(String id);

    Result getAll();

    Result getMessageByCarId(String carId);
}
