
package com.example.springboot.controller;

import com.example.springboot.entity.WarnSignal;
import com.example.springboot.entity.dto.WarnSignalDto;
import com.example.springboot.service.WarnSignalService;
import com.example.springboot.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "电池信号管理")
@CrossOrigin
@RequestMapping("/signal")
public class WarnSignalController {
    @Autowired
    private WarnSignalService warnSignalService;

    @PostMapping("/add")
    @ApiOperation(value = "添加电池信号信息")
    public Result add(@RequestBody WarnSignalDto warnSignalDto){
        //现在尝试老师的添加方法
        return warnSignalService.add(warnSignalDto) ;
    }
    @PostMapping("/addList")
    @ApiOperation(value = "添加一个列表的电池信号信息")
    public Result addList(@RequestBody List<WarnSignalDto> warnSignalDtos){
        //现在尝试老师的添加方法
        return warnSignalService.addList(warnSignalDtos) ;
    }
    @PostMapping("/get")
    @ApiOperation("获取电池信号信息")
    public Result get(@RequestParam String id)
    {
        return warnSignalService.get(id);
    }
    @PostMapping("/getByCarId")
    @ApiOperation("根据车架id获取电池信号信息")
    public Result getByCarId(@RequestParam String carId)
    {
        return warnSignalService.getByCarId(carId);
    }
    @GetMapping("/getAll")
    @ApiOperation("获取所有电池信号信息")
    public Result getAll()
    {
        return warnSignalService.getAll();
    }
    @PostMapping("/delete")
    @ApiOperation("删除电池信号信息")
    public Result delete(@RequestParam String id)
    {
        return warnSignalService.delete(id);
    }
    @PostMapping("/update")
    @ApiOperation(value = "更改电池信号信息")
    @Transactional // 添加事务注解
    public Result updateWarnSignal(@RequestBody WarnSignal warnSignal){
        return warnSignalService.updateWarnSignal(warnSignal) ;
    }

}