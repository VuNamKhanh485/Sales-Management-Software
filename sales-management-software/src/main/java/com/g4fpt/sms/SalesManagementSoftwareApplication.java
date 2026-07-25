package com.g4fpt.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SalesManagementSoftwareApplication {


    public static void main(String[] args) {
        SpringApplication.run(SalesManagementSoftwareApplication.class, args);

    }
}
