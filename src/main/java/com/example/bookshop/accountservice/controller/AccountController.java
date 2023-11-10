package com.example.bookshop.accountservice.controller;

import com.example.bookshop.accountservice.dto.AccountDto;
import com.example.bookshop.accountservice.dto.ResponseDto;
import com.example.bookshop.accountservice.dto.ResponsePayload;
import com.example.bookshop.accountservice.event.KafkaProducer;
import com.example.bookshop.accountservice.service.AccountServiceImpl;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "api/account")
public class AccountController {

    private final AccountServiceImpl service;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public AccountController(AccountServiceImpl service, KafkaProducer kafkaProducer) {
        this.service = service;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("register")
    public ResponseEntity<ResponseDto<AccountDto>> register(@Validated({AccountDto.RegisterInfo.class}) @RequestBody AccountDto accountDto, WebRequest request){
        AccountDto savedDto = service.createAccount(accountDto);
        ResponsePayload<AccountDto> payload = ResponsePayload.<AccountDto>builder()
                .currentPage(0).currentPageSize(1).pageSize(1).totalPages(1).recordCounts(1L)
                .records(List.of(accountDto))
                .build();
        ResponseDto<AccountDto> response = ResponseDto.<AccountDto>builder()
                .apiPath(request.getDescription(false))
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.CREATED)
                .message("Tạo tài khoản thành công!")
                .payload(payload).build();
//        kafkaProducer.notifyAccountRegistration(savedDto.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}
