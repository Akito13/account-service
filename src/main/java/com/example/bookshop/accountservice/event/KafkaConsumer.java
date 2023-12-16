package com.example.bookshop.accountservice.event;

import com.example.bookshop.accountservice.CommonConstants;
import com.example.bookshop.accountservice.model.Account;
import com.example.bookshop.accountservice.repository.AccountRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KafkaConsumer {
    private final AccountRepository accountRepo;
    private final PasswordEncoder pwdEncoder;

    @Autowired
    public KafkaConsumer(AccountRepository accountRepo, PasswordEncoder pwdEncoder) {
        this.accountRepo = accountRepo;
        this.pwdEncoder = pwdEncoder;
    }

    @KafkaListener(
            topics = "${constant.kafka.account-confirm-success}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeAccountConfirmSuccess(ConsumerRecord<String, String> record) {
        String email = record.value();
        System.out.println("RECEIVED SUCCESS CONFIRMATION FROM: " + email);
        Optional<Account> optionalAccount = accountRepo.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            System.out.println("Something went wrong");
            return;
        }
        Account foundAccount = optionalAccount.get();
        foundAccount.setTrangThai(true);
        accountRepo.save(foundAccount);
    }

    @KafkaListener(
            topics = "${constant.kafka.account-password-change-confirmed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeAccountPasswordChangeConfirmed(ConsumerRecord<String, String> record) {
        String[] info = record.value().split(":");
        String email = info[0];
        String password = info[1];
        try {
            Optional<Account> result = accountRepo.findByEmail(email);
            if(result.isPresent()) {
                Account account = result.get();
                account.setPassword(pwdEncoder.encode(password));
                accountRepo.save(account);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

//    @KafkaListener(topics = "${constant.kafka.account-registration}", groupId = "${spring.kafka.consumer.group-id}")
//    public void consumeAccountRegistration(String email) {
//        System.out.println("RECEIVED AN EMAIL CONFIRMATION FROM: " + email);
//    }
}
