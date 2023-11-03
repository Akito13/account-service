package com.example.bookshop.accountservice.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ResponseDto<T> {
    private HttpStatus statusCode;
    private String message;
    private LocalDateTime timestamp;
    private String apiPath;
    private ResponsePayload<T> payload;
}
