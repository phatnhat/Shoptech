package com.shoptech.common.entity.product;

import com.shoptech.common.entity.BasedEntity;
import com.shoptech.common.entity.Brand;
import com.shoptech.common.entity.Category;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "products")
public class Product extends BasedEntity {
    @Column(length = 256, unique = true, nullable = false)
    private String name;

    @Column(length = 256, unique = true, nullable = false)
    private String alias;

    @Column(length = 2042, nullable = false, name = "short_description")
    private String shortDescription;

    @Column(length = 4096, nullable = false, name = "full_description")
    private String fullDescription;

    @Column(columnDefinition = "boolean DEFAULT true")
    private boolean enabled;

    @Column(name = "in_stock", columnDefinition = "boolean DEFAULT true")
    private boolean inStock;

    @Column(columnDefinition = "real DEFAULT 0")
    private float cost;

    @Column(columnDefinition = "real DEFAULT 0")
    private float price;

    @Column(name = "discount_percent", columnDefinition = "real DEFAULT 0")
    private float discountPercent;

    @Column(columnDefinition = "real DEFAULT 0")
    private float length;

    @Column(columnDefinition = "real DEFAULT 0")
    private float height;

    @Column(columnDefinition = "real DEFAULT 0")
    private float width;

    @Column(columnDefinition = "real DEFAULT 0")
    private float weight;

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt asc")
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetail> details = new ArrayList<>();

    @Column(columnDefinition = "integer default 0")
    private int reviewCount;

    @Column(columnDefinition = "double precision default 0")
    private double averageRating;

    public Product(Long productId) {
        super(productId);
    }

    public Product(String name) {
        this.name = name;
    }
    public void addExtraImage(String imageName){
        this.images.add(new ProductImage(imageName, this));
    }

    @Transient
    public String getMainImagePath(){
        if(this.id == null || this.mainImage == null) return "/images/image-thumbnail.png";
        return "/product-images/" + this.id + "/" + this.mainImage;
    }

    @Transient private boolean customerCanReview;
    @Transient private boolean reviewedByCustomer;

    public void addDetail(String name, String value){
        this.details.add(new ProductDetail(name, value, this));
    }

    public void addDetail(Long id, String name, String value){
        this.details.add(new ProductDetail(id, name, value, this));
    }

    public boolean containsImageName(String fileName) {
        Iterator<ProductImage> iterator = images.iterator();

        while(iterator.hasNext()){
            ProductImage image = iterator.next();
            if(image.getName().equals(fileName)) return true;
        }
        return false;
    }

    @Transient
    public String getShortName() {
        if (name.length() > 70) {
            return name.substring(0, 70).concat("...");
        }
        return name;
    }

    @Transient
    public float getDiscountPrice(){
        if(this.discountPercent > 0) return this.price * ((100 - this.discountPercent) / 100);
        return this.price;
    }

    @Transient
    public String getURI(){
        return "/p/" + this.alias;
    }
}
