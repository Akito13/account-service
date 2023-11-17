package com.example.bookshop.accountservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//        Map<String, Object> responseData = new HashMap<>();
//        responseData.put("message", exception.getMessage());
//        responseData.put("statusCode", HttpStatus.BAD_REQUEST);
//        responseData.put("timestamp", LocalDateTime.now());
//        responseData.put("apiPath", request.getServletPath());
//
//        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//
//        OutputStream outputStream = response.getOutputStream();
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.writerWithDefaultPrettyPrinter().writeValue(outputStream, response);
//        outputStream.flush();
    }
}
