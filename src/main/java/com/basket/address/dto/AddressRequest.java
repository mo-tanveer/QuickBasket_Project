package com.basket.address.dto;

import com.basket.address.entity.AddressType;
import jakarta.validation.constraints.*;

public record AddressRequest(
@NotNull(message = "label is required (HOME, WORK or OTHER)") AddressType label,
@NotBlank(message = "fullName is required") String fullName,
@NotBlank @Pattern(regexp = "\\d{10}", message = "phone must be 10 digits") String phone,
@NotBlank(message = "line1 is required") String line1,
String line2,
@NotBlank(message = "city is required") String city,
@NotBlank(message = "state is required") String state,
@NotBlank @Pattern(regexp = "\\d{6}", message = "pincode must be 6 digits") String pincode,
boolean setAsDefault
) {}