package com.example.bookshop.accountservice.exception;

import com.example.bookshop.accountservice.dto.AccountDto;
import com.example.bookshop.accountservice.dto.ErrorResponseDto;
import com.example.bookshop.accountservice.dto.ResponseDto;
import com.example.bookshop.accountservice.mapper.CommonMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleAccountAlreadyExistsException(AccountAlreadyExistsException exception, WebRequest request){
        return new ResponseEntity<>(CommonMapper.buildErrorResponse(exception, request, null, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleAccountNotFoundException(AccountNotFoundException exception, WebRequest request){
        return new ResponseEntity<>(CommonMapper.buildErrorResponse(exception, request, null, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(objectError -> {
            String fieldName = ((FieldError) objectError).getField();
            String message = objectError.getDefaultMessage();
            errors.put(fieldName, message);
        });
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .apiPath(request.getDescription(false))
                .message("Thông tin nhập không hợp lệ")
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.BAD_REQUEST)
                .errors(errors)
                .build();
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<AccountDto>> handleGlobalException(Exception exception, WebRequest request){
        ResponseDto<AccountDto> responseDto = ResponseDto.<AccountDto>builder()
                .apiPath(request.getDescription(false))
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .payload(null)
                .build();
        exception.printStackTrace();
        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
