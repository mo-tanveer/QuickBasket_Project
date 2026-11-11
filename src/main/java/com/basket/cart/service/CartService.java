package com.basket.cart.service;

import com.basket.auth.entity.User;
import com.basket.cart.dto.*;
import com.basket.cart.entity.CartItem;
import com.basket.cart.repository.CartItemRepository;
import com.basket.common.exception.ResourceNotFoundException;
import com.basket.product.entity.Product;
import com.basket.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;

    public CartService(CartItemRepository cartRepo, ProductRepository productRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
    }

    public CartResponse getCart(User user) {
        List<CartItemResponse> items = cartRepo.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();

        int totalItems = items.stream().mapToInt(CartItemResponse::quantity).sum();
        BigDecimal total = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(items, totalItems, total);
    }

    @Transactional
    public CartResponse addToCart(User user, AddToCartRequest req) {
        Product product = productRepo.findById(req.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItem item = cartRepo.findByUserIdAndProductId(user.getId(), product.getId())
                .orElseGet(() -> {
                    CartItem c = new CartItem();
                    c.setUser(user);
                    c.setProduct(product);
                    c.setQuantity(0);
                    return c;
                });

        int newQty = item.getQuantity() + req.quantity();
        checkStock(product, newQty);
        item.setQuantity(newQty);
        cartRepo.save(item);

        return getCart(user);
    }

    @Transactional
    public CartResponse updateQuantity(User user, Long itemId, int quantity) {
        CartItem item = findOwnedItem(user, itemId);
        checkStock(item.getProduct(), quantity);
        item.setQuantity(quantity);
        cartRepo.save(item);
        return getCart(user);
    }

    @Transactional
    public CartResponse removeItem(User user, Long itemId) {
        cartRepo.delete(findOwnedItem(user, itemId));
        return getCart(user);
    }

    @Transactional
    public void clearCart(User user) {
        cartRepo.deleteByUserId(user.getId());
    }

    // ---------- helpers ----------

    private CartItem findOwnedItem(User user, Long itemId) {
        CartItem item = cartRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        // Security: dusre user ka item na chhu sake
        if (!item.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Cart item not found");
        }
        return item;
    }

    private void checkStock(Product product, int qty) {
        if (qty > product.getStock()) {
            throw new RuntimeException("Only " + product.getStock() + " items in stock");
        }
    }

    private CartItemResponse toResponse(CartItem c) {
        Product p = c.getProduct();
        BigDecimal subtotal = p.getPrice().multiply(BigDecimal.valueOf(c.getQuantity()));
        return new CartItemResponse(c.getId(), p.getId(), p.getName(),
                p.getImageUrl(), p.getPrice(), c.getQuantity(), subtotal);
    }
}