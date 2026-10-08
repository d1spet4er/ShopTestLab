package com.shoptestlab.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock ProductService service;

    @InjectMocks ProductController controller;

    @Test
    void getProduct_returnsProduct() {
        ProductDtos.ProductResponse response = new ProductDtos.ProductResponse(
                1L, "Keyboard", "desc", BigDecimal.TEN, 3, "Keyboards"
        );

        when(service.get(1L)).thenReturn(response);

        assertEquals("Keyboard", controller.get(1L).name());
        verify(service).get(1L);
    }

    @Test
    void getProduct_propagatesNotFound() {
        when(service.get(999L)).thenThrow(new ProductNotFoundException(999L));

        assertThrows(
                ProductNotFoundException.class,
                () -> controller.get(999L)
        );
    }
}
