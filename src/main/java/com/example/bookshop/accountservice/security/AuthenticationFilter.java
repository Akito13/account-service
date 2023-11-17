package com.example.bookshop.accountservice.security;

import com.example.bookshop.accountservice.CommonConstants;
import com.example.bookshop.accountservice.dto.AccountDto;
import com.example.bookshop.accountservice.dto.LoginAccountDto;
import com.example.bookshop.accountservice.dto.ResponseDto;
import com.example.bookshop.accountservice.dto.ResponsePayload;
import com.example.bookshop.accountservice.mapper.CommonMapper;
import com.example.bookshop.accountservice.model.Account;
import com.example.bookshop.accountservice.service.AccountService;
import com.example.bookshop.accountservice.service.AccountServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalUnit;
import java.util.*;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AccountService accountService;

    private ObjectMapper mapper;

    public AuthenticationFilter(AccountService accountService, ObjectMapper mapper, AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.accountService = accountService;
        this.mapper = mapper;
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
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        ResponseDto<AccountDto> responseData = createResponseData(null, request,HttpStatus.BAD_REQUEST, "Email hoặc mật khẩu không đúng");
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseData);
        System.out.println(json);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
        User user = (User) auth.getPrincipal();
        AccountDto account = CommonMapper.mapToAccountDto(accountService.getAccountByEmail(user.getUsername()));

        byte[] secretKeyBytes = Base64.getEncoder().encode(CommonConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
//            Keys.hmacShaKeyFor(secretKeyBytes)
        SecretKey key = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());

        Instant now = Instant.now();
        Date expiration = Date.from(now.plus(Period.ofDays(CommonConstants.JWT_EXP)));

        String jwt = Jwts.builder().setIssuer("Bookshop").setSubject("JWT Token")
                .claim("id", account.getAccountId())
                .claim("email", account.getEmail())
                .claim("authority", user.getAuthorities().stream().toList().get(0).getAuthority())
                .setIssuedAt(Date.from(now))
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        System.out.println("JWT KEY NE: " + jwt);
        ResponseDto<AccountDto> responseDto = createResponseData(List.<AccountDto>of(account), request, HttpStatus.OK, "Đăng nhập thành công");
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseDto);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        response.setHeader(CommonConstants.JWT_HEADER, jwt);
//        response.setHeader("AccountId", String.valueOf(account.getAccountId()));
//        response.setHeader("Authority", account.getRole());
//        response.setHeader("expiration", String.valueOf(expiration.getTime()));
        out.print(json);
        out.flush();
    }

    private ResponseDto<AccountDto> createResponseData(List<AccountDto> accountDtos, HttpServletRequest request, HttpStatus status, String message) {
        ResponsePayload<AccountDto> payload = accountDtos == null
                ? null
                : ResponsePayload.<AccountDto>builder()
                    .currentPageSize(1).pageSize(1).totalPages(1).currentPage(0).recordCounts(1L)
                    .records(accountDtos)
                    .build();
        return ResponseDto.<AccountDto>builder()
                .apiPath(request.getServletPath())
                .timestamp(LocalDateTime.now())
                .statusCode(status)
                .message(message)
                .payload(payload)
                .build();
    }
}
