package com.shoptech.category;

import com.shoptech.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTests {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testListEnabledCategories(){
        List<Category> categories = categoryRepository.findAllEnabled();
        categories.forEach(System.out::println);
    }

    @Test
    public void testFindCategoryByAlias(){
        String alias = "electronics";
        Category category = categoryRepository.findByAliasEnabled(alias);
        assertThat(category).isNotNull();
    }
}
