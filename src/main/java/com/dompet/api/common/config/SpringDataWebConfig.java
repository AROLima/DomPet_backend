package com.dompet.api.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class SpringDataWebConfig {
  // Stabiliza a serialização de Page para estrutura previsível (compatível com o front).
}
