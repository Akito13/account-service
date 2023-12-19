package com.example.bookshop.accountservice.service;

import com.example.bookshop.accountservice.dto.AccountDto;
import com.example.bookshop.accountservice.exception.AccountAlreadyExistsException;
import com.example.bookshop.accountservice.exception.AccountNotFoundException;
import com.example.bookshop.accountservice.exception.InvalidBodyException;
import com.example.bookshop.accountservice.mapper.CommonMapper;
import com.example.bookshop.accountservice.model.Account;
import com.example.bookshop.accountservice.model.Role;
import com.example.bookshop.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class
AccountServiceImpl implements AccountService{

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
        Account account = CommonMapper.mapToAccount(accountDto);
        account.setPassword(pwdEncoder.encode(accountDto.getPassword()));
        Role role = new Role();
        role.setRoleId("ROLE_USER");
        account.setRoleId(role);
        account.setTrangThai(false);
        account.setDeleted(false);
//        return CommonMapper.mapToAccountDto(account);
        return CommonMapper.mapToAccountDto(accountRepo.save(account));
    }

    public AccountDto getAndCheckValidAccount(Long accountId, String email) {
        Optional<Account> result = accountRepo.findById(accountId);
        if(result.isEmpty()) {
            return null;
        }
        Account account = result.get();
        if(account.getDeleted() ||
            !account.getEmail().equals(email) ||
            !account.getTrangThai() ||
            account.getRoleId().getRoleId().equals("ROLE_ADMIN")) {
            return null;
        }
        return CommonMapper.mapToAccountDto(account);
    }

    public AccountDto getAccount(Long accountId, Authentication auth) {
        Optional<Account> result = accountRepo.findById(accountId);
        if(result.isEmpty()) {
            throw new AccountNotFoundException("Tài khoản không tồn tại");
        }
        Account account = result.get();
        if(account.getDeleted() || !account.getEmail().equals(auth.getName())) {
            throw new AccountNotFoundException("Tài khoản không tồn tại");
        }
        return CommonMapper.mapToAccountDto(account);
    }

    @Override
    public Account getAccountByEmail(String email) {
        Optional<Account> result = accountRepo.findByEmail(email);
        if(result.isEmpty()){
            throw new AccountNotFoundException("Tài khoản không tồn tại");
        }
        return result.get();
    }

    @Override
    public AccountDto updateAccount(Long userId, AccountDto accountDto, Authentication auth) {
        Optional<Account> result = accountRepo.findById(userId);
        if(result.isEmpty()) {
            throw new AccountNotFoundException("Tài khoản không tồn tại");
        }
        Account account = result.get();
        if(account.getDeleted() || !account.getEmail().equals(auth.getName())) {
            throw new AccountNotFoundException("Tài khoản không khớp");
        }
        if(!account.getRoleId().getRoleId().equals(accountDto.getRole()) || !Objects.equals(account.getAccountId(), accountDto.getAccountId())) {
            throw new InvalidBodyException("Thông tin không hợp lệ");
        }
        String encodedPwd = account.getPassword();
        boolean trangThai = account.getTrangThai();
        boolean deleted = account.getDeleted();
        Role role = account.getRoleId();
        account = CommonMapper.mapToAccount(accountDto);
        account.setPassword(encodedPwd);
        account.setDeleted(deleted);
        account.setTrangThai(trangThai);
        account.setRoleId(role);
//        Account savedAcc = accountRepo.save(account);
//        System.out.println(savedAcc.getHoLot() + " " + savedAcc.getTen());
        return CommonMapper.mapToAccountDto(accountRepo.save(account));
    }

    private boolean hasAccountInfoChanged(Account account, AccountDto inputData) {
        ArrayList<Boolean> hasChanged = new ArrayList<>();
        if(account.getTen() == null) {
            if(inputData.getTen() == null || inputData.getTen().isEmpty()) hasChanged.add(false);
            else hasChanged.add(true);
        } else {
            if(account.getTen().equals(inputData.getTen())) hasChanged.add(false);
            else hasChanged.add(true);
        }
        if(account.getHoLot() == null) {
            if(inputData.getHoLot() == null || inputData.getHoLot().isEmpty()) hasChanged.add(false);
            else hasChanged.add(true);
        } else {
            if(account.getHoLot().equals(inputData.getHoLot())) hasChanged.add(false);
            else hasChanged.add(true);
        }
        if(account.getDiaChi() == null) {
            if(inputData.getDiaChi() == null || inputData.getDiaChi().isEmpty()) hasChanged.add(false);
            else hasChanged.add(true);
        } else {
            if(account.getDiaChi().equals(inputData.getDiaChi())) hasChanged.add(false);
            else hasChanged.add(true);
        }
        if(account.getSdt() == null) {
            if(inputData.getSdt() == null || inputData.getSdt().isEmpty()) hasChanged.add(false);
            else hasChanged.add(true);
        } else {
            if (account.getSdt().equals(inputData.getSdt())) hasChanged.add(false);
            else hasChanged.add(true);
        }
        hasChanged.forEach(System.out::println);
        return hasChanged.stream().anyMatch(b -> b);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepo.findByEmail(email);
        if(optionalAccount.isEmpty() || optionalAccount.get().getDeleted()){
            throw new UsernameNotFoundException(email);
        }
        Account foundAccount = optionalAccount.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(foundAccount.getRoleId().getRoleId()));
        return new User(email, foundAccount.getPassword(), authorities);
    }
}
