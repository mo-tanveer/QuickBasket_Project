package com.basket.product.entity;

import com.basket.category.entity.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;

    private String description;

    @NotNull(message = "Product price is required")
    @Positive(message = "Product price  must be greater than Zero")
    @Column(nullable = false,precision = 10,scale=2)
    private BigDecimal price;



    @NotNull(message = "Product stock is required")
    @Min(value = 0, message = "Product stock cannot be negative")
    @Column(nullable = false)
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
