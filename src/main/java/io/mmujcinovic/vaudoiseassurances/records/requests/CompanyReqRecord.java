package io.mmujcinovic.vaudoiseassurances.records.requests;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CompanyReqRecord(
        @NotNull(message = "Client data is required")
        @Valid
        @JsonUnwrapped
        ClientReqRecord clientRequestRecord,
        @NotBlank(message = "Company identifier is required")
        @Pattern(
                regexp = "^[A-Za-z0-9][A-Za-z0-9._/-]*$",
                message = "Company identifier may only contain letters, digits, dots, slashes, underscores or dashes")
        String companyIdentifier
) implements ClientReqInterfaceRecord { }
