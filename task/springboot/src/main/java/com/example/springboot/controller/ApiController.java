package com.example.springboot.controller;

import com.alibaba.fastjson.JSON;
import com.example.springboot.entity.Rule;
import com.example.springboot.entity.dto.RuleDto;
import com.example.springboot.entity.dto.WarnSignalDto;
import com.example.springboot.mapper.RuleMapper;
import com.example.springboot.service.WarnMessageService;
import com.example.springboot.service.WarnSignalService;
import com.example.springboot.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    @Autowired
    private RuleMapper ruleMapper;
    @PostMapping("/warn")
    @ApiOperation(value = "对一个列表的预警信号信息进行紧急处理")
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

    public Result getJSONString(@RequestParam int id)  {
        //现在尝试老师的添加方法
        Rule rule=ruleMapper.selectById(id);
        Result result=new Result<>();
        if(rule!=null){
            String[] operates=rule.getWarnRule().split("\\|");
            //根据操作数的比对确定报警信息
            Double max= Double.valueOf(operates[0]);
            //用一个List接收
            List<RuleDto>ruleDtos=new ArrayList<>();
            //设置最左的操作数和最右的操作数
            RuleDto ruleDto=new RuleDto();
            ruleDto.setLeft(max);
            ruleDto.setLeftOperator("<=");
            ruleDto.setLevel(0);
            ruleDtos.add(ruleDto);
            //对中间的进行处理
            for(int i=0;i<operates.length-1;i++)
            {
                //获取左操作数与右操作数
                Double right= Double.valueOf(operates[i]);
                Double left= Double.valueOf(operates[i+1]);
//                System.out.println("当前进入left比较left值为"+left);
//                System.out.println("当前进入right比较right值为"+right);
                RuleDto ruleDto1=new RuleDto();
                ruleDto1.setLeft(left);
                ruleDto1.setLeftOperator("<=");
                ruleDto1.setRight(right);
                ruleDto1.setRightOperator("<");
                ruleDto1.setLevel(i+1);
                ruleDtos.add(ruleDto1);
            }
            Double min= Double.valueOf(operates[operates.length-1]);
            RuleDto ruleDto2=new RuleDto();
            ruleDto2.setRight(min);
            ruleDto2.setRightOperator("<");
            ruleDto2.setLevel(-1);
            ruleDtos.add(ruleDto2);
            System.out.println("当前的ruleDtos为:"+ruleDtos);
            result.setData(ruleDtos);
            rule.setWarnRule(JSON.toJSONString(ruleDtos));
            int p=ruleMapper.updateById(rule);
            if(p==1){
                System.out.println("更新成功");
            }else{
                System.out.println("未到");
            }

        }
     return  result;
    }
}
