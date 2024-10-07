package com.shoptech.admin.category;

import com.shoptech.admin.paging.SearchRepository;
import com.shoptech.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends SearchRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE " +
            "CONCAT(c.id, ' ', c.name) ILIKE %?1%")
    Page<Category> findAll(String keyword, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findRootCategories();

    @Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
    @Modifying
    void updateEnabledStatus(Long id, boolean enabled);

    Category findByName(String name);

    Category findByAlias(String alias);

    Long countById(Long id);
}
