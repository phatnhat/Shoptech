package com.shoptech.shoppingcart;

import com.shoptech.common.entity.CartItem;
import com.shoptech.common.entity.Customer;
import com.shoptech.common.entity.product.Product;
import com.shoptech.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class ShoppingCartService {
    @Autowired
    private CartItemRepository repo;

    @Autowired
    private ProductRepository productRepository;

    public Integer addProduct(Long productId, Integer quantity, Customer customer) throws ShoppingCartException {
        Integer updatedQuantity = quantity;
        Product product = new Product(productId);

        CartItem cartItem = repo.findByCustomerAndProduct(customer, product);

        if(cartItem != null){
            updatedQuantity = cartItem.getQuantity() + quantity;
            if(updatedQuantity > 5){
                throw new ShoppingCartException("Could not add more " + quantity + " item(s) "
                    + "because there's already " + cartItem.getQuantity() + " item(s) in your shopping cart. " +
                        "Maximum allowed quantity is 5.");
            }
        }else{
            cartItem = new CartItem();
            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
        }

        cartItem.setQuantity(updatedQuantity);
        repo.save(cartItem);

        return updatedQuantity;
    }

    public List<CartItem> listCartItems(Customer customer){
        return repo.findByCustomerOrderByUpdatedAtDesc(customer);
    }

    public float updateQuantity(Long productId, Integer quantity, Customer customer){
        //repo.updateQuantity(quantity, customer.getId(), productId);
        Product product = productRepository.findById(productId).get();
        CartItem cartItem = repo.findByCustomerAndProduct(customer, product);
        cartItem.setQuantity(quantity);
        repo.saveAndFlush(cartItem);
        return product.getDiscountPrice() * quantity;
    }

    public void removeProduct(Long productId, Customer customer){
        repo.deleteByCustomerAndProduct(customer.getId(), productId);
    }

    public void deleteByCustomer(Customer customer){
        repo.deleteByCustomer(customer.getId());
    }
}
