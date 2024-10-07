package com.shoptech.admin.product;

import com.shoptech.common.entity.Brand;
import com.shoptech.common.entity.Category;
import com.shoptech.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepositoryTests {
    @Autowired
    private ProductRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateProduct(){
        Brand brand = entityManager.find(Brand.class, 1);
        Category category = entityManager.find(Category.class, 7);

        Product product = new Product();
        product.setName("Samsung Galaxy A31");
        product.setAlias("samsung-galaxy-a31");
        product.setShortDescription("A good smartphone from Samsung");
        product.setFullDescription("This is very good smart phone full description");
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(456);

        Product savedProduct = repo.save(product);
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllProducts(){
        Iterable<Product> products = repo.findAll();
        products.forEach(System.out::println);
    }
}
