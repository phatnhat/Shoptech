package com.shoptech.admin.product;

import com.shoptech.admin.paging.PagingAndSortingHelper;
import com.shoptech.admin.review.ReviewRepository;
import com.shoptech.admin.review.ReviewService;
import com.shoptech.common.entity.Review;
import com.shoptech.common.entity.product.Product;
import com.shoptech.common.exception.ProductNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ProductService {
    public static final int PRODUCTS_PER_PAGE = 10;
    public static final int PRODUCTS_SEARCH_PER_PAGE = 5;

    @Autowired
    private ProductRepository repo;

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Product> listAll(){
        return repo.findAll();
    }

    public Product save(Product product) {
        Product updatedProduct = repo.save(product);
        updateReviewCountAndAverageRating(updatedProduct);
        return updatedProduct;
    }

    public void updateReviewCountAndAverageRating(Product product){
        List<Review> reviews = reviewRepository.findByProduct(product);
        int reviewCount = reviews.size();
        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average().orElse(0.0);

        int scale = (int) Math.pow(10, 1);
        averageRating = Math.round(averageRating * scale) / (double) scale;

        product.setReviewCount(reviewCount);
        product.setAverageRating(averageRating);
    }

    public void saveProductPrice(Product productInForm){
        Product productInDB = repo.findById(productInForm.getId()).get();
        productInDB.setCost(productInForm.getCost());
        productInDB.setPrice(productInForm.getPrice());
        productInDB.setDiscountPercent(productInForm.getDiscountPercent());
        repo.save(productInDB);
    }

    public String checkUnique(Long id, String name){
        boolean isCreatingNew = (id == null || id == 0);

        Product productByName = repo.findByName(name);

        if(isCreatingNew){
            if(productByName != null) return "DuplicatedName";
        }else{
            if(productByName != null && productByName.getId() != id) return "DuplicatedName";
        }

        return "OK";
    }

    public void updateEnabled(Long id, boolean enabled){
        repo.updateEnabledStatus(id, enabled);
    }

    public void delete(Long id){
        Long countById = repo.countById(id);
        if(countById == null || countById == 0){
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }

        repo.deleteById(id);
    }

    public Product get(Long id){
        try{
            return repo.findById(id).get();
        }catch (NoSuchElementException e){
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper, Long categoryId){
        Pageable pageable = helper.createPageable(PRODUCTS_PER_PAGE, pageNum);
        Page<?> page = null;

        if(helper.getKeyword() != null && !helper.getKeyword().isEmpty()) {
            if(categoryId != null && categoryId > 0) {
                String categoryInMatch = "-" + String.valueOf(categoryId) + "-";
                page = repo.searchInCategory(categoryId, categoryInMatch, helper.getKeyword(), pageable);
            }else{
                page = repo.findAll(helper.getKeyword(), pageable);
            }
        }else{
            if(categoryId != null && categoryId > 0) {
                String categoryInMatch = "-" + String.valueOf(categoryId) + "-";
                page = repo.findAllInCategory(categoryId, categoryInMatch, pageable);
            }else{
                page = repo.findAll(pageable);
            }
        }

        helper.updateModelAttributes(pageNum, page);
    }

    public void searchProducts(int pageNum, PagingAndSortingHelper helper){
        Pageable pageable = helper.createPageable(PRODUCTS_SEARCH_PER_PAGE, pageNum);
        String keyword = helper.getKeyword();

        Page<Product> page = repo.searchProductByName(keyword, pageable);

        helper.updateModelAttributes(pageNum, page);
    }
}
