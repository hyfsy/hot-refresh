# 介绍

IDEA小锤子的远程版

本地修改代码后，远程服务器自动热刷新，效果实时生效

欢迎PR :)

# 使用

1、需要热刷新的应用程序引入依赖：

```xml
<dependency>
    <groupId>com.hyf</groupId>
    <artifactId>hot-refresh-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

2、获取本地服务器jar包：`hot-refresh-server-1.0.0-SNAPSHOT`，请自行拉取源代码打包

3、进入命令行界面，启动本地服务器：

```bash
java 
  -Dhome=C:\\Users\\baB_hyf\\Desktop\\test 
  -Durl=http://localhost:8082/rest 
  -jar hot-refresh-server-1.0.0-SNAPSHOT.jar
```

- `home`：修改代码的工作目录
- `url`：需要热刷新的应用程序地址，到接口路径，如：http://localhost:8080/rest/

# 模块介绍

- `hot-refresh-core`：热刷新核心包，应用程序引入
- `hot-refresh-server`：本地服务包，监听工作目录
- `hot-refresh-test-springboot`：本地测试包，模拟需要热刷新的应用程序

# FAQ

## 环境限制

暂时只支持SpringBoot环境

## 刷新范围限制

热刷新功能基于JVMTI，所以不能修改类的方法签名等信息，推荐只修改方法内代码

## 功能限制

热刷新时会发送HTTP请求，如果该请求被拦截，请自行在应用系统里将该请求放行