package com.shoptech.product;

import com.shoptech.common.entity.product.Product;
import com.shoptech.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    public static final int PRODUCTS_PER_PAGE = 10;
    public static final int SEARCH_RESULTS_PER_PAGE = 10;

    @Autowired
    private ProductRepository repo;

    public Page<Product> listByCategory(int pageNum, Long categoryId){
        String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
        Pageable pageable = PageRequest.of(pageNum, PRODUCTS_PER_PAGE);

        return repo.listByCategory(categoryId, categoryIdMatch, pageable);
    }

    public Product getProduct(String alias){
        Product product = repo.findByAlias(alias);
        if(product == null){
            throw new ProductNotFoundException("Could not find any product with alias " + alias);
        }
        return product;
    }

    public Product getProduct(Long id){
        try{
            return repo.findById(id).get();
        }catch (ProductNotFoundException ex){
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }
    }

    public Page<Product> search(String keyword, int pageNum){
        Pageable pageable = PageRequest.of(pageNum, SEARCH_RESULTS_PER_PAGE);
        return repo.search(keyword, pageable);
    }
}
