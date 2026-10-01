package com.shoptestlab.config;
import com.shoptestlab.product.ProductNotFoundException; import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestControllerAdvice public class ApiExceptionHandler {
 @ExceptionHandler(ProductNotFoundException.class) ResponseEntity<Map<String,Object>> notFound(ProductNotFoundException e){return body(HttpStatus.NOT_FOUND,e.getMessage());}
 @ExceptionHandler({IllegalArgumentException.class,MethodArgumentNotValidException.class}) ResponseEntity<Map<String,Object>> bad(Exception e){return body(HttpStatus.BAD_REQUEST,e instanceof MethodArgumentNotValidException?"Validation failed":e.getMessage());}
 private ResponseEntity<Map<String,Object>> body(HttpStatus s,String m){return ResponseEntity.status(s).body(Map.of("status",s.value(),"error",s.getReasonPhrase(),"message",m,"timestamp",new Date().toString()));}
}
