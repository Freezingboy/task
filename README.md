# task
# 小米大作业 

## 关于考核中提到的考核标准中的图片都进行了备注并存放在代码的同级文件夹下

本次作业使用技术栈包括 Java技术栈，SpringBoot 2.0+，Mysql 5.7+，Redis，http/https，MQ

数据库的sql文件在resources的pracitice.sql 注意在application.yml中的数据库配置 修改为你本地的数据库名 用户名以及密码

```yml
datasource:
  driver-class-name: com.mysql.cj.jdbc.Driver
  url: jdbc:mysql://localhost:3306/pracitice?serveTimezone=GMT%2b8
  username: root
  #    password: ontoweb
  password: 123456
```

运行前首先需要对idea进行配置 使用jdk1.8 同时将maven仓库进行配置

运行相关中间件包括redis rockermq其中rockermq要运行两个一个是nameserver 一个mybroker注意命令

运行mybroker命令
cd C:\Users\cizel\workspaces\lesson-5.13\rocketmq-all-5.3.0-bin-release
.\bin\mqbroker -n localhost:9876

运行nameserver命令
cd C:\Users\cizel\workspaces\lesson-5.13\rocketmq-all-5.3.0-bin-release
.\bin\mqnamesrv

然后启动项目出现以下代码表明项目启动成功

----------------------------------------------------------
	Application Jeecg-Boot is running! Access URLs:
	Local: 		http://localhost:9000/
	External: 	http://172.31.144.1:9000/
	Swagger文档: 	http://172.31.144.1:9000/doc.html
----------------------------------------------------------
2025-05-18 17:31:09.839  INFO 12432 --- [3)-172.31.144.1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2025-05-18 17:31:09.840  INFO 12432 --- [3)-172.31.144.1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2025-05-18 17:31:09.842  INFO 12432 --- [3)-172.31.144.1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 2 ms
2025-05-18 17:31:10.213  INFO 12432 --- [5)-172.31.144.1] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2025-05-18 17:31:10.292  INFO 12432 --- [5)-172.31.144.1] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.

整体代码层级结构图如下

![image-20250518173636715](D:\Baby\task\task\images\image-20250518173636715.png)

其中config中包括了redis与redisson的配置启动

![image-20250518173746071](D:\Baby\task\task\images\image-20250518173746071.png)

constant包括了一个静态类用于配置状态码

![image-20250518173820444](D:\Baby\task\task\images\image-20250518173820444.png)

controller层包括了对多个实体的接口封装 包括车架 规则 警告信息 警告信号以及外部api的接口配置 与之相对应的包括entity mapper service 以及service下的impl 具体业务代码都在impl下 entity下包含了各类实体以及生产者和消费者的配置与一些警告信息与警告信号的包装类 包装类放在entity下的dto包下

![image-20250518174108223](D:\Baby\task\task\images\image-20250518174108223.png)

还有一个redis包中放置了rediscache类配置了以下存取redis数据的方法 utils包下则存储了一个包装类用于返回结果

