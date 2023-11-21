package com.example.bookshop.accountservice.config;

import com.example.bookshop.accountservice.security.AuthenticationFilter;
import com.example.bookshop.accountservice.security.AuthorizationFilter;
import com.example.bookshop.accountservice.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    private AccountService userService;
    private PasswordEncoder pwdEncoder;
    private ObjectMapper mapper;

    @Autowired
    public WebSecurity(AccountService userService, PasswordEncoder pwdEncoder, ObjectMapper mapper) {
        this.userService = userService;
        this.pwdEncoder = pwdEncoder;
        this.mapper = mapper;
    }

//    @Bean
//    RestAuthenticationFailureHandler authenticationFailureHandler() {
//        return new RestAuthenticationFailureHandler();
//    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(pwdEncoder);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, mapper, authenticationManager);
        authenticationFilter.setFilterProcessesUrl("/api/account/auth");

        AuthorizationFilter authorizationFilter = new AuthorizationFilter(authenticationManager);

        http.cors(AbstractHttpConfigurer::disable)
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
                                .requestMatchers("/api/account/change").authenticated()
                                .requestMatchers(HttpMethod.GET,"/api/account", "/api/account/{userId:[\\d+]}").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/account/{userId:[\\d+]}").authenticated()
                                .requestMatchers(HttpMethod.POST, "/api/account/auth").permitAll())
                .addFilter(authorizationFilter)
                .addFilter(authenticationFilter)
//                .addFilterBefore()
                .authenticationManager(authenticationManager)
//                .addFilterAfter(new JWTGeneratorFilter(), AuthenticationFilter.class)
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
//                .formLogin(AbstractHttpConfigurer::disable)

        return http.build();
    }
}
