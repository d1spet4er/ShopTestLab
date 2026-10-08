package com.shoptestlab.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class OrderDtos {

    private OrderDtos() {
    }

    public record OrderItemResponse(
            Long productId,
            String productName,
            BigDecimal price,
            int quantity,
            BigDecimal subtotal
    ) {
        static OrderItemResponse from(OrderItem item) {
            return new OrderItemResponse(
                    item.getProductId(),
                    item.getProductName(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }
    }

    public record OrderResponse(
            Long id,
            String status,
            BigDecimal total,
            Instant createdAt,
            List<OrderItemResponse> items
    ) {
        static OrderResponse from(Order order) {
            return new OrderResponse(
                    order.getId(),
                    order.getStatus().name(),
                    order.getTotal(),
                    order.getCreatedAt(),
                    order.getItems().stream()
                            .map(OrderItemResponse::from)
                            .toList()
            );
        }
    }
}
