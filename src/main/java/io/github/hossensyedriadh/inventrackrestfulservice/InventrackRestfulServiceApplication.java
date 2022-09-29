package io.github.hossensyedriadh.inventrackrestfulservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableAsync
@EnableWebMvc
@EnableJpaRepositories(basePackages = {"io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa"})
@SpringBootApplication
public class InventrackRestfulServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventrackRestfulServiceApplication.class, args);
    }

}
