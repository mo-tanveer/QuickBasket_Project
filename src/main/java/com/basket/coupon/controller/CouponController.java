package com.basket.coupon.controller;

import com.basket.auth.entity.User;
import com.basket.coupon.dto.ApplyCouponRequest;
import com.basket.coupon.dto.ApplyCouponResponse;
import com.basket.coupon.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart/coupon")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public ApplyCouponResponse apply(@AuthenticationPrincipal User user,
                                     @Valid @RequestBody ApplyCouponRequest req) {
        return couponService.apply(user, req.code());
    }
}