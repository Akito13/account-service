package com.example.bookshop.accountservice;

import com.example.bookshop.accountservice.controller.AccountController;
import com.example.bookshop.accountservice.dto.AccountDto;
import com.example.bookshop.accountservice.exception.AccountAlreadyExistsException;
import com.example.bookshop.accountservice.exception.AccountNotFoundException;
import com.example.bookshop.accountservice.model.Account;
import com.example.bookshop.accountservice.model.Role;
import com.example.bookshop.accountservice.repository.AccountRepository;
import com.example.bookshop.accountservice.service.AccountServiceImpl;
import org.junit.jupiter.api.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = {AccountMockitoTest.class})
@AutoConfigureMockMvc
@WebMvcTest(AccountController.class)
public class AccountMockitoTest {
    @Mock
    AccountRepository accountRepository;

    @Autowired
    private MockMvc mockMvc;

    private Role mockAdminRole = new Role();
    private Role mockUserRole = new Role();

    @BeforeEach
    public void setUp() {
        mockAdminRole.setRoleId("ROLE_ADMIN");
        mockAdminRole.setAccounts(List.of(mockAccounts.get(0)));
        mockAccounts.get(0).setRoleId(mockAdminRole);

        mockUserRole.setRoleId("ROLE_USER");
        mockAccounts.get(1).setRoleId(mockUserRole);
        mockUserRole.setAccounts(List.of(mockAccounts.get(1)));
    }

    private List<Account> mockAccounts = List.of(
            Account.builder()
                    .accountId(1L)
                    .email("akitokami12@gmail.com")
                    .password("nhan1234")
                    .ten("Nhân")
                    .hoLot("Nguyễn Thiện")
                    .sdt("0912036271")
                    .trangThai(true)
                    .deleted(false).build(),
            Account.builder()
                    .accountId(2L)
                    .email("nhanntps12345@fpt.edu.vn")
                    .password("nhanps12")
                    .ten("Nhân")
                    .hoLot("Nguyễn Thành")
                    .sdt("0128935472")
                    .trangThai(true)
                    .deleted(false).build());

    @InjectMocks
    AccountServiceImpl accountService;

    @Test
    @Order(1)
    @DisplayName("Test AccountAlreadyExistException")
    public void test_createAccount1() {
        AccountDto accountDto = AccountDto.builder().email("akitokami12@gmail.com").password("Nhan1234").build();
        Mockito.when(accountRepository.findByEmail(accountDto.getEmail()))
                .thenReturn(Optional.of(mockAccounts.get(0)));
        Assertions.assertThrows(AccountAlreadyExistsException.class, () -> {
            accountService.createAccount(accountDto);
        });
//        BDDMockito.given().willThrow(new AccountAlreadyExistsException("Tài khoản đã tồn tài với email " + accountDto.getEmail()));
//        Mockito.<AccountDto>when(accountService.createAccount(accountDto)).then()
    }

    @Test
    @Order(2)
    @DisplayName("Test Get Nonexistent Account")
    @WithMockUser("akitokami12@gmail.com")
    public void test_getAccount() throws Exception {
        Mockito.when(accountRepository.findById(3L)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/account/3"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
