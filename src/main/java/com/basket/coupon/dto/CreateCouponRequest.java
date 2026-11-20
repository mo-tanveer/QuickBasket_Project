package com.basket.coupon.dto;

import com.basket.coupon.entity.DiscountType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCouponRequest(
        @NotBlank(message = "code is required") String code,
        @NotNull(message = "discountType is required") DiscountType discountType,
        @NotNull @Positive(message = "discountValue must be greater than 0") BigDecimal discountValue,
        @PositiveOrZero(message = "minOrderAmount cannot be negative") BigDecimal minOrderAmount,
        @Positive(message = "maxDiscount must be greater than 0") BigDecimal maxDiscount,
        @NotNull @Future(message = "expiryDate must be in the future") LocalDate expiryDate
        ) {
}
