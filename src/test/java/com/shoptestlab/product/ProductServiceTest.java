package com.shoptestlab.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository repo;

    private ProductService service() {
        return new ProductService(repo);
    }

    @Test
    void get_returnsDto() {
        Product product = new Product(
                "Keyboard", "desc", BigDecimal.TEN, 3, "Peripherals"
        );
        product.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(product));

        ProductDtos.ProductResponse result = service().get(1L);

        assertEquals(1L, result.id());
        assertEquals("Keyboard", result.name());
        assertEquals(BigDecimal.TEN, result.price());
    }

    @Test
    void create_doesNotExposeEntity() {
        Product saved = new Product(
                "Mouse", "desc", BigDecimal.valueOf(25), 5, "Peripherals"
        );
        saved.setId(2L);

        when(repo.save(any(Product.class))).thenReturn(saved);

        ProductDtos.ProductResponse result = service().create(
                new ProductDtos.ProductRequest(
                        "Mouse", "desc", BigDecimal.valueOf(25), 5, "Peripherals"
                )
        );

        assertEquals(2L, result.id());
        assertEquals("Mouse", result.name());

        verify(repo).save(argThat(product ->
                product.getId() == null
                        && product.getName().equals("Mouse")
        ));
    }

    @Test
    void list_mapsEntitiesToDtos() {
        Product product = new Product(
                "Keyboard", "desc", BigDecimal.TEN, 3, "Peripherals"
        );
        product.setId(1L);

        PageRequest pageable = PageRequest.of(
                0, 10, Sort.by("id").descending()
        );

        when(repo.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(product), pageable, 1));

        var result = service().list(null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Keyboard", result.getContent().getFirst().name());
    }

    @Test
    void get_throwsWhenMissing() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> service().get(999L)
        );
    }
}
