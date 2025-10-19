package io.mmujcinovic.vaudoiseassurances.records.requests;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompanyReqRecord(
        @JsonUnwrapped
        @NotNull
        @Valid
        ClientReqRecord clientRequestRecord, // Common fields
        @NotBlank
        String companyIdentifier // Identifier
) implements ClientReqInterfaceRecord { }
