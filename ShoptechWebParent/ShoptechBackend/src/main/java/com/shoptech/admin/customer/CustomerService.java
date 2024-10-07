package com.shoptech.admin.customer;

import com.shoptech.admin.paging.PagingAndSortingHelper;
import com.shoptech.admin.setting.country.CountryRepository;
import com.shoptech.common.entity.Country;
import com.shoptech.common.entity.Customer;
import com.shoptech.common.exception.CustomerNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CustomerService {
    public static final int CUSTOMERS_PER_PAGE = 10;

    @Autowired
    private CustomerRepository repo;

    @Autowired
    private CountryRepository countryRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, CUSTOMERS_PER_PAGE, repo);
    }

    public void updateCustomerEnabledStatus(Long id, boolean enabled) {
        repo.updateEnabledStatus(id, enabled);
    }

    public List<Country> listAllCountries() {
        return countryRepo.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(Long id, String email){
        Customer customer = repo.findByEmail(email);

        if(customer == null) return true;

        boolean isCreatingNew = (id == null);

        if(isCreatingNew){
            if(customer != null) return false;
        }else{
            if(!customer.getId().equals(id)) return false;
        }

        return true;
    }

    public void save(Customer customerInForm) {
        Customer customerInDB = repo.findById(customerInForm.getId()).get();

        if (!customerInForm.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
            customerInForm.setPassword(encodedPassword);
        } else {

            customerInForm.setPassword(customerInDB.getPassword());
        }

        customerInForm.setEnabled(customerInDB.isEnabled());
        customerInForm.setVerificationCode(customerInDB.getVerificationCode());
        customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
        customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());

        repo.save(customerInForm);
    }

    public void delete(Long id) throws CustomerNotFoundException {
        Long count = repo.countById(id);
        if (count == null || count == 0) {
            throw new CustomerNotFoundException("Could not find any customers with ID " + id);
        }

        repo.deleteById(id);
    }

    public Customer get(Long id) throws CustomerNotFoundException {
        try {
            return repo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new CustomerNotFoundException("Could not find any customers with ID " + id);
        }
    }

    public List<Customer> listAll() {

        Sort firstNameSorting =  Sort.by("id").ascending();

        List<Customer> customerList = new ArrayList<>();
        repo.findAll(firstNameSorting).forEach(customerList::add);
        return customerList;
    }
}
