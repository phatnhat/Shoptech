package com.shoptech.customer.controller;

import com.shoptech.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerRestController {
    @Autowired
    private CustomerService customerService;

    @PostMapping("/check_unique_email")
    public String checkDuplicateEmail(@Param("id") Long id, @Param("email") String email){
        return customerService.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}
