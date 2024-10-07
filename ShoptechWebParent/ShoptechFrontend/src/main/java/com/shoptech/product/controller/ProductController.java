package com.shoptech.product.controller;

import com.shoptech.ControllerHelper;
import com.shoptech.Utility;
import com.shoptech.category.CategoryService;
import com.shoptech.common.entity.Category;
import com.shoptech.common.entity.Customer;
import com.shoptech.common.entity.Review;
import com.shoptech.common.entity.product.Product;
import com.shoptech.common.exception.CategoryNotFoundException;
import com.shoptech.common.exception.ProductNotFoundException;
import com.shoptech.customer.CustomerService;
import com.shoptech.product.ProductService;
import com.shoptech.review.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {
    @Autowired private CategoryService categoryService;
    @Autowired private ProductService productService;
    @Autowired private ReviewService reviewService;
    @Autowired private CustomerService customerService;
    @Autowired private ControllerHelper controllerHelper;

    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias,
                                        Model model) {
        return viewCategoryByPage(alias, 1, model);
    }

    @GetMapping("/c/{category_alias}/page/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias,
                                     @PathVariable("pageNum") int pageNum,
                                     Model model) {

        try {
            Category category = categoryService.getCategory(alias);
            List<Category> listCategoryParents = categoryService.getCategoryParents(category);
            Page<Product> pageProducts = productService.listByCategory(pageNum - 1, category.getId());
            List<Product> listProducts = pageProducts.getContent();

            long startCount = (long) (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
            long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
            if (endCount > pageProducts.getTotalElements()) endCount = pageProducts.getTotalElements();

            model.addAttribute("currentPage", pageNum);
            model.addAttribute("totalPages", pageProducts.getTotalPages());
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", pageProducts.getTotalElements());
            model.addAttribute("pageTitle", category.getName());
            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("listProducts", listProducts);
            model.addAttribute("category", category);

            return "products/products_by_category";
        } catch (CategoryNotFoundException ex) {
            return "error/404";
        }
    }

    @GetMapping("/p/{product_alias}")
    public String viewProductDetail(Model model, @PathVariable("product_alias") String alias,
                                    HttpServletRequest request){
        try{
            Product product = productService.getProduct(alias);
            List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
            Page<Review> listReviews = reviewService.list3MostRecentReviewsByProduct(product);

            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
            if(customer != null){
                boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());
                if(customerReviewed){
                    model.addAttribute("customerReviewed", true);
                }else{
                    boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
                    model.addAttribute("customerCanReview", customerCanReview);
                }
            }

            model.addAttribute("product", product);
            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("listReviews", listReviews);
            model.addAttribute("pageTitle", product.getShortName());
            return "products/products_detail";
        }catch (ProductNotFoundException ex){
            return "error/404";
        }
    }

    @GetMapping("/search")
    public String searchFirstPage(@Param("keyword") String keyword, Model model){
        return search(keyword, 1, model);
    }

    @GetMapping("/search/page/{pageNum}")
    public String search(@Param("keyword") String keyword,
                         @PathVariable int pageNum,
                         Model model){

        Page<Product> pageProducts = productService.search(keyword, pageNum - 1);
        List<Product> listResult = pageProducts.getContent();

        long startCount = (long) (pageNum - 1) * ProductService.SEARCH_RESULTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;
        if (endCount > pageProducts.getTotalElements()) endCount = pageProducts.getTotalElements();

        model.addAttribute("keyword", keyword);
        model.addAttribute("listResult", listResult);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("pageTitle", keyword + " - Search Result ");

        return "products/search_result";
    }
}
