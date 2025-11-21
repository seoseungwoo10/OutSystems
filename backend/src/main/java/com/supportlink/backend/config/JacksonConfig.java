package com.supportlink.backend.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    // 현재는 별도 Jackson 모듈을 등록하지 않습니다.
    // Lazy 연관/엔티티 직렬화는 DTO와 @JsonIgnore 등을 통해 제어합니다.
}