package com.shoptech.review;

import com.shoptech.common.entity.Review;
import com.shoptech.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.customer.id = ?1")
    Page<Review> findByCustomer(Long customerId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND (" +
            "r.headline ILIKE %?2% OR r.comment ILIKE %?2% OR " +
            "r.product.name ILIKE %?2%)")
    Page<Review> findByCustomer(Long customerId, String keyword, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND r.id = ?2")
    Review findByCustomerAndId(Long customerId, Long id);

    Page<Review> findByProduct(Product product, Pageable pageable);

    List<Review> findByProduct(Product product);

    @Query("SELECT COUNT(r.id) FROM Review r WHERE r.customer.id = ?1 AND " +
            "r.product.id = ?2")
    Long countByCustomerAndProduct(Long customerId, Long productId);
}
