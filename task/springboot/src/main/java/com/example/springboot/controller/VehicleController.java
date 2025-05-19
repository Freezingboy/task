package com.example.springboot.controller;

import com.example.springboot.entity.Vehicle;
import com.example.springboot.service.VehicleService;
import com.example.springboot.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "车辆管理")
@CrossOrigin
@RequestMapping("/vehicle")
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;



    @PostMapping("/add")
    @ApiOperation(value = "添加车辆信息")
    public Result add(@RequestBody Vehicle vehicle){
        //现在尝试老师的添加方法
        return vehicleService.add(vehicle) ;
    }
    @PostMapping("/get")
    @ApiOperation("获取车辆信息")
    public Result get(@RequestParam String id)
    {
        return vehicleService.get(id);
    }
    @GetMapping("/getAll")
    @ApiOperation("获取所有车辆信息")
    public Result getAll()
    {
        return vehicleService.getAll();
    }
    @PostMapping("/delete")
    @ApiOperation("删除车辆信息")
    public Result delete(@RequestParam String id)
    {
        return vehicleService.delete(id);
    }
    @PostMapping("/update")
    @ApiOperation(value = "更改车辆信息")
    @Transactional // 添加事务注解
    public Result updateEmploee(@RequestBody Vehicle vehicle){
        return vehicleService.updateVehicle(vehicle) ;
    }

}