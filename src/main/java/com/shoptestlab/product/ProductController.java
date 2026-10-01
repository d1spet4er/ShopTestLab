package com.shoptestlab.product;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.data.domain.*; import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/products") @RequiredArgsConstructor
public class ProductController {
 private final ProductRepository repo;
 @GetMapping public Page<Product> list(@RequestParam(required=false) String category,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size){
  Pageable p=PageRequest.of(page,Math.min(size,50),Sort.by("id").descending()); return category==null?repo.findAll(p):repo.findByCategoryIgnoreCase(category,p);
 }
 @GetMapping("/{id}") public Product get(@PathVariable Long id){return repo.findById(id).orElseThrow(()->new ProductNotFoundException(id));}
 @PostMapping @PreAuthorize("hasRole('ADMIN')") public ResponseEntity<Product> create(@Valid @RequestBody Product product){product.setId(null);return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(product));}
 @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public Product update(@PathVariable Long id,@Valid @RequestBody Product input){Product p=get(id);p.setName(input.getName());p.setDescription(input.getDescription());p.setPrice(input.getPrice());p.setStock(input.getStock());p.setCategory(input.getCategory());return repo.save(p);}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){repo.delete(get(id));}
}
