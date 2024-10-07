package com.shoptech.admin.order;

import com.shoptech.common.entity.*;
import com.shoptech.common.entity.order.*;
import com.shoptech.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderRepositoryTests {
    @Autowired
    private OrderRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testAddNewOrder(){
        Customer customer = entityManager.find(Customer.class, 1L);
        Product product = entityManager.find(Product.class, 1L);

        Order mainOrder = new Order();
        mainOrder.setCustomer(customer);
        mainOrder.setFirstName(customer.getFirstName());
        mainOrder.setLastName(customer.getLastName());
        mainOrder.setPhoneNumber(customer.getPhoneNumber());
        mainOrder.setAddressLine1(customer.getAddressLine1());
        mainOrder.setAddressLine2(customer.getAddressLine2());
        mainOrder.setCity(customer.getCity());
        mainOrder.setCountry(customer.getCountry().getName());
        mainOrder.setPostalCode(customer.getPostalCode());
        mainOrder.setState(customer.getState());

        mainOrder.setShippingCost(10);
        mainOrder.setProductCost(product.getCost());
        mainOrder.setTax(0);
        mainOrder.setSubtotal(product.getPrice());
        mainOrder.setTotal(product.getPrice() + 10);

        mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        mainOrder.setOrderStatus(OrderStatus.NEW);
        mainOrder.setDeliverDays(1);
        mainOrder.setDeliverDate(new Date());

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProduct(product);
        orderDetail.setOrder(mainOrder);
        orderDetail.setProductCost(product.getCost());
        orderDetail.setShippingCost(10);
        orderDetail.setQuantity(1);
        orderDetail.setSubtotal(product.getPrice());
        orderDetail.setUnitPrice(product.getPrice());

        mainOrder.getOrderDetails().add(orderDetail);
        Order savedOrder = repo.save(mainOrder);
        assertThat(savedOrder).isNotNull();
    }

    @Test
    public void testUpdateOrderTracks() {
        Long orderId = 1L;
        Order order = repo.findById(orderId).get();

        OrderTrack newTrack = new OrderTrack();
        newTrack.setOrder(order);
        newTrack.setOrderStatus(OrderStatus.NEW);
        newTrack.setUpdatedTime(new Date());
        newTrack.setNotes(OrderStatus.NEW.defaultDescription());

        OrderTrack processingTrack = new OrderTrack();
        processingTrack.setOrder(order);
        processingTrack.setOrderStatus(OrderStatus.PROCESSING);
        processingTrack.setUpdatedTime(new Date());
        processingTrack.setNotes(OrderStatus.PROCESSING.defaultDescription());

        List<OrderTrack> orderTracks = order.getOrderTracks();
        orderTracks.add(newTrack);
        orderTracks.add(processingTrack);

        Order updatedOrder = repo.save(order);

        assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
    }

    @Test
    public void testFindByOrderTimeBetween() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormat.parse("2021-08-01");
        Date endTime = dateFormat.parse("2021-08-31");

        List<Order> listOrders = repo.findByCreatedAtBetween(startTime, endTime);
        assertThat(listOrders.size()).isGreaterThan(0);

        for(Order order : listOrders){
            System.out.printf("%s | %s | %.2f | %.2f | %.2f \n",
                    order.getId(), order.getCreatedAt(), order.getProductCost(),
                    order.getSubtotal(), order.getTotal());
        }
    }
}
