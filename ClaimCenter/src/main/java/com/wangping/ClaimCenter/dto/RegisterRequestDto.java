package com.wangping.ClaimCenter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 20, message = "The length of the first name should be between 2 and 20 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 20, message = "The length of the last name should be between 2 and 20 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email address must be valid")
    private String email;

    @NotBlank(message =  "Password is required")
    @Size(min = 4, max = 20, message = "Password length must be between 4 and 20 characters")
    private String password;
}
