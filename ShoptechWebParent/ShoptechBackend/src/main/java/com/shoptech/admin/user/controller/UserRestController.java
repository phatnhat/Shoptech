package com.shoptech.admin.user.controller;

import com.shoptech.admin.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {
    @Autowired
    private UserService userService;

    @PostMapping("/users/check_email")
    public String checkDuplicateEmail(@Param("id") Long id, @Param("email") String email){
        return userService.isEmailUnique(id, email) ? "OK" : "Duplicated";
    }
}
