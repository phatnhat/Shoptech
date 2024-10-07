package com.shoptech.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "brands")
public class Brand extends BasedEntity{
    @Column(nullable = false, length = 45, unique = true)
    private String name;

    @Column(length = 128)
    private String logo;

    @ManyToMany
    @JoinTable(name = "brands_categories",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    public Brand(String name) {
        this.name = name;
    }

    @Transient
    public String getLogoPath(){
        if(this.id == null || this.logo == null) return "/images/image-thumbnail.png";
        return "/brand-images/" + this.id + "/" + this.logo;
    }
}
