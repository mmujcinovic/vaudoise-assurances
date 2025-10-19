package io.mmujcinovic.vaudoiseassurances.records.responses;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractRespRecord(
        Long id,
        Long clientId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal costAmount
) { }
