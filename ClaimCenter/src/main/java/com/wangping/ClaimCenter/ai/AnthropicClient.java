package com.wangping.ClaimCenter.ai;

import com.wangping.ClaimCenter.config.AnthropicProperties;
import com.wangping.ClaimCenter.dto.AnthropicMessage;
import com.wangping.ClaimCenter.dto.AnthropicRequest;
import com.wangping.ClaimCenter.dto.AnthropicResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class AnthropicClient {

    private final RestClient restClient;
    private final AnthropicProperties properties;

    public AnthropicClient(AnthropicProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("x-api-key", properties.getApiKey())
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("content-type", "application/json")
                .build();
    }

    public String complete(String prompt) {
        AnthropicRequest request = new AnthropicRequest(
                properties.getModel(),
                properties.getMaxTokens(),
                List.of(new AnthropicMessage("user", prompt))
        );

        AnthropicResponse response = restClient.post()
                .uri("/v1/messages")
                .body(request)
                .retrieve()
                .body(AnthropicResponse.class);

        if (response == null) {
            throw new IllegalStateException("Anthropic API returned an empty response");
        }
        return response.firstText();
    }

}
