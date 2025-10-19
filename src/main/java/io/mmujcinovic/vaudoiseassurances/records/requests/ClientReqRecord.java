package io.mmujcinovic.vaudoiseassurances.records.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClientReqRecord(
        @NotBlank
        String name,
        @NotBlank
        @Pattern(regexp="^\\+?[0-9 ]+$")
        String phone,
        @NotBlank
        @Email
        String email
) { }
