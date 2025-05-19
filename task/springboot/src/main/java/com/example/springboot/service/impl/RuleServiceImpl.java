package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Rule;
import com.example.springboot.mapper.RuleMapper;
import com.example.springboot.redis.RedisCache;
import com.example.springboot.service.RuleService;
import com.example.springboot.utils.Result;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RuleServiceImpl extends ServiceImpl<RuleMapper, Rule> implements RuleService {
    @Autowired
    private  RuleMapper ruleMapper;
    @Autowired
    private final RedisCache redisCache;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;
    public RuleServiceImpl(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    @Override
    public Result add(Rule rule) {
        Result result=new Result<>();
        Rule rule1=ruleMapper.selectById(rule.getId());
        if (rule1!=null)
        {
            result.setMessage("当前id已被绑定，不可插入");
            return result;
        }
        result.setMessage("ok");
        result.setData(ruleMapper.insert(rule));
        return result ;
    }

    @Override
    public Result get(String id) {
        Result result=new Result<>();
        String cacheKey = "rule:" + id;
        Rule rule= (Rule) redisCache.get(cacheKey);
        if (rule != null) {
            result.setMessage("当前从redis获取数据");
            result.setData(rule);
            return result;
        }
        // 缓存未命中，查数据库
        rule = ruleMapper.selectById(id);
        if (rule != null) {
            redisCache.set(cacheKey, rule, 5); // 缓存5分钟
            System.out.println("当前的rule为"+rule);
        }
        if(rule!=null){
            result.setMessage("当前从数据库获取数据并调整到redis中");
            result.setData(rule);
        }
        else{
            result.setMessage("当前数据库也没有数据 查询不到该数据");
            result.setData(rule);
        }
        return result;
    }
    @Override
    public Result updateRule(Rule rule) {
        //现在尝试老师的redis读取方法
        String lockKey = "Rule:Lock_2:" + rule.getId();
        RLock ruleLock = redissonClient.getLock(lockKey);
        Result result=new Result();
        try {
            if (!ruleLock.tryLock()) {
                System.out.println("未获取到锁" + rule.getId());
                result.setMessage("未获取到锁" + rule.getId());
            }
            ruleLock.lock(5, TimeUnit.MINUTES);
            result.setData( ruleMapper.updateById(rule));
            redisCache.delete("rule:" + rule.getId()); // 删除旧缓存
            result.setMessage("成功将数据库中的数据进行更改并进行redis的删除同步");

            Thread.sleep(2000);
        } catch (Exception e) {
            //log.error
            e.printStackTrace();
        } finally {
            //这里是redission的机制 会要求必须检查是否是当前线程持有锁 所以必修添加下面一个委外条件进行判断
            if (ruleLock.isLocked() && ruleLock.isHeldByCurrentThread()) {
                ruleLock.forceUnlock();
            }
        }
        return result;
    }
    @Override
    public Result delete(String id) {
        //尝试老师上课教的方法
        Result result=new Result<>();
        int p =ruleMapper.deleteById(id);
        redisCache.delete("rule:" + id); // 删除缓存
        if(p==1){
            result.setMessage("已成功将数据从redis与数据库中移除");
        }
        else{
            result.setMessage("该数据不存在");
        }
        return result;
    }

    @Override
    public Result getAll() {
        QueryWrapper<Rule>queryWrapper=new QueryWrapper<>();
        queryWrapper.like("id","");
        List<Rule> vehicleList=ruleMapper.selectList(queryWrapper);
        Result result=new Result<>();
        result.setData(vehicleList);
        result.setMessage("已成功获取当前所有规则数据");
        return result;
    }
}
