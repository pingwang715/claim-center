package com.wangping.ClaimCenter.controller;

import com.wangping.ClaimCenter.dto.LoginRequestDto;
import com.wangping.ClaimCenter.dto.LoginResponseDto;
import com.wangping.ClaimCenter.dto.RegisterRequestDto;
import com.wangping.ClaimCenter.dto.UserDto;
import com.wangping.ClaimCenter.entity.User;
import com.wangping.ClaimCenter.enums.Role;
import com.wangping.ClaimCenter.repository.UserRepository;
import com.wangping.ClaimCenter.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CompromisedPasswordChecker compromisedPasswordChecker;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> apiLogin(@RequestBody LoginRequestDto loginRequestDto){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDto.username(), loginRequestDto.password())
            );
            System.out.println(">>> Auth manager passed ✅");
            var userDto = new UserDto();
            var loggedInUserDto = (User) authentication.getPrincipal();
            userDto.setFirstName(loggedInUserDto.getFirstName());
            userDto.setLastName(loggedInUserDto.getLastName());
            userDto.setEmail(loggedInUserDto.getEmail());
            userDto.setRole(authentication.getAuthorities().iterator().next().getAuthority());
            String jwtToken = jwtUtil.generateJwtToken(authentication);
            System.out.println(">>> Token generated: " + jwtToken);
            return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDto(HttpStatus.OK.getReasonPhrase(), userDto, jwtToken));
        } catch (BadCredentialsException ex) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        } catch (AuthenticationException ex) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication failed");
//        } catch (Exception ex) {
//            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterRequestDto registerRequestDto){

        CompromisedPasswordDecision decision = compromisedPasswordChecker.check((registerRequestDto.getPassword()));
        if(decision.isCompromised()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("password", "Choose a strong password"));
        }
        Optional<User> existingUser = userRepository.findByEmail(registerRequestDto.getEmail());
        if (existingUser.isPresent()) {
            Map<String, String> errors = new HashMap<>();
            User user =  existingUser.get();

            if (user.getEmail().equals(registerRequestDto.getEmail())) {
                errors.put("email", "Email is already registered");
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        User user = new User();
        BeanUtils.copyProperties(registerRequestDto, user);
        user.setPasswordHash(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setRole(Role.CLAIMANT);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Registration Successful");
    }

    private ResponseEntity<LoginResponseDto> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(new LoginResponseDto(message, null, null));
    }
}
