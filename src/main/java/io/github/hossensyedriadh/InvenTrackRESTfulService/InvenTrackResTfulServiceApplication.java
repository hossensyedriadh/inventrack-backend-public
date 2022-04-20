package io.github.hossensyedriadh.InvenTrackRESTfulService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.util.TimeZone;

@EnableJpaRepositories(basePackages = "io.github.hossensyedriadh.InvenTrackRESTfulService.repository")
@EntityScan(basePackages = "io.github.hossensyedriadh.InvenTrackRESTfulService.entity")
@SpringBootApplication
public class InvenTrackResTfulServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvenTrackResTfulServiceApplication.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Dhaka")));
    }

}
