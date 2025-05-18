package com.example.springboot;

import com.example.springboot.entity.Consumer;
import com.example.springboot.entity.Producer;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
@Slf4j
@SpringBootApplication
@MapperScan("com.example.springboot.mapper")
@EnableScheduling  // 关键注解
public class SpringbootApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(SpringbootApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        log.info("\n----------------------------------------------------------\n\t" +
                "Application Jeecg-Boot is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port  + "/\n\t" +
                "External: \thttp://" + ip + ":" + port  + "/\n\t" +
                "Swagger文档: \thttp://" + ip + ":" + port  + "/doc.html\n" +
                "----------------------------------------------------------");
    }

}
