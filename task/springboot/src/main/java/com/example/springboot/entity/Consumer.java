package com.example.springboot.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot.entity.dto.WarnMessageDto;
import com.example.springboot.mapper.WarnSignalMapper;
import com.example.springboot.service.WarnMessageService;
import com.example.springboot.service.WarnSignalService;
import com.example.springboot.utils.Result;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Consumer implements CommandLineRunner {

    @Resource
    private WarnSignalService warnSignalService;
    @Resource
    private WarnMessageService warnMessageService;
    @Override
    public void run(String... args) throws Exception {
        startMQConsumer();
    }
    //将构建结果集合单独剥离到外围成一个方法
    private Map<String, Object> buildResultMap(WarnMessageDto dto, String signalId) {
        Map<String, Object> map = new HashMap<>();
        map.put("车架编号", dto.getCarId());
        map.put("电池类型", dto.getBatteryType());
        map.put("warnName", dto.getWarnLevel() == -1 ? "不报警" : dto.getWarnName());
        if(dto.getWarnLevel() != -1) map.put("warnLevel", dto.getWarnLevel());
        return map;
    }
    public void startMQConsumer() throws MQClientException {
        System.out.println("消费者监听信息如下");
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("DemoConsumer");
        consumer.setNamesrvAddr("localhost:9876");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.subscribe("test05130102", "*");
        consumer.setConsumeTimeout(1000L * 60 * 2);
        consumer.setMaxReconsumeTimes(0);

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(
                    List<MessageExt> msgs,
                    ConsumeConcurrentlyContext context) {
                msgs.forEach(messageExt -> {
                    try {
                        String messageBody = new String(
                                messageExt.getBody(),
                                RemotingHelper.DEFAULT_CHARSET
                        );
                        System.out.println("收到消息: " + messageBody);
                        //从消息队列中获取数据后开始处理
                        List<WarnSignal> warnSignals = JSON.parseArray(messageBody, WarnSignal.class);
                        // 流式处理核心逻辑
                        if(warnSignals.size()!=0){
                            List<Map<String, Object>> resultList = warnSignals.stream()
                                    .peek(signal -> signal.setSignalState(1)) // 前置状态设置 当前已经开始处理
                                    .flatMap(signal -> {
                                        // 处理信号生成告警消息DTO
                                        List<WarnMessageDto> dtos = warnSignalService.handleWarnSignal(signal);

                                        // 转换DTO为告警信息并绑定信号ID
                                        List<WarnMessage> messages = dtos.stream()
                                                .map(dto -> {
                                                    WarnMessage entity = new WarnMessage(dto);
                                                    entity.setSignalId(signal.getId()); // 直接使用signal对象
                                                    return entity;
                                                })
                                                .collect(Collectors.toList());

                                        // 批量插入
                                        if(!messages.isEmpty()) {
                                            warnMessageService.insertBatch(messages); // 需要实现批量插入方法
                                        }
                                        // 构建结果映射
                                        return dtos.stream()
                                                .map(dto -> buildResultMap(dto, signal.getId()));
                                    })
                                    .collect(Collectors.toList());

                            // 批量更新信号状态使用并行流
                            warnSignals.parallelStream() // 根据数据量决定是否并行
                                    .forEach(warnSignalService::updateWarnSignal);

                            System.out.println("消费者处理后当前结果为" + resultList);
                        }else{
                            System.out.println("消费者没有拆分到结果，不做处理");
                        }

//-----------------------------------------------------------------------------------------------------------------
//                       下面为原有处理逻辑没有加入streamAPI与lambda表达式
//                        List<WarnSignal> warnSignals = JSON.parseArray(messageBody, WarnSignal.class);
//                        //对数据返回的格式进行整理
//                        List<Map<String,Object>>resultList=new ArrayList<>();
//                        for(WarnSignal warnSignal:warnSignals){
//                            List<WarnMessageDto> warnMessageDtos = warnSignalService.handleWarnSignal(warnSignal);
//                            //开始处理 将警告信息插入数据库同时将信号对应的状态置为1
//                            //然后将警告信息一条一条插入数据库
//                            for (WarnMessageDto warnMessageDto : warnMessageDtos) {
//                                WarnMessage warnMessage=new WarnMessage(warnMessageDto);
//                                //绑定警告信息与信号id之间的关联关系
//                                warnMessage.setSignalId(warnSignal.getId());
//                                Result result=warnMessageService.add(warnMessage);
//                                if(result!=null){
//                                    System.out.println("当前result为"+result);
//                                }else{
//                                    System.out.println("当前插入数据库插入出错");
//                                }
//                                Map<String, Object> map = new HashMap<>();
//                                map.put("车架编号", warnMessageDto.getCarId());
//                                map.put("电池类型", warnMessageDto.getBatteryType());
//                                if (warnMessageDto.getWarnLevel() == -1) {
//                                    map.put("warnName", "不报警");
//                                } else {
//                                    map.put("warnName", warnMessageDto.getWarnName());
//                                    map.put("warnLevel", warnMessageDto.getWarnLevel());
//                                }
//                                //将改造后的map结果放入结果集中
//                                resultList.add(map);
//                            }
//                            //最后记得将这里的每一个signal的状态进行更新
//                            warnSignal.setSignalState(1);
//                            Result result1=warnSignalService.updateWarnSignal(warnSignal);
//                            if(result1!=null){
//                                System.out.println("当前result1为："+result1);
//                            }else{
//                                System.out.println("当前插入数据库插入出错");
//                            }
//                        }
//                        System.out.println("消费者处理后当前结果为" + resultList);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (Exception e) { // 捕获所有异常
                        e.printStackTrace();
                    }
                });
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        System.out.println("Consumer Started");
    }
}