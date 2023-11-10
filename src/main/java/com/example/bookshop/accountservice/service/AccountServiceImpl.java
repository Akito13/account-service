package com.example.bookshop.accountservice.service;

import com.example.bookshop.accountservice.dto.AccountDto;
import com.example.bookshop.accountservice.exception.AccountAlreadyExistsException;
import com.example.bookshop.accountservice.exception.AccountNotFoundException;
import com.example.bookshop.accountservice.mapper.CommonMapper;
import com.example.bookshop.accountservice.model.Account;
import com.example.bookshop.accountservice.model.Role;
import com.example.bookshop.accountservice.repository.AccountRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepo;
    private final PasswordEncoder pwdEncoder;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepo, PasswordEncoder pwdEncoder) {
        this.accountRepo = accountRepo;
        this.pwdEncoder = pwdEncoder;
    }

    public AccountDto createAccount(AccountDto accountDto) {
        Optional<Account> optionalAccount = accountRepo.findByEmail(accountDto.getEmail());
        if(optionalAccount.isPresent() && !optionalAccount.get().getDeleted()){
            throw new AccountAlreadyExistsException("Tài khoản đã tồn tại với email " + accountDto.getEmail());
        }
//        accountDto.setPassword(pwdEncoder.encode(accountDto.getPassword()));
        Account account = CommonMapper.mapToAccount(accountDto);
        account.setPassword(pwdEncoder.encode(accountDto.getPassword()));
        Role role = new Role();
        role.setRoleId("ROLE_USER");
        account.setRoleId(role);
        account.setTrangThai(false);
        account.setDeleted(false);
        return CommonMapper.mapToAccountDto(account);
//        return CommonMapper.mapToAccountDto(accountRepo.save(account));
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepo.findByEmail(email);
        if(optionalAccount.isEmpty() || optionalAccount.get().getDeleted()){
            throw new AccountNotFoundException("Tài khoản không tồn tại");
        }
        Account foundAccount = optionalAccount.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(foundAccount.getRoleId().getRoleId()));
        return new User(email, foundAccount.getPassword(), authorities);
    }
}
