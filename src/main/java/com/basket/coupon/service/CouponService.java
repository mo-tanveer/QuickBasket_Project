package com.basket.coupon.service;

import com.basket.auth.entity.User;
import com.basket.cart.dto.CartResponse;
import com.basket.cart.service.CartService;
import com.basket.common.exception.ResourceNotFoundException;
import com.basket.coupon.dto.ApplyCouponResponse;
import com.basket.coupon.dto.CreateCouponRequest;
import com.basket.coupon.entity.Coupon;
import com.basket.coupon.entity.DiscountType;
import com.basket.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class CouponService {

    private final CouponRepository couponRepo;
    private final CartService cartService;

    public CouponService(CouponRepository couponRepo, CartService cartService){

        this.couponRepo = couponRepo;
        this.cartService = cartService;
    }

    //ADMIN

    public Coupon create(CreateCouponRequest req)
    {
        String code = req.code().trim().toUpperCase();

        if(couponRepo.existsByCodeIgnoreCase(code)){
            throw new RuntimeException("Coupon code already exists");
        }
        if(req.discountType() == DiscountType.PERCENT && req.discountValue().compareTo(BigDecimal.valueOf(100)) > 0){
            throw new RuntimeException("Percent discount can not be more than  100");
        }

        Coupon c = new Coupon();
        c.setCode(code);
        c.setDiscountType(req.discountType());
        c.setDiscountValue(req.discountValue());
        c.setMinOrderAmount(req.minOrderAmount() == null ? BigDecimal.ZERO : req.minOrderAmount());
        c.setMaxDiscount(req.maxDiscount());
        c.setExpiryDate(req.expiryDate());
        c.setActive(true);
        return couponRepo.save(c);
    }

    public List<Coupon> getAll(){
        return couponRepo.findAll();
    }

    public Coupon deactivate(Long id){
        Coupon c = couponRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        c.setActive(false);
        return couponRepo.save(c);
    }



    //-----------------------USER------------------------------

    public ApplyCouponResponse apply(User user, String code){
        CartResponse cart = cartService.getCart(user);
        if(cart.items().isEmpty()){
            throw new RuntimeException("Your cart is empty");
        }

        Coupon coupon = couponRepo.findByCodeIgnoreCase(code.trim())
                .orElseThrow(()-> new RuntimeException("Invalid coupon code"));

        validate(coupon,cart.total());

        BigDecimal discount = calculateDiscount(coupon, cart.total());
        BigDecimal finalTotal = cart.total().subtract(discount);

        return new ApplyCouponResponse(coupon.getCode(), cart.total(), discount, finalTotal);
    }

//    helpers (Order module mein bhi kaam aayenge) --

    public void validate(Coupon coupon, BigDecimal cartTotal) {
        if (!coupon.isActive()) {
            throw new RuntimeException("Coupon is not active");
        }
        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Coupon has expired");
        }
        if (cartTotal.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new RuntimeException(
                    "Minimum order amount for this coupon is ₹" + coupon.getMinOrderAmount());
        }
    }


    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal cartTotal) {
        BigDecimal discount;

        if (coupon.getDiscountType() == DiscountType.PERCENT) {
            discount = cartTotal
                    .multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // max seema laga do (agar set hai)
            if (coupon.getMaxDiscount() != null
                    && discount.compareTo(coupon.getMaxDiscount()) > 0) {
                discount = coupon.getMaxDiscount();
            }
        } else {
            discount = coupon.getDiscountValue();
        }

        // discount kabhi cart total se zyada nahi
        if (discount.compareTo(cartTotal) > 0) {
            discount = cartTotal;
        }
        return discount;
    }
}
