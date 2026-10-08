package com.shoptestlab.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserEmailOrderByIdAsc(String email);

    Optional<CartItem> findByIdAndUserEmail(Long id, String email);

    Optional<CartItem> findByUserEmailAndProductId(String email, Long productId);
}
