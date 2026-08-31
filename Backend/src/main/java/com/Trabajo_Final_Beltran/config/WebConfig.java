package com.Trabajo_Final_Beltran.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${storage.local.path:uploads}")
  private String storagePath;

  @Override
  public void addResourceHandlers(
      ResourceHandlerRegistry registry
  ) {

    registry
        .addResourceHandler("/uploads/**")
        .addResourceLocations(
            "file:" + storagePath + "/"
        );
  }
}