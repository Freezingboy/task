package com.example.springboot.controller;

import com.example.springboot.entity.dto.WarnSignalDto;
import com.example.springboot.service.WarnMessageService;
import com.example.springboot.service.WarnSignalService;
import com.example.springboot.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "外部api管理")
@CrossOrigin
@RequestMapping("/api")
public class ApiController {
    @Autowired
    private WarnSignalService warnSignalService;
    @Autowired
    private WarnMessageService warnMessageService;
    @PostMapping("/warn")
    @ApiOperation(value = "对一个列表的预警信号信息进行预警处理")
    public Result warn(@RequestBody List<WarnSignalDto> warnSignalDtos) throws Exception {
        //现在尝试老师的添加方法
        return warnSignalService.warn(warnSignalDtos) ;
    }
    @GetMapping("/getMessageByCarId")
    @ApiOperation("获取指定车架id下所有警告信息")
    public Result getMessageByCarId(@RequestParam String carId)
    {
        return warnMessageService.getMessageByCarId(carId);
    }

}
