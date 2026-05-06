package com.wangping.ClaimCenter.dto;

import com.wangping.ClaimCenter.enums.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDto {

    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
