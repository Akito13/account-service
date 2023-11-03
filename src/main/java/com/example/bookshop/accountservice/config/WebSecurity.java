package com.example.bookshop.accountservice.config;

import com.example.bookshop.accountservice.security.AuthenticationFilter;
import com.example.bookshop.accountservice.security.JWTGeneratorFilter;
import com.example.bookshop.accountservice.service.AccountService;
import com.example.bookshop.accountservice.service.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

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

        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(registry ->
                        registry
                                .requestMatchers("/api/account/register").permitAll())
                .addFilterAfter(new AuthenticationFilter(authenticationManager), BasicAuthenticationFilter.class)
                .addFilterAfter(new JWTGeneratorFilter(), AuthenticationFilter.class)
                .authenticationManager(authenticationManager);

        return http.build();
    }
}
