package com.basket.cart.dto;

import jakarta.validation.constraints.Min;

public record UpdateQuantityRequest(
        @Min(value = 1, message = "quantity must be at least 1") int quantity

) {
}
