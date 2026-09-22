package org.hooni.api.config;

import lombok.RequiredArgsConstructor;
import org.hooni.api.common.code.StatusCode;
import org.hooni.api.common.response.ApiSecurityResponseWriter;
import org.hooni.api.security.CustomAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationFilter authenticationFilter;
    private final ApiSecurityResponseWriter securityResponseWriter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) ->
                                securityResponseWriter.write(
                                        response,
                                        HttpStatus.UNAUTHORIZED,
                                        StatusCode.UNAUTHORIZED
                                ))
                        .accessDeniedHandler((request, response, exception) ->
                                securityResponseWriter.write(
                                        response,
                                        HttpStatus.FORBIDDEN,
                                        StatusCode.FORBIDDEN
                                )))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/api/v1/health",
                                "/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/"
                        ).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
