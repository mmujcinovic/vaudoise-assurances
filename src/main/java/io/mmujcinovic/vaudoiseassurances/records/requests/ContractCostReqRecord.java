package io.mmujcinovic.vaudoiseassurances.records.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ContractCostReqRecord(
        @NotNull(message = "Cost amount is required")
        @PositiveOrZero(message = "Cost amount must be zero or a positive value")
        BigDecimal costAmount
) { }
