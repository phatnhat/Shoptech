package com.shoptech.admin.order;

import com.shoptech.common.entity.order.Order;
import com.shoptech.common.entity.order.OrderDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
public class OrderDetailRepositoryTests {
    @Autowired private OrderDetailRepository repo;

    @Test
    public void testFindWithCategoryAndTimeBetween() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormat.parse("2021-08-01");
        Date endTime = dateFormat.parse("2021-08-31");

        List<OrderDetail> listOrderDetails = repo.findWithCategoryAndTimeBetween(startTime, endTime);

        assertThat(listOrderDetails.size()).isGreaterThan(0);

        for(OrderDetail detail : listOrderDetails){
            System.out.printf("%-30s | %d | %10.2f | %10.2f | %10.2f \n",
                    detail.getProduct().getCategory().getName(), detail.getQuantity(),
                    detail.getProductCost(), detail.getShippingCost(), detail.getSubtotal());
        }
    }

    @Test
    public void testFindWithProductAndTimeBetween() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormat.parse("2021-08-01");
        Date endTime = dateFormat.parse("2021-08-31");

        List<OrderDetail> listOrderDetails = repo.findWithProductAndTimeBetween(startTime, endTime);

        assertThat(listOrderDetails.size()).isGreaterThan(0);

        for(OrderDetail detail : listOrderDetails){
            System.out.printf("%-75s | %d | %10.2f | %10.2f | %10.2f \n",
                    detail.getProduct().getShortName(), detail.getQuantity(),
                    detail.getProductCost(), detail.getShippingCost(), detail.getSubtotal());
        }
    }
}
