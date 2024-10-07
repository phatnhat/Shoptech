package com.shoptech.setting;

import com.shoptech.common.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    List<Country> findAllByOrderByNameAsc();

    Country findByName(String name);

    @Query("SELECT c FROM Country c WHERE c.code = ?1")
    Country findByCode(String countryCode);
}
