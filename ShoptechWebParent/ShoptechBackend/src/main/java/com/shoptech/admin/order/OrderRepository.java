package com.shoptech.admin.order;

import com.shoptech.common.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT o FROM Order o WHERE CONCAT('#', o.id) ILIKE %?1% OR"
            + " CONCAT(o.firstName, ' ', o.lastName) ILIKE %?1% OR"
            + " o.firstName ILIKE %?1% OR"
            + " o.lastName ILIKE %?1% OR o.phoneNumber ILIKE %?1% OR"
            + " o.addressLine1 ILIKE %?1% OR o.addressLine2 ILIKE %?1% OR"
            + " o.postalCode ILIKE %?1% OR o.city ILIKE %?1% OR"
            + " o.state ILIKE %?1% OR o.country ILIKE %?1% OR"
            + " CAST(o.paymentMethod AS STRING) ILIKE %?1% OR CAST(o.orderStatus AS STRING) ILIKE %?1% OR"
            + " o.customer.firstName ILIKE %?1% OR"
            + " o.customer.lastName ILIKE %?1%")
    Page<Order> findAll(String keyword, Pageable pageable);

    Long countById(Long id);

    @Query("SELECT NEW com.shoptech.common.entity.order.Order(o.id, o.createdAt, " +
            "o.productCost, o.subtotal, o.total) FROM Order o " +
            "WHERE o.createdAt BETWEEN ?1 AND ?2 " +
            "ORDER BY o.createdAt ASC")
    List<Order> findByCreatedAtBetween(Date startTime, Date endTime);
}
