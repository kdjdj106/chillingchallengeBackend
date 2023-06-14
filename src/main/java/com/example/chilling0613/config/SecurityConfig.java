package com.example.chilling0613.config;

import com.example.chilling0613.oauth2jwt.jwt.CustomAccessDeniedHandler;
import com.example.chilling0613.oauth2jwt.jwt.CustomAuthenticationEntryPoint;
import com.example.chilling0613.oauth2jwt.jwt.JwtAuthenticationFilter;
import com.example.chilling0613.oauth2jwt.jwt.JwtProvider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;


    private static final String[] URL_TO_PERMIT = {
            "/v3/api-docs/**",
            "/api-docs/**",
            "/api-document/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api/v1/auth/test/test", "/api/v1/auth/login/**",
            "/api/v1/auth/signup/**"
    };
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 접근 권한 설정부
                .and()
                .authorizeRequests()
                .requestMatchers(HttpMethod.OPTIONS).permitAll() // 열어두어야 CORS Preflight 막을 수 있음
                .requestMatchers("/", "/h2/**").permitAll()
                .requestMatchers(URL_TO_PERMIT).permitAll()
                .anyRequest().authenticated()

                // JWT 토큰 예외처리부
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)

                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
