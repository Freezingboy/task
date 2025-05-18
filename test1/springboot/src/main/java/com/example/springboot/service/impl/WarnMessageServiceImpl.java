package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.WarnMessage;
import com.example.springboot.entity.WarnSignal;
import com.example.springboot.mapper.WarnMessageMapper;
import com.example.springboot.mapper.WarnSignalMapper;
import com.example.springboot.redis.RedisCache;
import com.example.springboot.service.WarnMessageService;
import com.example.springboot.utils.Result;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class WarnMessageServiceImpl extends ServiceImpl<WarnMessageMapper, WarnMessage> implements WarnMessageService {
    @Autowired
    private  WarnMessageMapper warnMessageMapper;
    @Autowired
    private WarnSignalMapper warnSignalMapper;
    @Autowired
    private final RedisCache redisCache;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;
    public WarnMessageServiceImpl(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    @Override
    public Result add(WarnMessage warnMessage) {
        Result result=new Result<>();
        WarnMessage warnMessage1=warnMessageMapper.selectById(warnMessage.getId());
        if (warnMessage1!=null)
        {
            result.setMessage("当前id已被绑定，不可插入");
            return result;
        }
        result.setData(warnMessageMapper.insert(warnMessage));
        return result ;
    }

    @Override
    public Result get(String id) {
        Result result=new Result<>();
        String cacheKey = "warnMessage:" + id;
        WarnMessage warnMessage= (WarnMessage) redisCache.get(cacheKey);
        if (warnMessage != null) {
            result.setMessage("当前从redis获取数据");
            result.setData(warnMessage);
            return result;
        }
        // 缓存未命中，查数据库
        warnMessage = warnMessageMapper.selectById(id);
        if (warnMessage != null) {
            redisCache.set(cacheKey, warnMessage, 5); // 缓存5分钟
            System.out.println("当前的warnMessage为"+warnMessage);
        }
        if(warnMessage!=null){
            result.setMessage("当前从数据库获取数据并调整到redis中");
            result.setData(warnMessage);
        }
        else{
            result.setMessage("当前数据库也没有数据 查询不到该数据");
            result.setData(warnMessage);
        }
        return result;
    }
    @Override
    public Result updateWarnMessage(WarnMessage warnMessage) {
        //现在尝试老师的redis读取方法
        String lockKey = "WarnMessage:Lock_2:" + warnMessage.getId();
        RLock warnMessageLock = redissonClient.getLock(lockKey);
        Result result=new Result();
        try {
            if (!warnMessageLock.tryLock()) {
                System.out.println("未获取到锁" + warnMessage.getId());
                result.setMessage("未获取到锁" + warnMessage.getId());

            }
            warnMessageLock.lock(5, TimeUnit.MINUTES);
            result.setData( warnMessageMapper.updateById(warnMessage));
            redisCache.delete("warnMessage:" + warnMessage.getId()); // 删除旧缓存
            result.setMessage("成功将数据库中的数据进行更改并进行redis的删除同步");

            Thread.sleep(2000);
        } catch (Exception e) {
            //log.error
            e.printStackTrace();
        } finally {
            //这里是redission的机制 会要求必须检查是否是当前线程持有锁 所以必修添加下面一个委外条件进行判断
            if (warnMessageLock.isLocked() && warnMessageLock.isHeldByCurrentThread()) {
                warnMessageLock.forceUnlock();
            }
        }
        return result;
    }
    @Override
    public Result delete(String id) {
        //尝试老师上课教的方法
        Result result=new Result<>();
        int p =warnMessageMapper.deleteById(id);
        redisCache.delete("warnMessage:" + id); // 删除缓存
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
        QueryWrapper<WarnMessage> queryWrapper=new QueryWrapper<>();
        queryWrapper.like("id","");
        List<WarnMessage> warnMessageList=warnMessageMapper.selectList(queryWrapper);
        Result result=new Result<>();
        result.setData(warnMessageList);
        result.setMessage("已成功获取当前所有车辆数据");
        return result;
    }

    @Override
    public Result getMessageByCarId(String carId) {
        Result result=new Result<>();
        //首先根据carId获取到对应的signalId
        LambdaQueryWrapper<WarnSignal>warnSignalLambdaQueryWrapper=new LambdaQueryWrapper<>();
        warnSignalLambdaQueryWrapper.eq(WarnSignal::getCarId,carId);
        List<WarnSignal>warnSignals=warnSignalMapper.selectList(warnSignalLambdaQueryWrapper);
        List<WarnMessage>totalWarnMessageList=null;
        if(warnSignals!=null&&warnSignals.size()!=0){
            totalWarnMessageList=new ArrayList<>();
            for(WarnSignal warnSignal:warnSignals){
                LambdaQueryWrapper<WarnMessage>warnMessageLambdaQueryWrapper=new LambdaQueryWrapper<>();
                warnMessageLambdaQueryWrapper.eq(WarnMessage::getSignalId,warnSignal.getId());
                warnMessageLambdaQueryWrapper.like(WarnMessage::getWarnName,"差报警");
                List<WarnMessage>warnMessageList=warnMessageMapper.selectList(warnMessageLambdaQueryWrapper);
                totalWarnMessageList.addAll(warnMessageList);
            }

        }
        result.setData(totalWarnMessageList);
        result.setMessage("ok");
        result.setCode(200);
        return result;
    }
}
