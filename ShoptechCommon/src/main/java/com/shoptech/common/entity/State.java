package com.shoptech.common.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "states")
public class State extends IdBasedEntity{
    @Column(nullable = false, length = 45)
    private String name;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
}
