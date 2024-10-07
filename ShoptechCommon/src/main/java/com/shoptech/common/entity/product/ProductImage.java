package com.shoptech.common.entity.product;

import com.shoptech.common.entity.BasedEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product_images")
public class ProductImage extends BasedEntity {
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductImage(Long id, String name, Product product) {
        super(id);
        this.name = name;
        this.product = product;
    }

    @Transient
    public String getImagePath(){
        return "/product-images/" + product.getId() + "/extras/" + this.name;
    }
}
