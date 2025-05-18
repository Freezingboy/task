package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.entity.Rule;
import com.example.springboot.entity.Vehicle;
import com.example.springboot.utils.Result;


public interface RuleService extends IService<Rule> {
    Result add(Rule rule);

    Result get(String id);

    Result updateRule(Rule rule);

    Result delete(String id);

    Result getAll();
}
