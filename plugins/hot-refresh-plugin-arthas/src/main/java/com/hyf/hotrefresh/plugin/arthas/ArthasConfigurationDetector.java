package com.hyf.hotrefresh.plugin.arthas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author baB_hyf
 * @date 2022/06/25
 */
public class ArthasConfigurationDetector implements SpringApplicationRunListener {

    public ArthasConfigurationDetector(SpringApplication springApplication, String[] args) {

    }

    @Override
    public void started() {

    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment configurableEnvironment) {
        if (ArthasUtils.existArthasConfiguration()) {
            Map<String, Object> properties = new HashMap<>();
            properties.put("spring.arthas.enabled", true);
            MapPropertySource mapPropertySource = new MapPropertySource("hot-refresh-adapter-arthas", properties);
            configurableEnvironment.getPropertySources().addFirst(mapPropertySource);
        }
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext configurableApplicationContext) {

    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext configurableApplicationContext) {

    }

    @Override
    public void finished(ConfigurableApplicationContext configurableApplicationContext, Throwable throwable) {

    }
}
