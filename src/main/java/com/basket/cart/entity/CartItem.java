package com.basket.cart.entity;

import com.basket.auth.entity.User;
import com.basket.product.entity.Product;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "carts-items", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))

@Data
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;
}
