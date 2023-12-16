package com.example.bookshop.accountservice.controller;

import com.example.bookshop.accountservice.dto.AccountDto;
import com.example.bookshop.accountservice.dto.ResponseDto;
import com.example.bookshop.accountservice.dto.ResponsePayload;
import com.example.bookshop.accountservice.event.KafkaProducer;
import com.example.bookshop.accountservice.exception.InvalidBodyException;
import com.example.bookshop.accountservice.model.Account;
import com.example.bookshop.accountservice.service.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        kafkaProducer.notifyAccountRegistration(savedDto.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Boolean> searchAccount(@RequestParam("id") Long accountId,
                                                 Authentication auth) {
        AccountDto accountDto = service.getAndCheckValidAccount(accountId, auth.getName());
        if(accountDto == null)
            return new ResponseEntity<>( false,HttpStatus.OK);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("{userId}")
    public ResponseEntity<ResponseDto<AccountDto>> getAccount(@PathVariable Long userId, WebRequest request,Authentication authentication){
        AccountDto accountDto = service.getAccount(userId, authentication);
        ResponsePayload<AccountDto> payload = ResponsePayload.<AccountDto>builder()
                .currentPage(0).currentPageSize(1).pageSize(1).totalPages(1).recordCounts(1L)
                .records(List.of(accountDto))
                .build();
        ResponseDto<AccountDto> response = ResponseDto.<AccountDto>builder()
                .apiPath(request.getDescription(false))
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.OK)
                .message("OK")
                .payload(payload).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("{userId}")
    public ResponseEntity updateAccount(@PathVariable Long userId, @RequestBody AccountDto accountDto, Authentication auth) {
        service.updateAccount(userId, accountDto, auth);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("email")
    public ResponseEntity isEmailValid(@RequestParam("email") Optional<String> email) {
        if(email.isEmpty()) {
            throw new InvalidBodyException("Hãy cung cấp email");
        }
        service.getAccountByEmail(email.get());
        return ResponseEntity.ok().build();
    }

    @GetMapping("password")
    public ResponseEntity passwordChangeRequest(@RequestParam("email") Optional<String> email,
                                                @RequestParam("password") Optional<String> password) {
        if(email.isEmpty()) {
            throw new InvalidBodyException("Hãy cung cấp email");
        }
        if(password.isEmpty()) {
            throw new InvalidBodyException("Chưa điền mật khẩu");
        }
        String info = email.get() + ":" + password.get();

        kafkaProducer.notifyAccountPasswordChange(info);
        return ResponseEntity.ok().build();
    }
}
