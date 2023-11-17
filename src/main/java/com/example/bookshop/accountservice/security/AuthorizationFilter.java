package com.example.bookshop.accountservice.security;

import com.example.bookshop.JwtAuthority.JwtParser;
import com.example.bookshop.accountservice.CommonConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

public class AuthorizationFilter extends BasicAuthenticationFilter {
    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.out.println("Header null");
            chain.doFilter(request, response);
            return;
        }
        System.out.println("Header not null");
        UsernamePasswordAuthenticationToken user = getAuthentication(authorizationHeader);
        System.out.println(user);
        SecurityContextHolder.getContext().setAuthentication(user);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String authHeader) {
        String jwt = authHeader.replace("Bearer ", "");
        System.out.println("getAuthentication() jwt: " + jwt);
        String secretKey = CommonConstants.JWT_KEY;
        System.out.println(secretKey);
        if(secretKey == null) return null;
        try {
            JwtParser jwtParser = new JwtParser(jwt, secretKey);
            return new UsernamePasswordAuthenticationToken(jwtParser.getEmail(), null, jwtParser.extractAccountRole());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        byte[] secretKeyBytes = Base64.getEncoder().encode(secretKey.getBytes());
//
//        SecretKey signingKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());
//        JwtParser jwtParser = Jwts.parserBuilder()
//                .setSigningKey(signingKey)
//                .build();
//
//        try {
//            Jwt<Header, Claims> parsedToken = jwtParser.parse(jwt);
//            Claims claims = parsedToken.getBody();
//            String authority = claims.get("authority", String.class);
//            String email = claims.get("email", String.class);
//            return new UsernamePasswordAuthenticationToken(email, null, List.<SimpleGrantedAuthority>of(new SimpleGrantedAuthority(authority)));
//        } catch (Exception e) {
//            return null;
//        }
    }

}
