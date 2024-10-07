package com.shoptech.admin.review;

import com.shoptech.admin.paging.PagingAndSortingHelper;
import com.shoptech.common.entity.Review;
import com.shoptech.common.entity.product.Product;
import com.shoptech.common.exception.ReviewNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReviewService {
    public static final int REVIEWS_PER_PAGE = 10;

    @Autowired private ReviewRepository repo;

    public void listByPage(int pageNum, PagingAndSortingHelper helper){
        helper.listEntities(pageNum, REVIEWS_PER_PAGE, repo);
    }

    public Review get(Long id) throws ReviewNotFoundException {
        try{
            return repo.findById(id).get();
        }catch (NoSuchElementException ex){
            throw new ReviewNotFoundException("Could not find any reviews with ID " + id);
        }
    }

    public void save(Review reviewInForm){
        Review reviewInDB = repo.findById(reviewInForm.getId()).get();
        reviewInDB.setHeadline(reviewInForm.getHeadline());
        reviewInDB.setComment(reviewInForm.getComment());

        repo.save(reviewInDB);
        updateReviewCountAndAverageRating(reviewInDB.getProduct());
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
    }

    public void delete(Long reviewId) throws ReviewNotFoundException {
        if(!repo.existsById(reviewId)){
            throw new ReviewNotFoundException("Could not find any reviews with ID " + reviewId);
        }

        repo.deleteById(reviewId);
    }
}
