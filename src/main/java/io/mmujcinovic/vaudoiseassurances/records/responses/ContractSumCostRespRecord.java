package io.mmujcinovic.vaudoiseassurances.records.responses;

import java.math.BigDecimal;

public record ContractSumCostRespRecord(
        Long clientId,
        BigDecimal sumCost
) { }
