package com.wangping.ClaimCenter.security;

import com.wangping.ClaimCenter.entity.User;
import com.wangping.ClaimCenter.enums.Role;
import com.wangping.ClaimCenter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClaimCenterUsernamePwdAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        System.out.println(">>> CustomProvider.authenticate() called");
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();

        System.out.println(">>> Attempting login for: " + username);

        User user = userRepository.findByEmail(username).orElseThrow(
                () -> {
                    System.out.println(">>> USER NOT FOUND: " + username);
                    return new UsernameNotFoundException("User details not found for the user: " + username);
                }
        );
        System.out.println(">>> Found user, stored hash: " + user.getPasswordHash());
        boolean matches = passwordEncoder.matches(pwd, user.getPasswordHash());
        System.out.println(">>> Password matches: " + matches);
        Role role = user.getRole();
        System.out.println(">>> Role: " + role);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        System.out.println(">>> Authorities: " + authorities);
        if (passwordEncoder.matches(pwd, user.getPasswordHash())) {
            return new UsernamePasswordAuthenticationToken(user,null, authorities);
        } else {
            throw new BadCredentialsException("Invalid password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
