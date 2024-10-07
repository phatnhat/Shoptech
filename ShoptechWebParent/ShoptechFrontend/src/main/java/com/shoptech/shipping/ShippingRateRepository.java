package com.shoptech.shipping;

import com.shoptech.common.entity.Country;
import com.shoptech.common.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, Long> {
    ShippingRate findByCountryAndState(Country country, String state);
}
