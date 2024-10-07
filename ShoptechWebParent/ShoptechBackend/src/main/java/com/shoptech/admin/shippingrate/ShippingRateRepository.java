package com.shoptech.admin.shippingrate;

import com.shoptech.admin.paging.SearchRepository;
import com.shoptech.common.entity.ShippingRate;
import com.shoptech.common.entity.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRateRepository extends SearchRepository<ShippingRate, Long> {
    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.id = ?1 AND sr.state = ?2")
    ShippingRate findByCountryAndState(Long countryId, String state);

    @Query("SELECT sr FROM ShippingRate sr WHERE LOWER(sr.country.name) LIKE %?1% " +
            "OR LOWER(sr.state) LIKE %?1%")
    Page<ShippingRate> findAll(String keyword, Pageable pageable);

    Long countById(Long id);
}
