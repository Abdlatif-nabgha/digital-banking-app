package com.nabgha.digitalbanking;

import com.nabgha.digitalbanking.entities.AppUser;
import com.nabgha.digitalbanking.enums.Role;
import com.nabgha.digitalbanking.repositories.AppUserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DigitalBankingApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(DigitalBankingApplication.class, args);
    }

}
