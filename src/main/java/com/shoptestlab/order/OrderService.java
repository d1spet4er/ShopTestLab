package com.shoptestlab.order;

import com.shoptestlab.cart.CartItem;
import com.shoptestlab.cart.CartItemRepository;
import com.shoptestlab.product.Product;
import com.shoptestlab.product.ProductRepository;
import com.shoptestlab.user.User;
import com.shoptestlab.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orders;
    private final CartItemRepository cartItems;
    private final ProductRepository products;
    private final UserRepository users;

    @Transactional
    public OrderDtos.OrderResponse createFromCart(String email) {
        User user = users.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<CartItem> items = cartItems.findByUserEmailOrderByIdAsc(email);

        if (items.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Order order = new Order(user);
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : items) {
            Product product = products.findWithLockById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            if (cartItem.getQuantity() > product.getStock()) {
                throw new IllegalArgumentException(
                        "Not enough stock for product: " + product.getName()
                );
            }

            product.setStock(product.getStock() - cartItem.getQuantity());

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            total = total.add(subtotal);

            order.addItem(new OrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    cartItem.getQuantity()
            ));
        }

        order.setTotal(total);
        Order saved = orders.save(order);
        cartItems.deleteAll(items);

        return OrderDtos.OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderDtos.OrderResponse> list(String email) {
        return orders.findByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(OrderDtos.OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDtos.OrderResponse get(String email, Long id) {
        return OrderDtos.OrderResponse.from(
                orders.findByIdAndUserEmail(id, email)
                        .orElseThrow(() -> new IllegalArgumentException("Order not found"))
        );
    }
}
