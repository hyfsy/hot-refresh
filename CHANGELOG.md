

## 1.2.1 (Jul 22, 2022)

- Optimize the client dependency to client and api jar for extension.
- Upgrade rpc message version to 2.
- Support args parser to parse custom args.
- Support Hotrefresh manage command.
- Support custom httpclient request builder.
- Support assign infrastructure resource outer.
- Support infra class loader to load the custom file with INFRA.hotrefresh suffix.
- Support SpringMVC interceptor escape for /hot-refresh request path.
- Fix lombok use with mapstruct occur some problem.
- Fix extend class loader getResource... to load own resources.
- Fix find resource use app class instead of infra class at compile time.
- [#6] Fix add output home failed when directory not exists.
- Fix instrumentation obtain from the jvm start process.


## 1.2.0 (May 28, 2022)

- Compatible with some Spring Boot basic features.
- Add new ways to execute scripts.
- Optimize infrastructure class loader, separate it duties, divide into two class: InfrastructureClassLoader and InfraUtils.
- Optimize log to monitor rpc codec error.
- Optimize trace to analysis class byte code that failed to reTransform.
- Fix dump class byte code failed when hot refresh not been able to work.
- [#3] Fixed infrastructure's class has been app class loader to load to lead to ClassNotFoundException.


## 1.1.0 (May 21, 2022)

- Optimize spring boot autoconfiguration for HotRefreshFilter.
- Optimize remoting module function, support outside extension processing.
- Optimize remoting api.
- Support fastjson plugin to transform json format conversion.
- Support add executable test script to debug the environment convenience.
- Support the external plugins of the client side.
- [#2] Fixed
- Fixed many other bugs.



## 1.0.1 (May 17, 2022)

- Nothing was added, just publish 1.0.0 has something error.



## 1.0.0 (May 16, 2022)

- Support hot refresh class on the production environment.
- Compatible with Lombok.
- Compatible with MapStruct.



