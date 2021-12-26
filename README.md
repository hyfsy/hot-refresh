# 介绍

IDEA小锤子的远程版

本地修改代码后，远程服务器自动热刷新，效果实时生效

欢迎PR :)

# 使用

> 目前懒得发布maven中央仓库，请自行拉取源代码打包

1、需要热刷新的应用程序引入依赖：

```xml
<dependency>
    <groupId>com.hyf</groupId>
    <artifactId>hot-refresh-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

2、获取本地服务器jar包：`hot-refresh-server-1.0.0-SNAPSHOT`

3、进入命令行界面，启动本地服务器：

```bash
java 
  -jar hot-refresh-server-1.0.0-SNAPSHOT.jar
  -h C:\\Users\\baB_hyf\\Desktop\\test 
  -s http://localhost:8082/rest 
```

- `-h`：本地编写代码的工作目录
- `-s`：需要热刷新的应用程序地址，到servlet路径，如：http://localhost:8080/ctx-path/rest/

5、修改`-h`指定的工作目录下的java文件可看到应用系统热刷新

# 模块介绍

- `hot-refresh-common`：客户端、服务端公用的基础模块
- `hot-refresh-core`：热刷新核心包，应用程序引入
- `hot-refresh-server`：本地服务包，监听本地工作目录
- `hot-refresh-test-springboot`：本地测试包，模拟需要热刷新的应用程序

# FAQ

## 环境限制

1. 暂时只支持SpringBoot环境
2. ~~只支持JDK环境，JRE环境不支持~~（待测试）

## 功能限制

1. 热刷新时会发送HTTP请求，如果该`/hot-refresh`请求被拦截，请自行在应用系统内放行
2. 热刷新功能基于JVMTI，所以不能添加类字段、方法、修改类的方法签名等信息，推荐只修改方法内代码
3. 无法热刷新混淆的字节码

