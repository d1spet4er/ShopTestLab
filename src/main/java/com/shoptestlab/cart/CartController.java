package com.shoptestlab.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService service;

    @GetMapping
    public CartDtos.CartResponse get(Authentication authentication) {
        return service.getCart(authentication.getName());
    }

    @PostMapping("/items")
    public CartDtos.CartResponse addItem(
            Authentication authentication,
            @RequestBody CartDtos.AddCartItemRequest request
    ) {
        return service.addItem(authentication.getName(), request);
    }

    @DeleteMapping("/items/{id}")
    public void removeItem(
            Authentication authentication,
            @PathVariable Long id
    ) {
        service.removeItem(authentication.getName(), id);
    }

    @DeleteMapping
    public void clear(Authentication authentication) {
        service.clear(authentication.getName());
    }
}
