package com.wangping.ClaimCenter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AnthropicResponse(List<ContentBlock> content) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ContentBlock(String type, String text) {}

    public String firstText() {
        return content.stream()
                .filter(b -> "text".equals(b.type()))
                .map(ContentBlock::text)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No text block in Anthropic response"));
    }
}
