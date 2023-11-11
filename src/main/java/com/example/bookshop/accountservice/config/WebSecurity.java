package com.example.bookshop.accountservice.config;

import com.example.bookshop.accountservice.security.AuthenticationFilter;
import com.example.bookshop.accountservice.security.JWTGeneratorFilter;
import com.example.bookshop.accountservice.service.AccountService;
import com.example.bookshop.accountservice.service.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    public AccountService userService;
    public PasswordEncoder pwdEncoder;

    @Autowired
    public WebSecurity(AccountService userService, PasswordEncoder pwdEncoder) {
        this.userService = userService;
        this.pwdEncoder = pwdEncoder;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(pwdEncoder);


        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager);
        authenticationFilter.setFilterProcessesUrl("/api/account/auth");

        http
//                .cors(configurer -> configurer.configurationSource(r -> {
//                    CorsConfiguration configuration = new CorsConfiguration();
//                    configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
//                    configuration.setAllowedMethods(Collections.singletonList("*"));
//                    configuration.setAllowCredentials(true);
//                    configuration.setAllowedHeaders(Collections.singletonList("*"));
//                    configuration.setMaxAge(3600L);
//                    return configuration;
//                }))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(registry ->
                        registry
                                .requestMatchers("/api/account/register").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/account/auth").permitAll())
                .addFilter(authenticationFilter)
                .authenticationManager(authenticationManager)
//                .addFilterAfter(new JWTGeneratorFilter(), AuthenticationFilter.class)
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
//                .formLogin(AbstractHttpConfigurer::disable)

        return http.build();
    }
}
