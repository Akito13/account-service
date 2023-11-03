package com.example.bookshop.accountservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @SequenceGenerator(name = "account_id_seq", sequenceName = "accounts_account_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_id_seq")
    private Integer accountId;
    private String hoLot;
    private String ten;
    private String sdt;
    private String email;
    @Column(name = "`password`")
	private String password;
    private Boolean trangThai;
    private Boolean deleted;

    @JoinColumn(name = "role_id")
    @ManyToOne
    private Role roleId;
}
