// AddressResponse.java
package com.basket.address.dto;

import com.basket.address.entity.AddressType;

public record AddressResponse(
        Long id,
        AddressType label,
        String fullName,
        String phone,
        String line1,
        String line2,
        String city,
        String state,
        String pincode,
        boolean defaultAddress
) {}