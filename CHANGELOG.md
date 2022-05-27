
## 1.2.0 (May 27, 2022)

- Optimize infrastructure class loader, separate it duties, divide into two class: InfrastructureClassLoader and InfraUtils.
- Optimize log to monitor rpc codec error.
- Optimize trace to analysis class byte code that failed to reTransform.
- Fix dump class byte code failed when hot refresh not been able to work.
- [#3] Fix infrastructure's class has been app class loader to load to lead to ClassNotFoundException.


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



