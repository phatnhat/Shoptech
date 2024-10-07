package com.shoptech.product;

import com.shoptech.common.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.enabled = true AND " +
            "(p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%) " +
            "ORDER BY p.name ASC")
    Page<Product> listByCategory(Long categoryId, String categoryIdMatch, Pageable pageable);

    Product findByAlias(String alias);

    @Query(value = "SELECT * FROM products WHERE enabled = true AND " +
            "to_tsvector('simple', name || ' ' || short_description || ' ' || full_description) " +
            "@@ to_tsquery('simple', replace(?1, ' ', ' | '))", nativeQuery = true)
    Page<Product> search(String keyword, Pageable pageable);


}
