package com.example.bookshop.accountservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountDto {

    @NotNull(message = "Account ID không thể thiếu", groups = {RequireId.class})
    private Integer accountId;

    @NotNull(message = "Họ và tên lót chưa có", groups = {RegisterInfo.class})
    @Size(message = "Họ và tên lót phải trên 1 ký tự", min = 1, groups = {RegisterInfo.class})
    private String hoLot;

    @NotNull(message = "Tên chưa có", groups = {RegisterInfo.class})
    @Size(message = "Tên phải trên 1 ký tự", min = 1, groups = {RegisterInfo.class})
    private String ten;

    @NotNull(message = "SĐT chưa có", groups = {RegisterInfo.class})
    @Size(message = "SĐT không hợp lệ", min = 10, max = 11,groups = {RegisterInfo.class})
    private String sdt;

    @NotBlank(message = "Email chưa có", groups = {RegisterInfo.class})
    @Email(message = "Email không hợp lệ", groups = {RegisterInfo.class})
    private String email;

    @NotNull(message = "Chưa nhập mật khẩu", groups = {RegisterInfo.class})
    @Size(message = "Mật khẩu phãi từ 8 đến 16 ký tự", min = 8, max = 16, groups = {RegisterInfo.class})
    private String password;

//    private String role;
//
//    private Boolean trangThai;
//
//    private Boolean deleted;
    public interface RequireId{}
    public interface RegisterInfo {}
}
