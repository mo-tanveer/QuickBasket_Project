package com.basket.coupon.dto;

import java.math.BigDecimal;

public record ApplyCouponResponse(
        String code,
        BigDecimal cartTotal,
        BigDecimal  discount,
        BigDecimal finalTotal
) {
}
