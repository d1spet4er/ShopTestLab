package com.shoptestlab.cart;

import com.shoptestlab.product.Product;
import com.shoptestlab.product.ProductRepository;
import com.shoptestlab.product.ProductNotFoundException;
import com.shoptestlab.user.User;
import com.shoptestlab.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItems;
    private final ProductRepository products;
    private final UserRepository users;

    @Transactional(readOnly = true)
    public CartDtos.CartResponse getCart(String email) {
        List<CartDtos.CartItemResponse> items = cartItems
                .findByUserEmailOrderByIdAsc(email)
                .stream()
                .map(CartDtos.CartItemResponse::from)
                .toList();

        BigDecimal total = items.stream()
                .map(CartDtos.CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDtos.CartResponse(items, total);
    }

    @Transactional
    public CartDtos.CartResponse addItem(String email, CartDtos.AddCartItemRequest request) {
        if (request.productId() == null || request.quantity() < 1) {
            throw new IllegalArgumentException("Product id and positive quantity are required");
        }

        User user = users.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product product = products.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(request.productId()));

        if (request.quantity() > product.getStock()) {
            throw new IllegalArgumentException("Not enough product stock");
        }

        CartItem item = cartItems
                .findByUserEmailAndProductId(email, product.getId())
                .orElseGet(() -> new CartItem(user, product, 0));

        int newQuantity = item.getQuantity() + request.quantity();

        if (newQuantity > product.getStock()) {
            throw new IllegalArgumentException("Not enough product stock");
        }

        item.setQuantity(newQuantity);
        cartItems.save(item);

        return getCart(email);
    }

    @Transactional
    public void removeItem(String email, Long itemId) {
        CartItem item = cartItems.findByIdAndUserEmail(itemId, email)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        cartItems.delete(item);
    }

    @Transactional
    public void clear(String email) {
        cartItems.findByUserEmailOrderByIdAsc(email)
                .forEach(cartItems::delete);
    }
}
