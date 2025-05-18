package com.example.springboot.controller;

import com.example.springboot.entity.Vehicle;
import com.example.springboot.entity.WarnMessage;
import com.example.springboot.service.VehicleService;
import com.example.springboot.service.WarnMessageService;
import com.example.springboot.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(tags = "警告信息管理")
@CrossOrigin
@RequestMapping("/warn_message")
public class WarnMessageController {
    @Autowired
    private WarnMessageService warnMessageService;



    @PostMapping("/add")
    @ApiOperation(value = "添加警告信息信息")
    public Result add(@RequestBody WarnMessage warnMessage){
        //现在尝试老师的添加方法
        return warnMessageService.add(warnMessage) ;
    }
    @PostMapping("/get")
    @ApiOperation("获取警告信息信息")
    public Result get(@RequestParam String id)
    {
        return warnMessageService.get(id);
    }

    @GetMapping("/getAll")
    @ApiOperation("获取所有警告信息")
    public Result getAll()
    {
        return warnMessageService.getAll();
    }
    @PostMapping("/delete")
    @ApiOperation("删除警告信息信息")
    public Result delete(@RequestParam String id)
    {
        return warnMessageService.delete(id);
    }
    @PostMapping("/update")
    @ApiOperation(value = "更改警告信息信息")
    @Transactional // 添加事务注解
    public Result updateEmploee(@RequestBody WarnMessage warnMessage){
        return warnMessageService.updateWarnMessage(warnMessage) ;
    }

}