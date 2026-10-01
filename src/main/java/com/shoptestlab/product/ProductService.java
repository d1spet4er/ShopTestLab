package com.shoptestlab.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repo;

    @Transactional(readOnly = true)
    public Page<ProductDtos.ProductResponse> list(
            String category,
            String name,
            Pageable pageable
    ) {
        boolean hasCategory = category != null && !category.isBlank();
        boolean hasName = name != null && !name.isBlank();

        Page<Product> products;

        if (hasCategory && hasName) {
            products = repo.findByCategoryIgnoreCaseAndNameContainingIgnoreCase(
                    category.trim(), name.trim(), pageable
            );
        } else if (hasCategory) {
            products = repo.findByCategoryIgnoreCase(category.trim(), pageable);
        } else if (hasName) {
            products = repo.findByNameContainingIgnoreCase(name.trim(), pageable);
        } else {
            products = repo.findAll(pageable);
        }

        return products.map(ProductDtos.ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public ProductDtos.ProductResponse get(Long id) {
        return ProductDtos.ProductResponse.from(findEntity(id));
    }

    @Transactional
    public ProductDtos.ProductResponse create(ProductDtos.ProductRequest request) {
        Product product = new Product(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.category()
        );

        return ProductDtos.ProductResponse.from(repo.save(product));
    }

    @Transactional
    public ProductDtos.ProductResponse update(Long id, ProductDtos.ProductRequest request) {
        Product product = findEntity(id);

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(request.category());

        return ProductDtos.ProductResponse.from(repo.save(product));
    }

    @Transactional
    public void delete(Long id) {
        repo.delete(findEntity(id));
    }

    private Product findEntity(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
