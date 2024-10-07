package com.shoptech.checkout.paypal;

import com.shoptech.setting.PaymentSettingBag;
import com.shoptech.setting.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class PaypalService {
    private static final String GET_ORDER_API = "/v2/checkout/orders/";

    @Autowired private SettingService settingService;
    @Autowired private RestTemplate restTemplate;

    public boolean validateOrder(String orderId) throws PaypalApiException {
        PaypalOrderResponse orderResponse = getOrderDetails(orderId);
        return orderResponse.validate(orderId);
    }

    private PaypalOrderResponse getOrderDetails(String orderId) throws PaypalApiException {
        ResponseEntity<PaypalOrderResponse> response = makeRequest(orderId);
        HttpStatus statusCode = (HttpStatus) response.getStatusCode();

        if(!statusCode.equals(HttpStatus.OK)){
            throwExceptionForNonOKResponse(statusCode);
        }
        return response.getBody();
    }

    private ResponseEntity<PaypalOrderResponse> makeRequest(String orderId) {
        PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
        String baseURL = paymentSettings.getVnpayPayUrl();
        String requestURL = baseURL + GET_ORDER_API + orderId;
        String clientId = paymentSettings.getPaypalClientID();
        String clientSecret = paymentSettings.getPaypalClientSecret();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en_US");
        headers.setBasicAuth(clientId, clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        return restTemplate.exchange(requestURL, HttpMethod.GET, request, PaypalOrderResponse.class);
    }

    private void throwExceptionForNonOKResponse(HttpStatus statusCode) throws PaypalApiException {
        String message = null;

        switch (statusCode){
            case NOT_FOUND:
                message = "Order ID not found";
            case BAD_REQUEST:
                message = "Bad Request to Paypal Checkout API";
            case INTERNAL_SERVER_ERROR:
                message = "Paypal server error";
            default:
                message = "Paypal returned non-OK status code";
        }
        throw new PaypalApiException(message);
    }
}
