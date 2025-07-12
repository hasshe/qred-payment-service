package com.qred.qredpaymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class QredPaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QredPaymentServiceApplication.class, args);
    }

}
