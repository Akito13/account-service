package com.example.bookshop.accountservice.mapper;

import com.example.bookshop.accountservice.dto.AccountDto;
import com.example.bookshop.accountservice.dto.ErrorResponseDto;
import com.example.bookshop.accountservice.model.Account;
import com.example.bookshop.accountservice.model.Role;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;

public class CommonMapper {
    public static AccountDto mapToAccountDto(Account account){
        return AccountDto.builder()
                .accountId(account.getAccountId())
                .email(account.getEmail())
                .sdt(account.getSdt())
                .hoLot(account.getHoLot())
                .ten(account.getTen())
                .role(account.getRoleId().getRoleId())
                .diaChi(account.getDiaChi())
//                .trangThai(account.getTrangThai())
//                .deleted(account.getDeleted())
                .build();
    }

    public static Account mapToAccount(AccountDto accountDto){
        return Account.builder()
                .accountId(accountDto.getAccountId())
                .email(accountDto.getEmail())
                .sdt(accountDto.getSdt())
                .hoLot(accountDto.getHoLot())
                .ten(accountDto.getTen())
                .diaChi(accountDto.getDiaChi())
//                .password(ac)
//                .roleId(role)
//                .trangThai(accountDto.getTrangThai())
//                .deleted(accountDto.getDeleted())
                .build();
    }

    public static ErrorResponseDto buildErrorResponse(RuntimeException exception, WebRequest request, Map<String, String> errors, HttpStatus httpStatus){
        return ErrorResponseDto.builder()
                .apiPath(request.getDescription(false))
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .statusCode(httpStatus)
                .errors(errors)
                .build();
    }
}
