


package com.example.springboot.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.*;
import com.example.springboot.entity.dto.WarnMessageDto;
import com.example.springboot.entity.dto.WarnSignalDto;
import com.example.springboot.mapper.RuleMapper;
import com.example.springboot.mapper.VehicleMapper;
import com.example.springboot.mapper.WarnSignalMapper;
import com.example.springboot.redis.RedisCache;
import com.example.springboot.service.WarnSignalService;
import com.example.springboot.utils.Result;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class WarnSignalServiceImpl extends ServiceImpl<WarnSignalMapper, WarnSignal> implements WarnSignalService {
    @Autowired
    private  WarnSignalMapper warnSignalMapper;
    @Autowired
    private VehicleMapper vehicleMapper;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private final RedisCache redisCache;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;
    public WarnSignalServiceImpl(RedisCache redisCache) {
        this.redisCache = redisCache;
    }
    @Resource
    private Producer springProducer;
    @Override
    public Result add(WarnSignalDto warnSignalDto) {
        WarnSignal warnSignal=new WarnSignal(warnSignalDto);
        Result result=new Result<>();
        warnSignal.setSignalState(0);
        result.setData(warnSignalMapper.insert(warnSignal));
        result.setCode(200);
        return result ;
    }
    @Override
    public Result addList(List<WarnSignalDto> warnSignalDtos) {
        Result result = new Result<>();
        List<WarnSignal>warnSignals=new ArrayList<>();
        for(WarnSignalDto warnSignalDto:warnSignalDtos) {
            WarnSignal warnSignal = new WarnSignal(warnSignalDto);
//            UUID uuid = UUID.randomUUID();
//            // 打印原始UUID
////        System.out.println("生成的UUID是: " + uuid.toString());
//            warnSignal.setId(uuid.toString());
            warnSignalMapper.insert(warnSignal);
        }
        result.setData("成功加入一个列表的数据");
        result.setCode(200);
        return result ;
    }
    @Override
    //这一个接口是对外声明的 可以主动调用
    public Result warn(List<WarnSignalDto> warnSignalDtos) throws Exception {
        Result result = new Result<>();
        List<WarnMessageDto> TotalMessageDtos=new ArrayList<>();
        for(WarnSignalDto warnSignalDto:warnSignalDtos) {
            WarnSignal warnSignal = new WarnSignal(warnSignalDto);
//            int p=warnSignalMapper.insert(warnSignal);
//            if(p==1){
//                System.out.println("将signal加入数据库");
//            }else{
//                System.out.println("加入signal失败");
//            }
            //下面对每一个信号进行处理判断
            List<WarnMessageDto> warmMessageDtos=handleWarnSignal(warnSignal);
            //只要返回的结果不是0个 就将结果加入到总结果集合中
            if(warmMessageDtos.size()!=0)
            {
                TotalMessageDtos.addAll(warmMessageDtos);
            }
        }
        //对数据返回的格式进行整理
        List<Map<String,Object>>resultList=new ArrayList<>();
        for(WarnMessageDto warnMessageDto:TotalMessageDtos){
            Map<String,Object>map=new HashMap<>();
            map.put("车架编号",warnMessageDto.getCarId());
            map.put("电池类型",warnMessageDto.getBatteryType());
            if (warnMessageDto.getWarnLevel()==-1){
                map.put("warnName","不报警");
            }else{
                map.put("warnName",warnMessageDto.getWarnName());
                map.put("warnLevel",warnMessageDto.getWarnLevel());
            }
            //将改造后的map结果放入结果集中
            resultList.add(map);
        }
        result.setCode(200);
        result.setData(resultList);
        result.setMessage("ok");
        return result ;
    }
    //下面这个warn1执行定时任务定时查询数据库中的 从数据库中获取数据
    @Override
    @Scheduled(cron = "0 0/5 * * * ?")
//    @Scheduled(cron = "0 * * * * ?")  // 每分钟执行一次
    public Result warn1() throws Exception {
        //定时任务开始执行
        System.out.println("开始执行定时任务");
        Result result = new Result<>();
        //从数据库中获取到当前的警告信息
        LambdaQueryWrapper<WarnSignal>warnSignalLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //查询未处理的信号信息 并放到消息队列中
        warnSignalLambdaQueryWrapper.eq(WarnSignal::getSignalState,"0");
        List<WarnSignal>warnSignals=warnSignalMapper.selectList(warnSignalLambdaQueryWrapper);

        // warnSignalMapper.insert(warnSignal);
        //将所有数据打入消息队列任务就完成了
        if(warnSignals.size()==0){
            System.out.println("当前未处理的数据为0 不需要向消息队列输入数据");
        }
        else{
            System.out.println("当前开始向消息队列进行输入数据");
            springProducer.sendMessage("test05130102",JSON.toJSONString(warnSignals));
            System.out.println(JSON.toJSONString(warnSignals));
            System.out.println("将warnSignal数据进行打印");
        }
        result.setCode(200);
        result.setData("当前五分钟执行一次将数据打入消息队列中");
        result.setMessage("ok");
        return result ;
    }

    public List<WarnMessageDto> handleWarnSignal(WarnSignal warnSignal) {
        //我们首先确定当前的信号是有无规则编号 如果有 则只需要找到指定规则即可 如果没有 则需要遍历所有规则
        List<WarnMessageDto>warnMessageDtoList=new ArrayList<>();
        if(warnSignal.getWarnId()==null){
            //无规则编号时
            //首先查询到本车架对应的电池类型
            LambdaQueryWrapper<Vehicle>vehicleLambdaQueryWrapper=new LambdaQueryWrapper<>();
            vehicleLambdaQueryWrapper.eq(Vehicle::getId,warnSignal.getCarId());
            Vehicle vehicle=vehicleMapper.selectOne(vehicleLambdaQueryWrapper);
            if(vehicle!=null) {
                //获取本车架的电池类型 根据电池类型获取到规则
                String batteryType = vehicle.getBatteryType();
                LambdaQueryWrapper<Rule> ruleLambdaQueryWrapper = new LambdaQueryWrapper<>();
                ruleLambdaQueryWrapper.eq(Rule::getBatteryType, batteryType);
                List<Rule> rules = ruleMapper.selectList(ruleLambdaQueryWrapper);
                if (rules != null && rules.size() != 0) {
                    //对每一个规则进行一次处理 然后将数据加入到list中
                    for (Rule rule : rules) {
                        //将信号中的信号信息 规则的规则编号与规则本身进行传递
                        WarnMessageDto warnMessageDto = handleWarnSignalWithRule(warnSignal.getCwsignal(), rule.getWarnId(), rule);
                        //加入列表中
                        if (warnMessageDto != null) {
                            //将车架编号加入
                            warnMessageDto.setCarId(warnSignal.getCarId());
                            warnMessageDtoList.add(warnMessageDto);
                        }
                    }
                }
            }
        }else{
            //有规则编号时
            //首先查询到本车架对应的电池类型
            LambdaQueryWrapper<Vehicle>vehicleLambdaQueryWrapper=new LambdaQueryWrapper<>();
            vehicleLambdaQueryWrapper.eq(Vehicle::getId,warnSignal.getCarId());
            Vehicle vehicle=vehicleMapper.selectOne(vehicleLambdaQueryWrapper);
            if(vehicle!=null) {
                //获取本车架的电池类型 根据电池类型与规则编号获取到规则
                String batteryType = vehicle.getBatteryType();
                LambdaQueryWrapper<Rule> ruleLambdaQueryWrapper = new LambdaQueryWrapper<>();
                ruleLambdaQueryWrapper.eq(Rule::getBatteryType, batteryType);
                ruleLambdaQueryWrapper.eq(Rule::getWarnId, warnSignal.getWarnId());
                Rule rule = ruleMapper.selectOne(ruleLambdaQueryWrapper);
                if(rule!=null){
                    //将信号中的信号信息 规则的规则编号与规则本身进行传递
                    WarnMessageDto warnMessageDto=handleWarnSignalWithRule(warnSignal.getCwsignal(),rule.getWarnId(),rule);
                    //加入列表中
                    if(warnMessageDto!=null){
                        //将车架编号加入
                        warnMessageDto.setCarId(warnSignal.getCarId());
                        warnMessageDtoList.add(warnMessageDto);
                    }
                }
            }
        }
        return warnMessageDtoList;
    }

    @Override
    public Result getByCarId(String carId) {
        LambdaQueryWrapper<WarnSignal>warnSignalLambdaQueryWrapper=new LambdaQueryWrapper<>();
        warnSignalLambdaQueryWrapper.eq(WarnSignal::getCarId,carId);
        List<WarnSignal>warnSignals=warnSignalMapper.selectList(warnSignalLambdaQueryWrapper);
        Result result=new Result<>();
        result.setCode(200);
        result.setMessage("已成功获取数据");
        result.setData(warnSignals);
        return  result;
    }

    //下面这个方法是根据规则与信号之间的信息进行判定并将警告信息进行生成的接口
    private WarnMessageDto handleWarnSignalWithRule(String signal,int warnId, Rule rule) {
//        System.out.println("当前开始进行规则与信号之间生成警告信息");
        //首先吧signal信息进行获取后通过json对象进行拆分
        JSONObject jsonObject= JSON.parseObject(signal);
        //将rule中的规则进行拆分获取操作数
        String[] operates=rule.getWarnRule().split("\\|");
//        for(String s:operates){
//            System.out.println("当前拆分出来的操作数为"+s);
//        }

        WarnMessageDto warnMessageDto=null;
        //根据warnId判定是选择电流还是电压
        if(warnId==1){
            //计算最大电压与最小电压的差值 //需要判断一下合法性
            if(isValieMxWithMi(jsonObject)){
                Double Mx= Double.valueOf(jsonObject.getString("Mx"));
                Double Mi= Double.valueOf(jsonObject.getString("Mi"));
                Double offset=Mx-Mi;
                //声明一个警告信息对象
                warnMessageDto=new WarnMessageDto();
                //第一种情况
                //先将警告名与电池类型确定
                warnMessageDto.setWarnName(rule.getName());
                warnMessageDto.setBatteryType(rule.getBatteryType());
                warnMessageDto.setWarnLevel(getWarnLevel(offset,operates));
//                System.out.println("当前计算的结果位："+warnMessageDto);
            }

        }
        else if(warnId==2){
            //检验是否存在合法的Ix与Ii
            if(isValieIxWithIi(jsonObject)) {
                //计算最大电流与最小电流的差值
                Double Ix= Double.valueOf(jsonObject.getString("Ix"));
                Double Ii= Double.valueOf(jsonObject.getString("Ii"));
                //假设这里出错直接不执行了
                Double offset = Ix - Ii;
                //声明一个警告信息对象
                warnMessageDto = new WarnMessageDto();
                //第二种情况
                //先将警告名与电池类型确定
                warnMessageDto.setWarnName(rule.getName());
                warnMessageDto.setBatteryType(rule.getBatteryType());
                warnMessageDto.setWarnLevel(getWarnLevel(offset, operates));
            }
        }else{
            //如果不是上面两个情况 直接返回null 再上一级进行判断
            return null;
        }
        //最后将数据进行返回
        return warnMessageDto;
    }
    private boolean isValieMxWithMi(JSONObject jsonObject) {
       return jsonObject.containsKey("Mx")&&jsonObject.getString("Mx")!=null&&jsonObject.containsKey("Mi")&&jsonObject.getString("Mi")!=null;
    }
    private boolean isValieIxWithIi(JSONObject jsonObject) {
        return jsonObject.containsKey("Ix")&&jsonObject.getString("Ix")!=null&&jsonObject.containsKey("Ii")&&jsonObject.getString("Ii")!=null;
    }

    //通过一个通用的接口确定等级
    private int getWarnLevel(Double offset, String[] operates) {
//        System.out.println("当前进入获取等级接口");
//        System.out.println("当前的差值为："+offset);
        //根据操作数的比对确定报警信息
        Double max= Double.valueOf(operates[0]);
        Double min= Double.valueOf(operates[operates.length-1]);
        if(offset>=max){
//            System.out.println("当前进入max比较max值为"+max);
            //设置0等级
           return 0;
        }else if(offset<min){
//            System.out.println("当前进入min比较min值为"+min);
            //设置不报警
          return -1;
        }else{
            //遍历数组即可
            for(int i=0;i<operates.length-1;i++)
            {
                //获取左操作数与右操作数
                Double left= Double.valueOf(operates[i]);
                Double right= Double.valueOf(operates[i+1]);
//                System.out.println("当前进入left比较left值为"+left);
//                System.out.println("当前进入right比较right值为"+right);
                //对offset进行处理后设置等级
                if(offset>=right&&offset<left){
//                    System.out.println("当前进入比较并返回差值");
                    //根据符合的类型设置等级
                    //比如他在第一个区间就是第一个等级 第二个区间就是第二个等级
                    return i+1;
                }
            }
        }
        return -1;
    }

    @Override
    public Result get(String id) {
        Result result=new Result<>();
        String cacheKey = "warnSignal:" + id;
        WarnSignal warnSignal= (WarnSignal) redisCache.get(cacheKey);
        if (warnSignal != null) {
            result.setMessage("当前从redis获取数据");
            result.setData(warnSignal);
            return result;
        }
        // 缓存未命中，查数据库
        warnSignal = warnSignalMapper.selectById(id);
        if (warnSignal != null) {
            redisCache.set(cacheKey, warnSignal, 5); // 缓存5分钟
            System.out.println("当前的WarnSignal为"+warnSignal);
        }
        if(warnSignal!=null){
            result.setMessage("当前从数据库获取数据并调整到redis中");
            result.setData(warnSignal);
        }
        else{
            result.setMessage("当前数据库也没有数据 查询不到该数据");
            result.setData(warnSignal);
        }
        result.setCode(200);
        return result;
    }
    @Override
    public Result updateWarnSignal(WarnSignal warnSignal) {
        //现在尝试老师的redis读取方法
        String lockKey = "WarnSignal:Lock_2:" + warnSignal.getId();
        RLock warnSignalLock = redissonClient.getLock(lockKey);
        Result result=new Result();
        try {
            if (!warnSignalLock.tryLock()) {
                System.out.println("未获取到锁" + warnSignal.getId());
                result.setMessage("未获取到锁" + warnSignal.getId());

            }
            warnSignalLock.lock(5, TimeUnit.MINUTES);
            result.setData( warnSignalMapper.updateById(warnSignal));
            redisCache.delete("warnSignal:" + warnSignal.getId()); // 删除旧缓存
            result.setMessage("成功将数据库中的数据进行更改并进行redis的删除同步");
            result.setCode(200);
            Thread.sleep(2000);
        } catch (Exception e) {
            //log.error
            e.printStackTrace();
        } finally {
            //这里是redission的机制 会要求必须检查是否是当前线程持有锁 所以必修添加下面一个委外条件进行判断
            if (warnSignalLock.isLocked() && warnSignalLock.isHeldByCurrentThread()) {
                warnSignalLock.forceUnlock();
            }
        }
        return result;
    }
    @Override
    public Result delete(String id) {
        //尝试老师上课教的方法
        Result result=new Result<>();
        int p =warnSignalMapper.deleteById(id);
        redisCache.delete("warnSignal:" + id); // 删除缓存
        if(p==1){
            result.setMessage("已成功将数据从redis与数据库中移除");
        }
        else{
            result.setMessage("该数据不存在");
        }
        result.setCode(200);
        return result;
    }

    @Override
    public Result getAll() {
        QueryWrapper<WarnSignal> queryWrapper=new QueryWrapper<>();
        List<WarnSignal> WarnSignalList=warnSignalMapper.selectList(queryWrapper);
        Result result=new Result<>();
        result.setData(WarnSignalList);
        result.setMessage("已成功获取当前所有预警信号数据");
        return result;
    }


}
