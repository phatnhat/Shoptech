package com.shoptech.address;

import com.shoptech.common.entity.Address;
import com.shoptech.common.entity.Customer;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByCustomer(Customer customer, Sort sortByCreatedAtDesc);

    List<Address> findByIdNot(Long addressId);

    @Query("SELECT a FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
    Address findByIdAndCustomer(Long addressId, Long customerId);

    @Modifying
    @Query("DELETE FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
    void deleteByIdAndCustomer(Long addressId, Long customerId);

    @Query("SELECT a FROM Address a WHERE a.customer.id = ?1 AND a.defaultForShipping = true")
    Address findDefaultByCustomer(Long customerId);
}
