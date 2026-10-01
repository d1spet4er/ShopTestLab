package com.shoptestlab.product;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
@Entity @Getter @Setter @NoArgsConstructor
public class Product {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @NotBlank @Size(max=120) @Column(nullable=false) private String name;
 @Size(max=1000) private String description;
 @NotNull @DecimalMin("0.01") @Column(nullable=false,precision=12,scale=2) private BigDecimal price;
 @Min(0) @Column(nullable=false) private int stock;
 @NotBlank @Size(max=80) @Column(nullable=false) private String category;
 public Product(String name,String description,BigDecimal price,int stock,String category){this.name=name;this.description=description;this.price=price;this.stock=stock;this.category=category;}
}
