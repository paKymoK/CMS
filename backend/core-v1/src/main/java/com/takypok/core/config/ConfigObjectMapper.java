package com.takypok.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigObjectMapper {
  @Bean
  public ObjectMapper mapper() {
    return objectMapper();
  }

  public static ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    JavaTimeModule timeModule = new JavaTimeModule();
    SimpleModule module = new SimpleModule();
    mapper.registerModule(timeModule);
    mapper.registerModule(module);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }
}
