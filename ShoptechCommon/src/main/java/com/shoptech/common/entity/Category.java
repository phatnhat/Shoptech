package com.shoptech.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category extends BasedEntity{
    @Column(length = 128, nullable = false, unique = true)
    private String name;

    @Column(length = 64, nullable = false, unique = true)
    private String alias;

    @Column(length = 128)
    private String image;

    private boolean enabled;

    @Column(name="all_parent_ids", length=256, nullable = true)
    private String allParentIDs;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("name asc")
    @ToString.Exclude
    private Set<Category> children = new HashSet<>();

    public Category(Long id) {
        this.id = id;
    }

    public Category(String name) {
        this.name = name;
        this.alias = name;
        this.image = "default.png";
    }

    public Category(String name, Category parent){
        this(name);
        this.parent = parent;
    }

    public Category(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public Category(Long id, String name, String alias) {
        super(id);
        this.name = name;
        this.alias = alias;
    }

    public static Category copyIdAndName(Long id, String name) {
        Category copyCategory = new Category();
        copyCategory.setId(id);
        copyCategory.setName(name);

        return copyCategory;
    }


    @Transient
    public String parentBreadcrumb(){
        Category parent = this.getParent();
        if(parent == null) return "";

        String parentName = parent.getName();
        String parentPath = parent.parentBreadcrumb();

        if (parentPath.isEmpty()) return parentName;

        return parentPath + "/" + parentName;
    }

    @Transient
    public String getImagePath(){
        if(this.id == null || this.image == null) return "/images/image-thumbnail.png";
        return "/category-images/" + this.id + "/" + this.image;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", image='" + image + '\'' +
                ", enabled=" + enabled +
                ", children=" + children +
                '}';
    }
}
