package com.shoptestlab.order;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDtos.OrderResponse create(Authentication authentication) {
        return service.createFromCart(authentication.getName());
    }

    @GetMapping
    public List<OrderDtos.OrderResponse> list(Authentication authentication) {
        return service.list(authentication.getName());
    }

    @GetMapping("/{id}")
    public OrderDtos.OrderResponse get(
            Authentication authentication,
            @PathVariable Long id
    ) {
        return service.get(authentication.getName(), id);
    }
}
