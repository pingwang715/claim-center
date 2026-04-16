package com.wangping.ClaimCenter.dto;

public record LoginResponseDto(String message, UserDto user, String jwtToken) {
}
