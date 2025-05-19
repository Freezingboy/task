
package com.example.springboot.controller;

import com.example.springboot.entity.Rule;
import com.example.springboot.service.RuleService;
import com.example.springboot.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "规则管理")
@CrossOrigin
@RequestMapping("/rule")
public class RuleController {
    @Autowired
    private RuleService ruleService;



    @PostMapping("/add")
    @ApiOperation(value = "添加规则信息")
    public Result add(@RequestBody Rule rule){
        //现在尝试老师的添加方法
        return ruleService.add(rule) ;
    }
    @PostMapping("/get")
    @ApiOperation("获取规则信息")
    public Result get(@RequestParam String id)
    {
        return ruleService.get(id);
    }
    @GetMapping("/getAll")
    @ApiOperation("获取所有规则信息")
    public Result getAll()
    {
        return ruleService.getAll();
    }
    @PostMapping("/delete")
    @ApiOperation("删除规则信息")
    public Result delete(@RequestParam String id)
    {
        return ruleService.delete(id);
    }
    @PostMapping("/update")
    @ApiOperation(value = "更改规则信息")
    @Transactional // 添加事务注解
    public Result updateEmploee(@RequestBody Rule rule){
        return ruleService.updateRule(rule) ;
    }

}