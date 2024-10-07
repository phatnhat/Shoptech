package com.shoptech.admin.product;

import com.shoptech.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByName(String name);

    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    @Modifying
    void updateEnabledStatus(Long id, boolean enabled);

    Long countById(Long id);

    @Query("SELECT p FROM Product p WHERE p.name ILIKE %?1% " +
            "OR p.shortDescription ILIKE %?1% " +
            "OR p.fullDescription ILIKE %?1% " +
            "OR p.brand.name ILIKE %?1% " +
            "OR p.category.name ILIKE %?1%")
    Page<Product> findAll(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = ?1 OR p.category.allParentIDs ILIKE %?2%")
    Page<Product> findAllInCategory(Long categoryId, String categoryIdMatch, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "(p.category.id = ?1 OR p.category.allParentIDs ILIKE %?2%) " +
            "AND (p.name ILIKE %?3% " +
            "OR p.shortDescription ILIKE %?3% " +
            "OR p.fullDescription ILIKE %?3% " +
            "OR p.brand.name ILIKE %?3% " +
            "OR p.category.name ILIKE %?3%)")
    Page<Product> searchInCategory(Long categoryId, String categoryIdMatch, String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.name ILIKE %?1%")
    Page<Product> searchProductByName(String keyword, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1")
    float getAvgRatingByProduct(Long productId);

    @Query("SELECT COUNT(r.id) FROM Review r WHERE r.product.id = ?1")
    int getReviewCountByProduct(Long productId);
}
