package com.example.bookshop.accountservice;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

@SpringBootApplication
public class AccountServiceApplication {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		ClassLoader classLoader = AccountServiceApplication.class.getClassLoader();
		File fireBaseConfigFile = new File(Objects.requireNonNull(classLoader.getResource("serviceAccountKey.json")).getFile());
		try(FileInputStream configInputStream = new FileInputStream(fireBaseConfigFile.getAbsolutePath());) {
			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(configInputStream))
					.build();
			FirebaseApp.initializeApp(options);
		}

		SpringApplication.run(AccountServiceApplication.class, args);
	}

}