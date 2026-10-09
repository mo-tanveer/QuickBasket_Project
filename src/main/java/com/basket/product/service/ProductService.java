package com.basket.product.service;

import com.basket.category.entity.Category;
import com.basket.category.repository.CategoryRepository;
import com.basket.product.entity.Product;
import com.basket.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product createProduct(Product product) {

        if (product.getCategory() == null ||
                product.getCategory().getId() == null) {
            throw new RuntimeException("Category ID is required");
        }

        Long categoryId = product.getCategory().getId();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException("Category not found: " + categoryId));

        product.setCategory(category);

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id)
    {
        return productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Porduct Not Found with id ! : " + id));

    }

    public Product updateProductById(Long id,Product updateProduct)
    {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Product is not found with this id " + id));

        existingProduct.setName(updateProduct.getName());
        existingProduct.setDescription(updateProduct.getDescription());
        existingProduct.setPrice(updateProduct.getPrice());
        existingProduct.setStock(updateProduct.getStock());

        return productRepository.save(existingProduct);
    }

    public void  deleteUpdateById(Long id)
    {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Product Not Found "));

        productRepository.delete(product);
    }
}