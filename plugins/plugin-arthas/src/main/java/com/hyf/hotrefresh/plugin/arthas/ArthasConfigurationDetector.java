package com.hyf.hotrefresh.plugin.arthas;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/06/25
 */
public class ArthasConfigurationDetector implements SpringApplicationRunListener {

    public ArthasConfigurationDetector(SpringApplication springApplication, String[] args) {

    }

    // ============================================spring6x-====================================================

    // @Override
    public void started() {

    }

    // @Override
    public void environmentPrepared(ConfigurableEnvironment configurableEnvironment) {
        if (ArthasUtils.existArthasConfiguration()) {
            Map<String, Object> properties = new HashMap<>();
            properties.put("spring.arthas.enabled", true);
            MapPropertySource mapPropertySource = new MapPropertySource("hot-refresh-adapter-arthas", properties);
            configurableEnvironment.getPropertySources().addFirst(mapPropertySource);
        }
    }

    // @Override
    // public void contextPrepared(ConfigurableApplicationContext configurableApplicationContext) {
    //
    // }
    //
    // @Override
    // public void contextLoaded(ConfigurableApplicationContext configurableApplicationContext) {
    //
    // }

    // @Override
    public void finished(ConfigurableApplicationContext configurableApplicationContext, Throwable throwable) {

    }

    // ============================================spring6x+====================================================

    public void starting(ConfigurableBootstrapContext bootstrapContext) {
    }

    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
    }

    public void contextPrepared(ConfigurableApplicationContext context) {
    }

    public void contextLoaded(ConfigurableApplicationContext context) {
    }

    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
    }

    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
    }

    public void failed(ConfigurableApplicationContext context, Throwable exception) {
    }




}
