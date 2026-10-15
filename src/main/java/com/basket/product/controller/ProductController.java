package com.basket.product.controller;

import com.basket.category.entity.Category;
import com.basket.product.entity.Product;
import com.basket.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService)
    {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product)
    {

        Product savedProduct = productService.createProduct(product);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedProduct);
    }

    @GetMapping
    public List<Product>getAllProducts(){
        return productService.getAllProducts();
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<Product> getProductById(@PathVariable Long id)
    {
        Product product = productService.getProductById(id);
        return ResponseEntity
                .ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProductById(
            @PathVariable Long id,@Valid @RequestBody Product product)
    {
        Product updateProduct = productService.updateProductById(id,product);
        return ResponseEntity
                .ok(updateProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id)
    {
        productService.deleteUpdateById(id);
        return ResponseEntity
                .noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchByName(@RequestParam String name){

        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity
                .ok(products);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product> > getProductsByCategoryId(@PathVariable Long categoryId)
    {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        return ResponseEntity
                .ok(products);
    }

    //Pagination
    @GetMapping("/page")
    public ResponseEntity<Page<Product>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Page<Product> products = productService.getProducts(
                page, size, sortBy, direction);
        return ResponseEntity
                .ok(products);
    }

}
