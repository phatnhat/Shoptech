package com.shoptech.review;

import com.shoptech.common.entity.Customer;
import com.shoptech.common.entity.Review;
import com.shoptech.common.entity.order.OrderStatus;
import com.shoptech.common.entity.product.Product;
import com.shoptech.common.exception.ReviewNotFoundException;
import com.shoptech.order.OrderDetailRepository;
import com.shoptech.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    public static final int REVIEWS_PER_PAGE = 1;

    @Autowired private ReviewRepository repo;
    @Autowired private OrderDetailRepository orderDetailRepository;
    @Autowired private ProductRepository productRepository;

    public Page<Review> listByCustomerByPage(Customer customer, String keyword, int pageNum,
                                             String sortField, String sortDir){
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);

        if(keyword != null){
            return repo.findByCustomer(customer.getId(), keyword, pageable);
        }

        return repo.findByCustomer(customer.getId(), pageable);
    }

    public Review getByCustomerAndId(Customer customer, Long reviewId) throws ReviewNotFoundException {
        Review review = repo.findByCustomerAndId(customer.getId(), reviewId);
        if(review == null) throw new ReviewNotFoundException("Customer does not have nay reviews with ID " + reviewId);
        return review;
    }

    public Page<Review> list3MostRecentReviewsByProduct(Product product){
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(0, 3, sort);

        return repo.findByProduct(product, pageable);
    }

    public Page<Review> listByProduct(Product product, int pageNum, String sortField, String sortDir){
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);

        return repo.findByProduct(product, pageable);
    }

    public boolean didCustomerReviewProduct(Customer customer, Long productId){
        Long count = repo.countByCustomerAndProduct(customer.getId(), productId);
        return count > 0;
    }

    public boolean canCustomerReviewProduct(Customer customer, Long productId){
        Long count = orderDetailRepository.countByProductAndCustomerAndOrderStatus(productId, customer.getId(), OrderStatus.DELIVERED);
        return count > 0;
    }

    public Review save(Review review){
        Review savedReview = repo.save(review);
        updateReviewCountAndAverageRating(savedReview.getProduct());
        return savedReview;
    }

    public void updateReviewCountAndAverageRating(Product product){
        List<Review> reviews = repo.findByProduct(product);

        int reviewCount = reviews.size();
        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average().orElse(0.0);

        int scale = (int) Math.pow(10, 1);
        averageRating = Math.round(averageRating * scale) / (double) scale;

        product.setReviewCount(reviewCount);
        product.setAverageRating(averageRating);

        productRepository.save(product);
    }
}
