package com.shoptech.admin.review;

import com.shoptech.admin.paging.SearchRepository;
import com.shoptech.common.entity.Review;
import com.shoptech.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends SearchRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.headline ILIKE %?1% OR " +
            "r.comment ILIKE %?1% OR r.product.name ILIKE %?1% OR " +
            "CONCAT(r.customer.firstName, ' ', r.customer.lastName) ILIKE %?1%")
    Page<Review> findAll(String keyword, Pageable pageable);

    List<Review> findAll();

    @Query("SELECT r FROM Review r WHERE r.product.id = ?1")
    List<Review> findByProduct(Long productId);

    List<Review> findByProduct(Product product);
}
