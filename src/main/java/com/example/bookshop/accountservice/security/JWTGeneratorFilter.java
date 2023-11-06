package com.example.bookshop.accountservice.security;

import com.example.bookshop.accountservice.CommonConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

public class JWTGeneratorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null) {
            byte[] secretKeyBytes = Base64.getEncoder().encode(CommonConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
//            Keys.hmacShaKeyFor(secretKeyBytes)
            SecretKey key = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());
            Instant now = Instant.now();
            String jwt = Jwts.builder().setIssuer("Bookshop").setSubject("JWT Token")
                    .claim("email", auth.getPrincipal())
                    .claim("authorities", auth.getAuthorities().toArray()[0])
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plusMillis(CommonConstants.JWT_EXP)))
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
            response.setHeader(CommonConstants.JWT_HEADER, jwt);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/login");
    }
}
