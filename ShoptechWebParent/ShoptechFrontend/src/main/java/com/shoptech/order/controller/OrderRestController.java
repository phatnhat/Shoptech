package com.shoptech.order.controller;

import com.shoptech.ControllerHelper;
import com.shoptech.Utility;
import com.shoptech.common.entity.Customer;
import com.shoptech.common.exception.CustomerNotFoundException;
import com.shoptech.common.exception.OrderNotFoundException;
import com.shoptech.customer.CustomerService;
import com.shoptech.order.OrderReturnRequest;
import com.shoptech.order.OrderReturnResponse;
import com.shoptech.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderRestController {
    @Autowired private OrderService orderService;
    @Autowired private CustomerService customerService;
    @Autowired private ControllerHelper controllerHelper;

    @PostMapping("/orders/return")
    public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderReturnRequest returnRequest,
                                                      HttpServletRequest servletRequest){
        Customer customer = null;
        try{
            customer = controllerHelper.getAuthenticatedCustomer(servletRequest);
        }catch (CustomerNotFoundException ex){
            return new ResponseEntity<>("Authentication required", HttpStatus.BAD_REQUEST);
        }

        try {
            orderService.setOrderReturnRequested(returnRequest, customer);
        } catch (OrderNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()), HttpStatus.OK);
    }
}
