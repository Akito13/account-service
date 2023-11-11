package com.example.bookshop.accountservice.security;

import com.example.bookshop.accountservice.CommonConstants;
import com.example.bookshop.accountservice.dto.LoginAccountDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginAccountDto account = new ObjectMapper().readValue(request.getInputStream(), LoginAccountDto.class);
            System.out.println("attempting to authenticate");
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(account.getEmail(), account.getPassword())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth);
        if(auth != null) {
            byte[] secretKeyBytes = Base64.getEncoder().encode(CommonConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
//            Keys.hmacShaKeyFor(secretKeyBytes)
            SecretKey key = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());
            Instant now = Instant.now();
            String jwt = Jwts.builder().setIssuer("Bookshop").setSubject("JWT Token")
                    .claim("account", auth.getPrincipal())
//                    .claim("authorities", auth.getAuthorities().toArray()[0])
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plusMillis(CommonConstants.JWT_EXP)))
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
            System.out.println("JWT KEY NE: " + jwt);
            response.setHeader(CommonConstants.JWT_HEADER, jwt);
        }
    }
}
