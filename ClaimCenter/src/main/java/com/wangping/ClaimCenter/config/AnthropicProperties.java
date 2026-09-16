package com.wangping.ClaimCenter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "anthropic")
public class AnthropicProperties {

    private String apiKey;
    private String model = "claude-sonnet-4-6";
    private String baseUrl = "https://api.anthropic.com";
    private int maxTokens = 1024;


}
