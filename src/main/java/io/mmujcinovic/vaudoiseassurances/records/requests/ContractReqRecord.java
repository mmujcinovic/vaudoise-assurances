package io.mmujcinovic.vaudoiseassurances.records.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractReqRecord(
        @NotNull
        @JsonFormat(pattern="yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(pattern="yyyy-MM-dd")
        LocalDate endDate,
        @NotNull
        @PositiveOrZero
        BigDecimal costAmount
) { }
