package com.shoptech.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "shipping_rates")
public class ShippingRate extends BasedEntity{
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(nullable = false, length = 45)
    private String state;

    private float rate;

    private int days;

    @Column(name = "cod_supported")
    private boolean codSupported;
}
