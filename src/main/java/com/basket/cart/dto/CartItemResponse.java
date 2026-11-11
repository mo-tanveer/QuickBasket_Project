package com.basket.cart.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        Long itemId,
        Long productId,
        String name,
        String imageUrl,
        BigDecimal price,
        int quantity,
        BigDecimal subtotal
) {
}
