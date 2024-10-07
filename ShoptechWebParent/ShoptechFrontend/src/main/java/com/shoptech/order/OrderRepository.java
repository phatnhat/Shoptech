package com.shoptech.order;

import com.shoptech.common.entity.Customer;
import com.shoptech.common.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od JOIN od.product p "
            + "WHERE o.customer.id = ?2 "
            + "AND (p.name ILIKE %?1% OR CAST(o.orderStatus AS STRING) ILIKE %?1%)")
    Page<Order> findAll(String keyword, Long customerId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.customer.id = ?1")
    Page<Order> findAll(Long customerId, Pageable pageable);

    Order findByIdAndCustomer(Long id, Customer customer);
}
