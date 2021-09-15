package com.opensysnet.paperless;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationProperties
@MapperScan("com.opensysnet.paperless.mapper")
public class PaperlessControllApplication extends SpringBootServletInitializer {

    public static void main(String[] args) { SpringApplication.run(PaperlessControllApplication.class, args); }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PaperlessControllApplication.class);
    }}
