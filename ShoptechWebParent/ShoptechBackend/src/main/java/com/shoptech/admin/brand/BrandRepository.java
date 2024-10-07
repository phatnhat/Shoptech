package com.shoptech.admin.brand;

import com.shoptech.admin.paging.SearchRepository;
import com.shoptech.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends SearchRepository<Brand, Long> {
    Long countById(Long id);

    Brand findByName(String name);

    @Query("SELECT b FROM Brand b WHERE " +
            "CONCAT(b.id, ' ', b.name) ILIKE %?1%")
    Page<Brand> findAll(String keyword, Pageable pageable);
}
