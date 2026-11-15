package com.example.supportticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SupportTicketApplication {
    public static void main(String[] args) {
        SpringApplication.run(SupportTicketApplication.class, args);
    }
}
