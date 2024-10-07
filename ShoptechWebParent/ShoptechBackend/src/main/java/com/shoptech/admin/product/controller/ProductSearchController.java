package com.shoptech.admin.product.controller;

import com.shoptech.admin.paging.PagingAndSortingHelper;
import com.shoptech.admin.paging.PagingAndSortingParam;
import com.shoptech.admin.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProductSearchController {
    @Autowired private ProductService productService;

    @GetMapping("/orders/search_product")
    public String showSearchProductPage(){
        //return "orders/search_product";
        return "redirect:/orders/search_product/page/1?sortField=name&sortDir=asc&keyword=";
    }

    @PostMapping("/orders/search_product")
    public String searchProducts(String keyword){
        return "redirect:/orders/search_product/page/1?sortField=name&sortDir=asc&keyword=" + keyword;
    }

    @GetMapping("/orders/search_product/page/{pageNum}")
    public String showSearchProductByPage(@PathVariable int pageNum,
                                          @PagingAndSortingParam(moduleURL = "/orders/search_product", listName = "listProducts")
                                                PagingAndSortingHelper helper){

        productService.searchProducts(pageNum, helper);

        return "orders/search_product";
    }
}
