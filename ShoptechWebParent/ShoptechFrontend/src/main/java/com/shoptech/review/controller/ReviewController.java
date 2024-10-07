package com.shoptech.review.controller;

import com.shoptech.ControllerHelper;
import com.shoptech.Utility;
import com.shoptech.common.entity.Customer;
import com.shoptech.common.entity.Review;
import com.shoptech.common.entity.product.Product;
import com.shoptech.common.exception.ProductNotFoundException;
import com.shoptech.common.exception.ReviewNotFoundException;
import com.shoptech.customer.CustomerService;
import com.shoptech.product.ProductService;
import com.shoptech.review.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ReviewController {
    private String defaultRedirectURL = "redirect:/reviews/page/1?sortField=updatedAt&sortDir=desc";

    @Autowired private ReviewService reviewService;
    @Autowired private CustomerService customerService;
    @Autowired private ProductService productService;
    @Autowired private ControllerHelper controllerHelper;

    @GetMapping("/reviews")
    public String getFirstPage(){
        return defaultRedirectURL;
    }

    @GetMapping("/reviews/page/{pageNum}")
    public String listReviewsByCustomerByPage(Model model, HttpServletRequest request,
                                              @PathVariable(name = "pageNum") int pageNum,
                                              String keyword, String sortField, String sortDir){

        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        Page<Review> page = reviewService.listByCustomerByPage(customer, keyword, pageNum, sortField, sortDir);
        List<Review> listReviews = page.getContent();

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("moduleURL", "/reviews");
        model.addAttribute("listReviews", listReviews);

        long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
        model.addAttribute("startCount", startCount);

        long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
        if(endCount > page.getTotalElements()){
            endCount = page.getTotalElements();
        }

        model.addAttribute("endCount", endCount);

        return "reviews/reviews_customer";
    }

    @GetMapping( "/reviews/detail/{id}")
    public String viewReview(@PathVariable("id") Long id, Model model,
                             RedirectAttributes redirectAttributes,
                             HttpServletRequest request){

        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        try{
            Review review = reviewService.getByCustomerAndId(customer, id);
            model.addAttribute("review", review);

            return "reviews/review_details_modal";
        }catch (ReviewNotFoundException ex){
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping(path = "/ratings/{productAlias}/page/{pageNum}")
    public String listByProductByPage(Model model,
                                      @PathVariable(name = "productAlias") String productAlias,
                                      @PathVariable(name = "pageNum") int pageNum,
                                      String sortField, String sortDir){

        Product product = null;

        try{
            product = productService.getProduct(productAlias);
        }catch (ProductNotFoundException ex){
            return "error/404";
        }

        Page<Review> page = reviewService.listByProduct(product, pageNum, sortField, sortDir);
        List<Review> listReviews = page.getContent();

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("listReviews", listReviews);
        model.addAttribute("product", product);

        long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
        model.addAttribute("startCount", startCount);

        long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
        if(endCount > page.getTotalElements()){
            endCount = page.getTotalElements();
        }

        model.addAttribute("endCount", endCount);
        model.addAttribute("pageTitle", "Reviews for " + product.getShortName());

        return "reviews/reviews_product";
    }

    @GetMapping("/ratings/{productAlias}")
    public String listByProductFirstPage(@PathVariable(name = "productAlias") String productAlias, Model model){
        return listByProductByPage(model, productAlias, 1, "createdAt", "desc");
    }

    @GetMapping("/write_review/product/{productId}")
    public String showViewForm(@PathVariable("productId") Long productId, Model model, HttpServletRequest request){

        Review review = new Review();
        Product product = null;
        try{
            product = productService.getProduct(productId);
        }catch (ProductNotFoundException ex){
            return "error/404";
        }

        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());
        if(customerReviewed){
            model.addAttribute("customerReviewed", customerReviewed);
        }else{
            boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
            if(customerCanReview){
                model.addAttribute("customerCanReview", customerCanReview);
            }else{
                model.addAttribute("noReviewPermission", true);
            }
        }

        model.addAttribute("product", product);
        model.addAttribute("review", review);
        return "reviews/review_form";
    }

    @PostMapping("/post_review")
    public String saveReview(Model model, Review review, Long productId, HttpServletRequest request){
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);

        Product product = null;
        try{
            product = productService.getProduct(productId);
        }catch (ProductNotFoundException ex){
            return "error/404";
        }

        review.setProduct(product);
        review.setCustomer(customer);

        Review savedReview = reviewService.save(review);
        model.addAttribute("review", savedReview);

         return "reviews/review_done";
    }
}
