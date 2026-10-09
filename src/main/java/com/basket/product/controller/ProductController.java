package com.basket.product.controller;

import com.basket.category.entity.Category;
import com.basket.product.entity.Product;
import com.basket.product.service.ProductService;
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
    public Product createProduct(@RequestBody Product product)
    {

        System.out.println("prodcuct name:" + product.getName());

        System.out.println("category object " + product.getCategory());

        if(product.getCategory() != null)
        {
            System.out.println("Category Id : " + product.getCategory().getId());
        }
        return productService.createProduct(product);
    }

    @GetMapping
    public List<Product>getAllProducts(){
        return productService.getAllProducts();
    }

    @GetMapping({"/{id}"})
    public Product getProductById(@PathVariable Long id)
    {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public Product updateProductById(@PathVariable Long id, @RequestBody Product product)
    {
        return productService.updateProductById(id,product);
    }

    @DeleteMapping("/{id}")
    public String deleteProductById(@PathVariable Long id)
    {
        productService.deleteUpdateById(id);
        return "Product deleted successfully";
    }
}
