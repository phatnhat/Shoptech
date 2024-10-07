package com.shoptech.admin.customer.controller;

import com.shoptech.admin.customer.CustomerService;
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

    @PostMapping("/check_email")
    public String checkDuplicateEmail(@Param("id") Long id, @Param("email") String email){
        if(customerService.isEmailUnique(id, email)) return "OK";
        else return "Duplicated";
    }
}
