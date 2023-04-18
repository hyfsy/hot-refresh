package com.hyf.hotrefresh.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 与skywalking不兼容：
 * <p>
 *     agent.is_open_debugging_class = ${SW_AGENT_OPEN_DEBUG:true}
 *     agent.is_cache_enhanced_class = ${SW_AGENT_CACHE_CLASS:false}
 *     agent.class_cache_mode = ${SW_AGENT_CLASS_CACHE_MODE:MEMORY}
 *     agent.service_name=${SW_AGENT_NAME:Your_ApplicationName}
 *     collector.backend_service=${SW_AGENT_COLLECTOR_BACKEND_SERVICES:127.0.0.1:11800}
 *     logging.file_name=${SW_LOGGING_FILE_NAME:skywalking-api.log}
 *     logging.level=${SW_LOGGING_LEVEL:INFO}
 *     logging.dir=${SW_LOGGING_DIR:""}
 *     -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
 * -javaagent:E:\study\env\component\cloud-resources\skywalking\apache-skywalking-apm-8.5.0\agent\skywalking-agent.jar -Dskywalking.agent.service_name=test-application -Dsagent.is_open_debugging_class=true -Dagent.is_cache_enhanced_class=true -Dagent.class_cache_mode=FILE
 */
@SpringBootApplication
public class TestHotRefreshApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestHotRefreshApplication.class, args);
    }

}
