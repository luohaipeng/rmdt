package org.rmdt.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.rmdt.*"})
public class ClientApp {

    public static void main(String[] args) {
        SpringApplication.run(ClientApp.class,args);
    }
}
