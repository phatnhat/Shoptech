package com.shoptech.common.entity.product;

import com.shoptech.common.entity.BasedEntity;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "product_details")
public class ProductDetail extends BasedEntity {
    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String value;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductDetail(Long id, String name, String value, Product product) {
        super(id);
        this.name = name;
        this.value = value;
        this.product = product;
    }
}
