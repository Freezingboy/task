package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Vehicle;
import com.example.springboot.mapper.VehicleMapper;
import com.example.springboot.redis.RedisCache;
import com.example.springboot.service.VehicleService;
import com.example.springboot.utils.Result;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements VehicleService {
    @Autowired
    private  VehicleMapper vehicleMapper;
    @Autowired
    private final RedisCache redisCache;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;
    public VehicleServiceImpl(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    @Override
    public Result add(Vehicle vehicle) {
        Result result=new Result<>();
        Vehicle vehicle1=vehicleMapper.selectById(vehicle.getId());
        if (vehicle1!=null)
        {
            result.setMessage("当前id已被绑定，不可插入");
            return result;
        }
        UUID uuid = UUID.randomUUID();
        // 打印原始UUID
        System.out.println("生成的UUID是: " + uuid.toString());
        // 转换为16位字符串
        String shortUuid = uuid.toString().replace("-", "").substring(0, 16);
        System.out.println("16位字符串是: " + shortUuid);
        vehicle.setVid(shortUuid);
        result.setData(vehicleMapper.insert(vehicle));
        return result ;
    }

    @Override
    public Result get(String id) {
        Result result=new Result<>();
        String cacheKey = "vehicle:" + id;
        Vehicle vehicle= (Vehicle) redisCache.get(cacheKey);
        if (vehicle != null) {
            result.setMessage("当前从redis获取数据");
            result.setData(vehicle);
            return result;
        }
        // 缓存未命中，查数据库
        vehicle = vehicleMapper.selectById(id);
        if (vehicle != null) {
            redisCache.set(cacheKey, vehicle, 5); // 缓存5分钟
            System.out.println("当前的vehicle为"+vehicle);
        }
        if(vehicle!=null){
            result.setMessage("当前从数据库获取数据并调整到redis中");
            result.setData(vehicle);
        }
        else{
            result.setMessage("当前数据库也没有数据 查询不到该数据");
            result.setData(vehicle);
        }
        return result;
    }
    @Override
    public Result updateVehicle(Vehicle vehicle) {
        //现在尝试老师的redis读取方法
        String lockKey = "Vehicle:Lock_2:" + vehicle.getId();
        RLock vehicleLock = redissonClient.getLock(lockKey);
        Result result=new Result();
        try {
            if (!vehicleLock.tryLock()) {
                System.out.println("未获取到锁" + vehicle.getId());
                result.setMessage("未获取到锁" + vehicle.getId());

            }
            vehicleLock.lock(5, TimeUnit.MINUTES);
            result.setData( vehicleMapper.updateById(vehicle));
            redisCache.delete("vehicle:" + vehicle.getId()); // 删除旧缓存
            result.setMessage("成功将数据库中的数据进行更改并进行redis的删除同步");

            Thread.sleep(2000);
        } catch (Exception e) {
            //log.error
            e.printStackTrace();
        } finally {
            //这里是redission的机制 会要求必须检查是否是当前线程持有锁 所以必修添加下面一个委外条件进行判断
            if (vehicleLock.isLocked() && vehicleLock.isHeldByCurrentThread()) {
                vehicleLock.forceUnlock();
            }
        }
        return result;
    }
    @Override
    public Result delete(String id) {
        //尝试老师上课教的方法
        Result result=new Result<>();
        int p =vehicleMapper.deleteById(id);
        redisCache.delete("vehicle:" + id); // 删除缓存
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
        QueryWrapper<Vehicle>queryWrapper=new QueryWrapper<>();
        queryWrapper.like("id","");
        List<Vehicle> vehicleList=vehicleMapper.selectList(queryWrapper);
        Result result=new Result<>();
        result.setData(vehicleList);
        result.setMessage("已成功获取当前所有车辆数据");
        return result;
    }
}
