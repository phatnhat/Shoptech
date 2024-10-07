package com.shoptech.admin.review;

import com.shoptech.common.entity.Customer;
import com.shoptech.common.entity.Review;
import com.shoptech.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.text.DecimalFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ReviewRepositoryTests {
    @Autowired private ReviewRepository repo;

    @Autowired private TestEntityManager entityManager;

    @Test
    public void testCreateReview(){
        Long productId = 3L;
        Product product = new Product(productId);

        Long customerId = 11L;
        Customer customer = new Customer(customerId);

        Review review = new Review();
        review.setProduct(product);
        review.setCustomer(customer);
        review.setHeadline("Test");
        review.setComment("test comment");
        review.setRating(5f);

        Review savedReview = repo.save(review);

        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getId()).isGreaterThan(0);
    }

    @Test
    public void testUpdateReviewCountAndAverageRating(){
        Long productId = 3L;
        Product product = entityManager.find(Product.class, productId);

        List<Review> reviews = repo.findByProduct(product);
        int reviewCount = reviews.size();
        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average().orElse(0.0);

//        DecimalFormat decimalFormat = new DecimalFormat("#.#");
//        decimalFormat.setMaximumFractionDigits(1);
//        averageRating = Float.parseFloat(decimalFormat.format(averageRating));

        int scale = (int) Math.pow(10, 1);
        averageRating = Math.round(averageRating * scale) / (double) scale;

        product.setReviewCount(reviewCount);
        product.setAverageRating(averageRating);

        System.out.println("Product = " + product);
        System.out.println("Review count = " + reviewCount);
        System.out.println("Avg Rating = " + averageRating);

        Product updatedProduct = entityManager.merge(product);
        assertThat(updatedProduct.getAverageRating()).isEqualTo(4.7);
    }
}
