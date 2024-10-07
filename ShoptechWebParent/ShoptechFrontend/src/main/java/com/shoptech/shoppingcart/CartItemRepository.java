package com.shoptech.shoppingcart;

import com.shoptech.common.entity.CartItem;
import com.shoptech.common.entity.Customer;
import com.shoptech.common.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCustomer(Customer customer);

    List<CartItem> findByCustomerOrderByUpdatedAtDesc(Customer customer);

    CartItem findByCustomerAndProduct(Customer customer, Product product);

    @Modifying
    @Query("UPDATE CartItem c SET c.quantity = :quantity WHERE c.customer.id = :customerId AND c.product.id = :productId")
    void updateQuantity(@Param("quantity") Integer quantity, @Param("customerId") Long customerId,
                        @Param("productId") Long productId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.customer.id = ?1 AND c.product.id = ?2")
    void deleteByCustomerAndProduct(Long customerId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.customer.id = ?1")
    void deleteByCustomer(Long customerId);
}
