# 介绍

IDEA小锤子的远程版

本地修改代码后，远程服务器自动热刷新，效果实时生效

欢迎PR :)

# 使用

> 目前懒得发布maven中央仓库，请自行拉取源代码打包

1、需要热刷新的应用系统引入依赖：

```xml
<dependency>
    <groupId>com.hyf.hotrefresh</groupId>
    <artifactId>hot-refresh-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

2、启动应用系统

3、获取本地服务器jar包：`hot-refresh-server-1.0.0-SNAPSHOT`

4、进入命令行界面，启动本地服务器：

```bash
java 
  -jar hot-refresh-server-1.0.0-SNAPSHOT.jar
  -h C:\\Users\\baB_hyf\\Desktop\\test 
  -s http://localhost:8082/rest 
```

- `-h`：本地编写代码的工作目录（默认当前命令行目录）
- `-s`：需要热刷新的应用系统地址，到servlet路径，如：http://localhost:8080/ctx-path/rest/

5、修改`-h`指定的工作目录下的java文件可看到应用系统热刷新

# 模块介绍

- `hot-refresh-common`：客户端、服务端公用的基础模块
- `hot-refresh-core`：热刷新核心包，应用系统引入
- `hot-refresh-server`：本地服务包，监听本地工作目录
- `test-case/hot-refresh-test-springboot`：本地测试包，模拟需要热刷新的应用系统
- `test-case/hot-refresh-test-jar-file`：直接打jar包，方便测试jar包的热刷新功能

# 注意事项

1. 暂时只支持SpringBoot环境，可提交PR添加其他环境的支持
2. 只支持JDK环境，JRE环境不支持
3. 热刷新时会发送HTTP请求，如果该`/hot-refresh`请求被拦截，请自行在应用系统内放行
4. 热刷新功能基于JVMTI，所以不能添加类字段、方法、修改类的方法签名等信息，推荐只修改方法内代码
5. **无法热刷新混淆的字节码**，提供扩展可自行添加混淆功能


# 未来规划

- 服务端提供IDEA插件
- jar包热刷新支持
- 集成Spring
- 集成MyBatis

