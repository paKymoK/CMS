package com.takypok.chatservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.chatservice.config.postgres.AttachmentListReader;
import com.takypok.chatservice.config.postgres.AttachmentListWriter;
import com.takypok.core.config.postgres.UserReader;
import com.takypok.core.config.postgres.UserWriter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.PostgresDialect;

@Configuration
@RequiredArgsConstructor
public class PostgresConfig {
  private final ObjectMapper mapper;

  @Bean
  public R2dbcCustomConversions customConversions() {
    List<Converter<?, ?>> converters = new ArrayList<>();
    converters.add(new UserReader(mapper));
    converters.add(new UserWriter(mapper));
    converters.add(new AttachmentListReader(mapper));
    converters.add(new AttachmentListWriter(mapper));
    return R2dbcCustomConversions.of(PostgresDialect.INSTANCE, converters);
  }
}
