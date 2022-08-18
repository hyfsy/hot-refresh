package com.hyf.hotrefresh.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 与skywalking不兼容：
 * <p>
 * -javaagent:E:\study\env\component\cloud-resources\skywalking\apache-skywalking-apm-8.5.0\agent\skywalking-agent.jar -Dskywalking.agent.service_name=test-hot-refresh-application -Dskywalking.plugin.jdbc.trace_sql_parameters=true
 */
@SpringBootApplication
public class TestHotRefreshApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestHotRefreshApplication.class, args);
    }

}
