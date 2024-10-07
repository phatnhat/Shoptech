package com.shoptech.admin.order.controller;

import com.shoptech.admin.order.OrderService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderRestController {
    @Autowired private OrderService orderService;

    @PostMapping("/orders_shipping/update/{id}/{status}")
    public Response updateOrderStatus(@PathVariable("id") Long orderId, @PathVariable String status){
        orderService.updateStatus(orderId, status);
        return new Response(orderId, status);
    }
}

@Getter
@Setter
@AllArgsConstructor
class Response{
    private Long orderId;
    private String status;
}
