package com.basket.coupon.controller;

import com.basket.coupon.dto.CreateCouponRequest;
import com.basket.coupon.entity.Coupon;
import com.basket.coupon.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/coupons")
public class AdminCouponController {

    private final CouponService couponService;

    public AdminCouponController(CouponService couponService){
        this.couponService = couponService;
    }

    @PostMapping
    public ResponseEntity<Coupon> create(@Valid @RequestBody CreateCouponRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.create(req));
    }

    @GetMapping
    public List<Coupon> getAll(){
        return couponService.getAll();
    }

    @PatchMapping("/{id}/deactivate")
    public Coupon deactivate(@PathVariable Long id)
    {
        return  couponService.deactivate(id);
    }
}
