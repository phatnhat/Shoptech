package com.shoptech.shoppingcart.controller;

import com.shoptech.ControllerHelper;
import com.shoptech.Utility;
import com.shoptech.common.entity.Customer;
import com.shoptech.common.exception.CustomerNotFoundException;
import com.shoptech.customer.CustomerService;
import com.shoptech.shoppingcart.ShoppingCartException;
import com.shoptech.shoppingcart.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShoppingCartRestController {
    @Autowired private ShoppingCartService shoppingCartService;
    @Autowired private CustomerService customerService;
    @Autowired private ControllerHelper controllerHelper;


    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity,
                                   HttpServletRequest request){
        try{
            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
            Integer updatedQuantity = shoppingCartService.addProduct(productId, quantity, customer);
            return updatedQuantity + " item(s) of this product were added to your shopping cart.";
        }catch (CustomerNotFoundException e){
            return "You must login to add this product to cart";
        }catch (ShoppingCartException e){
            return e.getMessage();
        }
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateQuantity(@PathVariable Long productId, @PathVariable Integer quantity,
                                   HttpServletRequest request){

        try{
            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
            float subtotal = shoppingCartService.updateQuantity(productId, quantity, customer);
            return String.valueOf(subtotal);
        }catch (CustomerNotFoundException e){
            return "You must login to change quantity of product";
        }
    }

    @DeleteMapping("/cart/remove/{productId}")
    public String removeProduct(@PathVariable Long productId,
                                HttpServletRequest request){

        try{
            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
            shoppingCartService.removeProduct(productId, customer);
            return "The product has been remove from your shopping cart.";
        }catch (CustomerNotFoundException e){
            return "You must login to change quantity of product";
        }
    }
}
