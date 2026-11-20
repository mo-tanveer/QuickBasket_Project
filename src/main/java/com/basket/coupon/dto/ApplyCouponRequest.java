package com.basket.coupon.dto;

import jakarta.validation.constraints.NotBlank;

public record ApplyCouponRequest(
        @NotBlank(message = "coupoun code is required") String code
) {
}
