package com.shoptech.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication()
@EntityScan({"com.shoptech.common.entity", "com.shoptech.admin.user"})
@EnableJpaAuditing // dùng để bật JPA Auditing (@CreatedDate, @LastModifiedDate)
//@EnableJpaRepositories({"com.shopme.admin.user"})
public class ShoptechBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoptechBackendApplication.class, args);
    }

}
