package io.mmujcinovic.vaudoiseassurances.records.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClientReqRecord(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^\\+?[0-9]{10}$",
                message = "Phone number must contain exactly 10 digits, with an optional leading +")
        String phone,
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) { }
