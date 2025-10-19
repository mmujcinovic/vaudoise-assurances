package io.mmujcinovic.vaudoiseassurances.records.responses;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record CompanyRespRecord(
        @JsonUnwrapped
        ClientRespRecord clientRecord,
        String companyIdentifier
) implements ClientRespInterfaceRecord { }
