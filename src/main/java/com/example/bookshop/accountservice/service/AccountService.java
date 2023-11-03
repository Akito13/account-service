package com.example.bookshop.accountservice.service;

import com.example.bookshop.accountservice.dto.AccountDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
    AccountDto createAccount(AccountDto accountDto);
}
