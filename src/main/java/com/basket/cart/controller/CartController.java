package com.basket.cart.controller;

import com.basket.auth.entity.User;
import com.basket.cart.dto.*;
import com.basket.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart(@AuthenticationPrincipal User user) {
        return cartService.getCart(user);
    }

    @PostMapping("/items")
    public CartResponse add(@AuthenticationPrincipal User user,
                            @Valid @RequestBody AddToCartRequest req) {
        return cartService.addToCart(user, req);
    }

    @PutMapping("/items/{itemId}")
    public CartResponse update(@AuthenticationPrincipal User user,
                               @PathVariable Long itemId,
                               @Valid @RequestBody UpdateQuantityRequest req) {
        return cartService.updateQuantity(user, itemId, req.quantity());
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse remove(@AuthenticationPrincipal User user,
                               @PathVariable Long itemId) {
        return cartService.removeItem(user, itemId);
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }
}