package com.shoptech.common.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "countries")
public class Country extends IdBasedEntity{
    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false, length = 5)
    private String code;

    @JsonManagedReference
    @OneToMany(mappedBy = "country")
    private Set<State> states;

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public Country(Long id, String name, String code) {
        super(id);
        this.name = name;
        this.code = code;
    }

    public Country(Long id) {
        super(id);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
