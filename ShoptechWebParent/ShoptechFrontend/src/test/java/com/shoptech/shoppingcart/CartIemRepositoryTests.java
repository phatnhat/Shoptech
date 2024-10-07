package com.shoptech.shoppingcart;

import com.shoptech.common.entity.CartItem;
import com.shoptech.common.entity.Customer;
import com.shoptech.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CartIemRepositoryTests {
    @Autowired
    private CartItemRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testSaveItem(){
        Long customerId = 1L;
        Long productId = 1L;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem cartItem = new CartItem();
        cartItem.setCustomer(customer);
        cartItem.setProduct(product);
        cartItem.setQuantity(11);

        CartItem savedCartItem = repo.save(cartItem);
        assertThat(savedCartItem).isNotNull();
    }

    @Test
    public void testFindByCustomer(){
        Long customerId = 1L;
        Customer customer = entityManager.find(Customer.class, customerId);

        List<CartItem> listItems = repo.findByCustomer(customer);
        listItems.forEach(System.out::println);

        assertThat(listItems).hasSizeGreaterThan(0);
    }

    @Test
    public void testFindByCustomerAndProduct(){
        Long customerId = 1L;
        Long productId = 1L;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem cartItem = repo.findByCustomerAndProduct(customer, product);
        System.out.println("cartItem = " + cartItem);

        assertThat(cartItem).isNotNull();
    }

    @Test
    public void testUpdateQuantity(){
        Long customerId = 1L;
        Long productId = 1L;

        repo.updateQuantity(20, customerId, productId);

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);
        CartItem cartItem = repo.findByCustomerAndProduct(customer, product);

        assertThat(cartItem.getQuantity()).isEqualTo(20);
    }

    @Test
    public void testDeleteByCustomerAndProduct(){
        Long customerId = 1L;
        Long productId = 1L;

        repo.deleteByCustomerAndProduct(customerId, productId);

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);
        CartItem cartItem = repo.findByCustomerAndProduct(customer, product);

        assertThat(cartItem).isNull();
    }
}
