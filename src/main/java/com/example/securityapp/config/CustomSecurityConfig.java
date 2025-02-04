package com.example.securityapp.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.securityapp.security.filter.JWTCheckFilter;
import com.example.securityapp.security.handler.ApiLoginFailurApiSuccessHandler;
import com.example.securityapp.security.handler.ApiLoginSuccessHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class CustomSecurityConfig {

    /*
     * 1.CSRF 비활성화
     * 2.CORS 설정
     * 3.세션 비활성화
     * 4. 비밀번호 암호화
     */

    // Spring Security의 SecurityFilterChain을 설정하여 Spring Boot 애플리케이션의 보안 구성 설정한다
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("-------------------------------CustomSecurityConfig.filterChain");

        // 1. CORS 설정
        http.cors((httpSecurityConfigure) -> {
            httpSecurityConfigure.configurationSource(corsConfigurationSource());
        });

        // 2. CSRF 비활성화
        http.csrf(config -> config.disable());

        // 3. 세션 비활성화
        http.sessionManagement((sessioinConfig) -> {
            sessioinConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });

        // 폼 기반 로그인 인증
        http.formLogin(config -> {
            // username, password
            config.loginPage("/api/v1/members/login"); // 로그인 요청을 처리할 앤드 포인트
            // config.usernameParameter("email");
            // config.passwordParameter("pwd");
            config.successHandler(new ApiLoginSuccessHandler());
            config.failureHandler(new ApiLoginFailurApiSuccessHandler());

        });

        http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173"));

        configuration.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "HEAD", "DLETE", "OPTIONS"));

        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

        // 클라이언트가 인증 정보(예: 쿠키, Authorization 헤더, TLS 클라이언트 인증)를 요청 헤더에 포함할 수 있도록 허용한다.
        configuration.setAllowCredentials(true);

        // 모든 경로에 대해 CORS 설정을 적욯한다.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
