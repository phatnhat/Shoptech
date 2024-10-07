package com.shoptech.address;

import com.shoptech.common.entity.Address;
import com.shoptech.common.entity.Customer;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AddressService {
    @Autowired
    private AddressRepository repo;

    public List<Address> listAddressBook(Customer customer){
        return repo.findByCustomer(customer, Address.SORT_BY_CREATED_AT_DESC);
    }

    public void save(Address address){
        repo.save(address);
    }

    public Address get(Long addressId, Long customerId){
        return repo.findByIdAndCustomer(addressId, customerId);
    }

    public void delete(Long addressId, Long customerId){
        repo.deleteByIdAndCustomer(addressId, customerId);
    }

    public void setDefaultAddress(Long defaultAddressId, Long customerId){
        Address address = get(defaultAddressId, customerId);
        List<Address> addressOthers = repo.findByIdNot(defaultAddressId);
        if(address != null){
            address.setDefaultForShipping(true);
            addressOthers.add(address);
        }
        addressOthers.stream().filter(addr -> !addr.getId().equals(defaultAddressId))
                .forEach(addr -> addr.setDefaultForShipping(false));
        repo.saveAll(addressOthers);
    }

    public Address getDefaultAddress(Customer customer){
        return repo.findDefaultByCustomer(customer.getId());
    }
}
