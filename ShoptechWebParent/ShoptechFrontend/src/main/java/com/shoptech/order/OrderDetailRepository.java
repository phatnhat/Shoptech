package com.shoptech.order;

import com.shoptech.common.entity.order.OrderDetail;
import com.shoptech.common.entity.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT COUNT(d) FROM OrderDetail d JOIN OrderTrack t ON d.order.id = t.order.id " +
            "WHERE d.product.id = ?1 AND d.order.customer.id = ?2 AND t.orderStatus = ?3")
    Long countByProductAndCustomerAndOrderStatus(Long productId, Long customerId, OrderStatus status);
}
