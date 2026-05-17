package com.ogc_prototype.ogc.config;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
            SpringApplication application) {
        File envFile = new File(".env");
        if (!envFile.exists()) {
            return;
        }

        Properties properties = new Properties();
        try (FileInputStream stream = new FileInputStream(envFile)) {
            properties.load(stream);
        } catch (IOException e) {
            return;
        }

        Map<String, Object> map = new HashMap<>();
        properties.forEach((key, value) -> map.put(key.toString().trim(), value.toString().trim()));

        environment.getPropertySources().addFirst(new MapPropertySource("dotenv", map));
    }
}
