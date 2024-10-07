package com.shoptech.admin.review.controller;

import com.shoptech.admin.paging.PagingAndSortingHelper;
import com.shoptech.admin.paging.PagingAndSortingParam;
import com.shoptech.admin.review.ReviewService;
import com.shoptech.common.entity.Review;
import com.shoptech.common.exception.ReviewNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
    private String defaultRedirectURL = "redirect:/reviews/page/1?sortField=createdAt&sortDir=asc";

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(@PagingAndSortingParam(listName = "listReviews", moduleURL = "/reviews") PagingAndSortingHelper helper,
                             @PathVariable(name = "pageNum") int pageNum) {

        reviewService.listByPage(pageNum, helper);

        return "reviews/reviews";
    }

    @GetMapping("/detail/{id}")
    public String viewReview(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Review review = reviewService.get(id);
            model.addAttribute("review", review);

            return "reviews/review_details_modal";
        } catch (ReviewNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/edit/{id}")
    public String editReview(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Review review = reviewService.get(id);

            model.addAttribute("review", review);
            model.addAttribute("pageTitle", String.format("Edit Review (ID:%d)", id));
            return "reviews/review_form";
        } catch (ReviewNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @PostMapping("/save")
    public String saveReview(Review reviewInForm, RedirectAttributes redirectAttributes){
        reviewService.save(reviewInForm);
        redirectAttributes.addFlashAttribute("message", "The review ID " + reviewInForm.getId() + " has been updated successfully.");
        return defaultRedirectURL;
    }

    @GetMapping("/delete/{id}")
    public String deleteReview(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        try{
            reviewService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The review ID " + id + " has been deleted.");
        }catch (ReviewNotFoundException ex){
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return defaultRedirectURL;
    }
}
