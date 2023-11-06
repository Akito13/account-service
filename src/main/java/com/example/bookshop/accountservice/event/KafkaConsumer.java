package com.example.bookshop.accountservice.event;

import com.example.bookshop.accountservice.CommonConstants;
import com.example.bookshop.accountservice.model.Account;
import com.example.bookshop.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KafkaConsumer {
    private final AccountRepository accountRepo;

    @Autowired
    public KafkaConsumer(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

//    @KafkaListener(topics = "${constant.kafka.account-confirm-success}", groupId = "${spring.kafka.consumer.group-id}")
//    public void consumeAccountConfirmSuccess(String email) {
//        Optional<Account> optionalAccount = accountRepo.findByEmail(email);
//        if (optionalAccount.isEmpty()) {
//            System.out.println("Something went wrong");
//            return;
//        }
//        Account foundAccount = optionalAccount.get();
//        foundAccount.setTrangThai(true);
//        accountRepo.save(foundAccount);
//    }

    @KafkaListener(topics = "${constant.kafka.account-registration}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAccountRegistration(String email) {
        System.out.println("RECEIVED AN EMAIL CONFIRMATION FROM: " + email);
    }
}
