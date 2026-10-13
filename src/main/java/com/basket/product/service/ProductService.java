package com.basket.product.service;

import com.basket.category.entity.Category;
import com.basket.category.repository.CategoryRepository;
import com.basket.product.entity.Product;
import com.basket.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Porduct Not Found with id ! : " + id));

    }

    public Product updateProductById(Long id, Product updateProduct) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product is not found with this id " + id));

        existingProduct.setName(updateProduct.getName());
        existingProduct.setDescription(updateProduct.getDescription());
        existingProduct.setPrice(updateProduct.getPrice());
        existingProduct.setStock(updateProduct.getStock());

        if(updateProduct.getCategory() != null && updateProduct.getCategory().getId() != null)
        {
            Long categoryId = updateProduct.getCategory().getId();

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(()-> new RuntimeException("category is not found"));
            existingProduct.setCategory(category);
        }
        return productRepository.save(existingProduct);
    }

    public void deleteUpdateById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product Not Found "));

        productRepository.delete(product);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }


    public Page<Product> getProducts(
            int page,
            int size,
            String sortBy,
            String direction) {


        if (page < 0) {
            throw new IllegalArgumentException("Page number can not be negative");
        }

        if (size < 1 || size > 100)
        {
            throw new IllegalArgumentException(
                    "Page size must between 1 and 100"
            );
        }

        if (!sortBy.equals("id")
                && !sortBy.equals("name")
                && !sortBy.equals("price")
                && !sortBy.equals("stock")) {
            throw new IllegalArgumentException(
                    "Invalid sort field");
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {
            throw new RuntimeException(
                    "Direction must be asc and desc");
        }

            Sort sort = direction.equalsIgnoreCase("desc")
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();


            Pageable pageable = PageRequest.of(page, size, sort);

            return productRepository.findAll(pageable);
        }

    }