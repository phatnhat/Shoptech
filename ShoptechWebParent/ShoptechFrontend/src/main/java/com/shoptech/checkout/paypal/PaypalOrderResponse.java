package com.shoptech.checkout.paypal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaypalOrderResponse {
    private String id;
    private String status;

    public boolean validate(String orderId){
        return id.equals(orderId) && status.equals("COMPLETED");
    }
}
