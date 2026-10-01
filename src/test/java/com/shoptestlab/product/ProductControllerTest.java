package com.shoptestlab.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    @Mock ProductRepository repo;
    @InjectMocks ProductController controller;

    @Test
    void getProduct_returnsProduct() {
        Product p = new Product("Keyboard", "desc", BigDecimal.TEN, 3, "Keyboards");
        p.setId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(p));

        assertEquals("Keyboard", controller.get(1L).getName());
    }

    @Test
    void getProduct_throwsWhenMissing() {
        when(repo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> controller.get(999L));
    }
}
