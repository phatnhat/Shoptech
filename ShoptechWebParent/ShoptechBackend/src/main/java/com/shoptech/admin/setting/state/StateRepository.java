package com.shoptech.admin.setting.state;

import com.shoptech.common.entity.Country;
import com.shoptech.common.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
    List<State> findByCountryOrderByNameAsc(Country country);

    State findByNameAndCountryId(String name, Long country_id);
}
