package com.shoptestlab.product;
import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product,Long>{ Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable); }
