package com.shoptech.order;

import com.shoptech.checkout.CheckoutInfo;
import com.shoptech.common.entity.Address;
import com.shoptech.common.entity.CartItem;
import com.shoptech.common.entity.Customer;
import com.shoptech.common.entity.order.*;
import com.shoptech.common.entity.product.Product;
import com.shoptech.common.exception.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    public static final int ORDERS_PER_PAGE = 5;

    @Autowired private OrderRepository repo;

    public Order createOrder(Customer customer, Address address,
                             List<CartItem> cartItems, PaymentMethod paymentMethod, CheckoutInfo checkoutInfo){

        Order newOrder = new Order();
        if(paymentMethod.equals(PaymentMethod.VNPAY) || paymentMethod.equals(PaymentMethod.PAYPAL)){
            newOrder.setOrderStatus(OrderStatus.PAID);
        }else{
            newOrder.setOrderStatus(OrderStatus.NEW);
        }
        newOrder.setCustomer(customer);
        newOrder.setProductCost(checkoutInfo.getProductCost());
        newOrder.setSubtotal(checkoutInfo.getProductTotal());
        newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
        newOrder.setTax(0.0f);
        newOrder.setTotal(checkoutInfo.getPaymentTotal());
        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setDeliverDays(checkoutInfo.getDeliverDays());
        newOrder.setDeliverDate(checkoutInfo.getDeliverDate());

        if(address == null) newOrder.copyAddressFromCustomer();
        else newOrder.copyShippingAddress(address);

        Set<OrderDetail> orderDetails = newOrder.getOrderDetails();
        for(CartItem cartItem : cartItems){
            Product product = cartItem.getProduct();

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(newOrder);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(product.getDiscountPrice());
            orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
            orderDetail.setSubtotal(cartItem.getSubtotal());
            orderDetail.setShippingCost(cartItem.getShippingCost());

            orderDetails.add(orderDetail);
        }

        OrderTrack track = new OrderTrack();
        track.setOrder(newOrder);
        track.setOrderStatus(OrderStatus.NEW);
        track.setNotes(OrderStatus.NEW.defaultDescription());
        track.setUpdatedTime(new Date());

        newOrder.getOrderTracks().add(track);

        return repo.save(newOrder);
    }

    public Page<Order> listForCustomerByPage(Customer customer, int pageNum,
                                             String sortField, String sortDir, String keyword) {

        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

        if (keyword != null) {
            return repo.findAll(keyword, customer.getId(), pageable);
        }

        return repo.findAll(customer.getId(), pageable);

    }

    public Order getOrder(Long id, Customer customer) {
        return repo.findByIdAndCustomer(id, customer);
    }

    public void setOrderReturnRequested(OrderReturnRequest request, Customer customer) throws OrderNotFoundException {
        Order order = repo.findByIdAndCustomer(request.getOrderId(), customer);
        if(order == null){
            throw new OrderNotFoundException("Order ID " + request.getOrderId() + " not found");
        }

        if(order.isReturnRequested()) return;

        OrderTrack orderTrack = new OrderTrack();
        orderTrack.setOrder(order);
        orderTrack.setUpdatedTime(new Date());
        orderTrack.setOrderStatus(OrderStatus.RETURN_REQUESTED);

        String notes = "Reason: " + request.getReason();
        if(!"".equals(request.getReason())) notes += ". " + request.getNote();
        orderTrack.setNotes(notes);

        order.getOrderTracks().add(orderTrack);
        order.setOrderStatus(OrderStatus.RETURN_REQUESTED);

        repo.save(order);
    }
}
