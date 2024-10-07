package com.shoptech.admin.product.controller;

import com.shoptech.admin.product.ProductDTO;
import com.shoptech.admin.product.ProductService;
import com.shoptech.common.entity.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductRestController {
    @Autowired
    private ProductService productService;

    @PostMapping("/check_unique")
    public String checkUnique(@Param("id") Long id, @Param("name") String name){
        return productService.checkUnique(id, name);
    }

    @GetMapping("/get/{id}")
    public ProductDTO getProductInfo(@PathVariable Long id){
        Product product = productService.get(id);
        return new ProductDTO(product.getName(), product.getMainImagePath(),
                product.getDiscountPrice(), product.getCost());
    }
}
