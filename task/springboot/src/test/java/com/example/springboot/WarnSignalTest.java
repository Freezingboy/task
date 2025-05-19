package com.example.springboot;

import com.example.springboot.entity.WarnSignal;
import com.example.springboot.entity.dto.WarnMessageDto;
import com.example.springboot.entity.dto.WarnSignalDto;
import com.example.springboot.service.WarnMessageService;
import com.example.springboot.service.WarnSignalService;
import com.example.springboot.utils.Result;
import javafx.application.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)       // JUnit4 需添加
@SpringBootTest(classes = SpringbootApplication.class) // 关键修正 // Application 是主类名              // 加载 Spring 上下文
public class WarnSignalTest {

    @Resource
    private WarnSignalService warnSignalService;
    @Resource
    private WarnMessageService warnMessageService;
    @Test
    public void  addtest(){
        WarnSignalDto warnSignalDto=new WarnSignalDto();
        warnSignalDto.setCarId(3);
        warnSignalDto.setWarnId(1);
        warnSignalDto.setSignal("{\"Mx\":12.0,\"Mi\":0.6}");
        Result result=new Result<>();
        result=warnSignalService.add(warnSignalDto);
        System.out.println(result);
    }
    @Test
    public void  gettest(){
        String id="445cc06b13bb4b8a981d78";
        Result result=new Result<>();
        result=warnSignalService.get(id);
        System.out.println(result);
    }
    //测试根据车架id获取对应车架id的数据
    @Test
    public void  getByCarIdtest(){
        String carId="3";
        Result result=new Result<>();
        result=warnSignalService.getByCarId(carId);
        System.out.println(result);
    }
    //测试修改接口
    @Test
    public void  updatetest(){
        WarnSignal warnSignal=new WarnSignal();
        warnSignal.setCwsignal("{\"Mx\":12.0,\"Mi\":0.6}");
        warnSignal.setWarnId(2);
        warnSignal.setCarId(1);
        warnSignal.setId("445cc06b13bb4b8a981d78");
        warnSignal.setCreateTime(LocalDateTime.now());
        Result result=new Result<>();
        result=warnSignalService.updateWarnSignal(warnSignal);
        System.out.println(result);
    }
    //测试删除接口
    @Test
    public void  deletetest(){
        String id="445cc06b13bb4b8a981d78";
        Result result=new Result<>();
        result=warnSignalService.delete(id);
        System.out.println(result);
    }
    //测试删除接口
    @Test
    public void  getMessageByCidtest(){
        String id="3";
        Result result=new Result<>();
        result=warnMessageService.getMessageByCarId(id);
        System.out.println(result);
    }
    @Test
    public void  warntest() throws Exception {
        WarnSignalDto warnSignalDto1=new WarnSignalDto();
        warnSignalDto1.setSignal("{\"Mx\":12.0,\"Mi\":0.6}");
        warnSignalDto1.setWarnId(1);
        warnSignalDto1.setCarId(1);
        WarnSignalDto warnSignalDto2=new WarnSignalDto();
        warnSignalDto2.setSignal("{\"Ix\":12.0,\"Ii\":11.7}");
        warnSignalDto2.setWarnId(2);
        warnSignalDto2.setCarId(2);
        WarnSignalDto warnSignalDto3=new WarnSignalDto();
        warnSignalDto3.setSignal("{\"Mx\":11.0,\"Mi\":9.6,\"Ix\":12.0,\"Ii\":11.7}");
        warnSignalDto3.setCarId(3);
        List<WarnSignalDto>warnSignalDtos=new ArrayList<>();
        warnSignalDtos.add(warnSignalDto1);
        warnSignalDtos.add(warnSignalDto2);
        warnSignalDtos.add(warnSignalDto3);
        Result result=new Result<>();
        result=warnSignalService.warn(warnSignalDtos);
        System.out.println(result);
    }
}
