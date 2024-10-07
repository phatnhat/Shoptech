package com.shoptech.customer;

import com.shoptech.common.entity.*;
import com.shoptech.common.exception.CustomerNotFoundException;
import com.shoptech.setting.CountryRepository;
import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CustomerService {
    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CustomerRepository repo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Country> listAllCountries(){
        return countryRepository.findAllByOrderByNameAsc();
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

    public void registerCustomer(Customer customer){
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setAuthenticationType(AuthenticationType.DATABASE);

        String randomCode = RandomString.make(64);
        customer.setVerificationCode(randomCode);

        repo.save(customer);
    }

    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }

    public boolean verify(String verificationCode){
        Customer customer = repo.findByVerificationCode(verificationCode);

        if(customer == null || customer.isEnabled()) return false;

        repo.enable(customer.getId());
        return true;
    }

    public Customer getCustomerByEmail(String email){
        return repo.findByEmail(email);
    }

    public void updateAuthenticationType(Customer customer, AuthenticationType type){
        if(!customer.getAuthenticationType().equals(type)){
            repo.updateAuthenticationType(customer.getId(), type);
        }
    }

    public void addNewCustomerUponOAthLogin(String name, String email, String countryCode, AuthenticationType authenticationType){
        Customer customer = new Customer();
        setName(name, customer);
        customer.setEmail(email);
        customer.setEnabled(true);
        customer.setAuthenticationType(authenticationType);
        customer.setPassword("");
        customer.setAddressLine1("");
        customer.setCity("");
        customer.setState("");
        customer.setPhoneNumber("");
        customer.setPostalCode("");
        customer.setCountry(countryRepository.findByCode(countryCode));
        repo.save(customer);
    }

    public void setName(String name, Customer customer){
        String[] nameArray = name.split(" ");
        if(nameArray.length < 2){
            customer.setFirstName(name);
            customer.setLastName("");
        }else{
            String firstName = nameArray[0].strip();
            customer.setFirstName(firstName);
            String lastName = name.replaceFirst(firstName, "").strip();
            customer.setLastName(lastName);
        }
    }

    public void update(Customer customerInForm){
        Customer customerInDB = repo.findById(customerInForm.getId()).get();

        if(customerInForm.getAuthenticationType().equals(AuthenticationType.DATABASE)){
            if (!customerInForm.getPassword().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
                customerInForm.setPassword(encodedPassword);
            } else {
                customerInForm.setPassword(customerInDB.getPassword());
            }
        }else{
            customerInForm.setPassword(customerInDB.getPassword());
        }

        customerInForm.setEnabled(customerInDB.isEnabled());
        customerInForm.setVerificationCode(customerInDB.getVerificationCode());
        customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
        customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());

        repo.save(customerInForm);
    }

    public String updateResetPasswordToken(String email) {
        Customer customer = repo.findByEmail(email);
        if(customer != null){
            String token = RandomString.make(30);
            customer.setResetPasswordToken(token);
            repo.save(customer);
            return token;
        }else{
            throw new CustomerNotFoundException("Could not find any customer with the email " + email);
        }
    }

    public Customer getByResetPasswordToken(String token){
        return repo.findByResetPasswordToken(token);
    }

    public void updatePassword(String token, String newPassword){
        Customer customer = repo.findByResetPasswordToken(token);
        if(customer == null){
            throw new CustomerNotFoundException("No customer found: invalid token");
        }
        customer.setPassword(newPassword);
        customer.setResetPasswordToken(null);
        encodePassword(customer);
        repo.save(customer);
    }
}
