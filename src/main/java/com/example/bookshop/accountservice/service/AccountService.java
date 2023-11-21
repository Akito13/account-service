package com.example.bookshop.accountservice.service;

import com.example.bookshop.accountservice.dto.AccountDto;
import com.example.bookshop.accountservice.model.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
    AccountDto createAccount(AccountDto accountDto);
    Account getAccountByEmail(String email);
    AccountDto updateAccount(Long userId, AccountDto accountDto, Authentication auth);
}
