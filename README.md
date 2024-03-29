# 介绍

IDEA小锤子的远程版

本地修改代码后，远程服务器自动热刷新，效果实时生效

欢迎PR :)

# 使用

1、需要热刷新的应用系统引入依赖：

```xml
<dependency>
    <groupId>io.github.hyfsy</groupId>
    <artifactId>hot-refresh-server-all</artifactId>
    <version>1.2.5</version>
</dependency>
```

2、启动应用系统

3、获取本地服务器部署包：[hot-refresh-client.zip](https://github.com/hyfsy/hot-refresh/releases)

4、解压后，启动本地服务器：

```shell
bin/hot.cmd -s http://localhost:8080 -h ...
```

- `-s`：需要热刷新的应用系统地址，到servlet路径，如：http:\/\/localhost:8080/ctx-path/rest/
- `-h`：本地编写代码的工作目录（默认当前命令行目录）
- `-d`：启用客户端调试模式

5、修改`-h`指定的工作目录下的java文件可看到应用系统热刷新


# 注意事项

1. 服务端暂时只支持SpringBoot环境，可提交PR添加其他环境的支持
2. 服务端只支持JDK环境，JRE环境不支持；仅支持JDK8，不支持其他版本的JDK
3. 客户端仅支持Windows系统
4. 热刷新时会发送HTTP请求，如果请求被拦截，请自行在应用系统内放行，或为客户端添加权限支持
4. 热刷新功能基于JVMTI，所以不能添加类字段、方法、修改类的方法签名等信息，推荐只修改方法内代码
5. 无法热刷新混淆的字节码，提供扩展可自行添加混淆功能
6. 服务端不能存在运行时会修改原有字节码的框架，如Skywalking等


# 未来规划

- [ ] 服务端提供IDEA插件
- [ ] jar包热刷新支持
- [x] 集成Lombok
- [x] 集成MapStruct
- [ ] 集成JDK Proxy/CGLib
- [x] 集成Spring
- [ ] 集成MyBatis
- [x] 兼容SkyWalking（agent需添加`-Dskywalking.agent.is_cache_enhanced_class=true`参数）
- [ ] 集成Arthas
- [ ] 支持JDK17
- [ ] 客户端支持Linux/Mac系统

