package io.mmujcinovic.vaudoiseassurances.records.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ContractCostReqRecord(
        @NotNull
        @PositiveOrZero
        BigDecimal costAmount
) { }
