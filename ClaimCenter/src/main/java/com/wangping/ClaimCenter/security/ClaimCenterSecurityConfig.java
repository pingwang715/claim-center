package com.wangping.ClaimCenter.security;

import com.wangping.ClaimCenter.filter.JWTTokenValidatorFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ClaimCenterSecurityConfig {

    private final List<String> publicPaths;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrfConfig -> csrfConfig.disable())
                .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource())
                )
                .authorizeHttpRequests((requests) -> {
                            requests.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                    .requestMatchers(publicPaths.toArray(new String[publicPaths.size()])).permitAll()
                                    .requestMatchers(HttpMethod.POST, "/api/v1/claims/*/override").hasRole("MANAGER")
                                    .requestMatchers(HttpMethod.POST, "/api/v1/claims/*/assign").hasRole("MANAGER")
                                    .requestMatchers(HttpMethod.GET, "/api/v1/claims").hasRole("MANAGER")

                                    .requestMatchers(HttpMethod.POST, "/api/v1/claims/*/approve").hasRole("ADJUSTER")
                                    .requestMatchers(HttpMethod.POST, "/api/v1/claims/*/reject").hasRole("ADJUSTER")
                                    .requestMatchers(HttpMethod.POST, "/api/v1/claims").hasRole("CLAIMANT")

                                    .requestMatchers(HttpMethod.GET, "/api/v1/claims/*").hasAnyRole("CLAIMANT", "ADJUSTER", "MANAGER")
                                    .anyRequest().authenticated();
                        }
                )
                .addFilterBefore(new JWTTokenValidatorFilter(publicPaths), BasicAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(withDefaults())
                .httpBasic(withDefaults()).build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) throws AuthenticationException {
        var provideManager = new ProviderManager(authenticationProvider);
        return provideManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
