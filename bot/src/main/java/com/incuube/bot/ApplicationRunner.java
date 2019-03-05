package com.incuube.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication(scanBasePackages = "com.incuube.bot")
public class ApplicationRunner {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(ApplicationRunner.class, args);
    }
}
