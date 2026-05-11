package com.nabgha.digitalbanking;

import com.nabgha.digitalbanking.entities.AppUser;
import com.nabgha.digitalbanking.entities.Customer;
import com.nabgha.digitalbanking.enums.Role;
import com.nabgha.digitalbanking.repositories.AppUserRepository;
import com.nabgha.digitalbanking.repositories.CustomerRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableAsync
@SpringBootApplication
public class DigitalBankingApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(DigitalBankingApplication.class, args);
    }

    @Bean
    CommandLineRunner start(AppUserRepository appUserRepository,
                           CustomerRepository customerRepository,
                           PasswordEncoder passwordEncoder) {
        return args -> {
            if (!appUserRepository.existsByEmail("abdelatif.nabgha06@gmail.com")) {
                Customer customer = new Customer();
                customer.setName("Admin");
                customer.setEmail("abdelatif.nabgha06@gmail.com");
                customer = customerRepository.save(customer);

                AppUser admin = AppUser.builder()
                        .email("abdelatif.nabgha06@gmail.com")
                        .password(passwordEncoder.encode("admin1234"))
                        .role(Role.ADMIN)
                        .enabled(true)
                        .customer(customer)
                        .build();
                appUserRepository.save(admin);
                System.out.println("Admin account created: abdelatif.nabgha06@gmail.com / admin1234");
            }
        };
    }

}
