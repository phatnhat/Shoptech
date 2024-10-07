package com.shoptech.checkout.controller;

import com.shoptech.ControllerHelper;
import com.shoptech.Utility;
import com.shoptech.address.AddressService;
import com.shoptech.checkout.CheckoutInfo;
import com.shoptech.checkout.CheckoutService;
import com.shoptech.checkout.paypal.PaypalApiException;
import com.shoptech.checkout.paypal.PaypalService;
import com.shoptech.checkout.vnpay.VNPAYService;
import com.shoptech.common.entity.Address;
import com.shoptech.common.entity.CartItem;
import com.shoptech.common.entity.Customer;
import com.shoptech.common.entity.ShippingRate;
import com.shoptech.common.entity.order.Order;
import com.shoptech.common.entity.order.PaymentMethod;
import com.shoptech.customer.CustomerService;
import com.shoptech.order.OrderService;
import com.shoptech.setting.CurrencySettingBag;
import com.shoptech.setting.EmailSettingBag;
import com.shoptech.setting.PaymentSettingBag;
import com.shoptech.setting.SettingService;
import com.shoptech.shipping.ShippingRateService;
import com.shoptech.shoppingcart.ShoppingCartService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class CheckoutController {
    @Autowired private CheckoutService checkoutService;
    @Autowired private CustomerService customerService;
    @Autowired private AddressService addressService;
    @Autowired private ShippingRateService shippingRateService;
    @Autowired private ShoppingCartService shoppingCartService;
    @Autowired private OrderService orderService;
    @Autowired private SettingService settingService;
    @Autowired private PaypalService paypalService;
    @Autowired private VNPAYService vnPayService;
    @Autowired private ControllerHelper controllerHelper;

    @GetMapping("/checkout")
    public String viewCheckoutPage(Model model, HttpServletRequest request){
        Customer customer = controllerHelper.getAuthenticatedCustomer(request);

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;
        if(defaultAddress != null){
            model.addAttribute("shippingAddress", defaultAddress.toString());
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        }else{
            model.addAttribute("shippingAddress", customer.getAddress());
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        if(shippingRate == null) return "redirect:/cart";

        List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        String currencyCode = settingService.getCurrencyCode();
        PaymentSettingBag paymentSettings = settingService.getPaymentSettings();
        String paypalClientId = paymentSettings.getPaypalClientID();

        model.addAttribute("paypalClientId", paypalClientId);
        model.addAttribute("currencyCode", currencyCode);
        model.addAttribute("customer", customer);
        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems", cartItems);

        return "checkout/checkout";
    }

    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        String paymentType = request.getParameter("paymentMethod") == null ? "VNPAY" : request.getParameter("paymentMethod");
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);

        Customer customer = controllerHelper.getAuthenticatedCustomer(request);

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;
        if(defaultAddress != null){
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        }else{
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        Order createdOrder = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
        shoppingCartService.deleteByCustomer(customer);
        sendOrderConfirmationEmail(request, createdOrder);

        return "checkout/order_completed";
    }

    private void sendOrderConfirmationEmail(HttpServletRequest request, Order order) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
        mailSender.setDefaultEncoding("utf-8");

        String toAddress = order.getCustomer().getEmail();
        String subject = emailSettings.getOrderConfirmationSubject();
        String content = emailSettings.getOrderConfirmationContent();

        subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss E, dd MM yyyy");
        String orderTime = dateFormat.format(order.getCreatedAt());

        CurrencySettingBag currencySettings = settingService.getCurrencySettings();
        String totalAmount = Utility.formatCurrency(order.getTotal(), currencySettings);
        String checkOrderURL = Utility.getSiteURL(request) + "/orders";

        content = content.replace("[[name]]", order.getCustomer().getFullName());
        content = content.replace("[[orderId]]", String.valueOf(order.getId()));
        content = content.replace("[[orderTime]]", orderTime);
        content = content.replace("[[shippingAddress]]", order.getShippingAddress());
        content = content.replace("[[total]]", totalAmount);
        content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());
        content = content.replace("[[orderLink]]", checkOrderURL);

        helper.setText(content, true);
        mailSender.send(message);
    }

    @PostMapping("/process_paypal_order")
    public String processPaypalOrder(HttpServletRequest request, Model model) throws MessagingException, UnsupportedEncodingException {
        String orderId = request.getParameter("orderId");
        String pageTitle = null;
        String message = null;

        try {
            if(paypalService.validateOrder(orderId)){
                return placeOrder(request);
            }else{
                pageTitle = "Checkout Failure";
                message = "ERROR: Transaction could not be completed because order information is invalid";
            }
        } catch (PaypalApiException e) {
            message = "ERROR: Transaction failed due to error: " + e.getMessage();
        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("message", message);

        return "message";
    }

    // vnpay
    @PostMapping("/submitOrder")
    public String submitOrder(@RequestParam("orderTotal") float orderTotal, HttpServletRequest request){
        String currencyCode = settingService.getCurrencyCode();
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(request, orderTotal, currencyCode, baseUrl);
        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/vnpay-payment-return")
    public String paymentCompleted(HttpServletRequest request, Model model) throws MessagingException, UnsupportedEncodingException {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        String pageTitle = null;
        String message = null;

        if(paymentStatus == 1) {
            return placeOrder(request);
        } else if(paymentStatus == 0){
            return "redirect:/checkout";
        }else{
            pageTitle = "Checkout Failure";
            message = "ERROR: Transaction could not be completed because order information is invalid";
        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("message", message);

        return "message";
    }
}
