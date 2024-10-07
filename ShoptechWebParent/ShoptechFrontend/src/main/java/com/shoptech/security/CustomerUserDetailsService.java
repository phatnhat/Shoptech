package com.shoptech.security;

import com.shoptech.common.entity.Customer;
import com.shoptech.common.entity.User;
import com.shoptech.customer.CustomerRepository;
import com.shoptech.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
    private final CustomerRepository customerRepository;

    public CustomerUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email);
        if(customer != null){
            return new CustomerUserDetails(customer);
        }
        throw new UsernameNotFoundException("Could not find customer with email: " + email);
    }
}
