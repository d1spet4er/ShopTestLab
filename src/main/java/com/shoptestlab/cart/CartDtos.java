package com.shoptestlab.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public final class CartDtos {
    private CartDtos() {}

    public record AddCartItemRequest(
            @NotNull Long productId,
            @Min(1) int quantity
    ) {}

    public record CartItemResponse(
            Long id, Long productId, String productName,
            BigDecimal price, int quantity, BigDecimal subtotal
    ) {
        static CartItemResponse from(CartItem item) {
            BigDecimal subtotal = item.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            return new CartItemResponse(item.getId(), item.getProduct().getId(),
                    item.getProduct().getName(), item.getProduct().getPrice(),
                    item.getQuantity(), subtotal);
        }
    }

    public record CartResponse(
            java.util.List<CartItemResponse> items,
            BigDecimal total
    ) {}
}
