package io.mmujcinovic.vaudoiseassurances.records.responses;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.time.LocalDate;

public record PersonRespRecord(
        @JsonUnwrapped
        ClientRespRecord clientRecord,
        LocalDate birthdate
) implements ClientRespInterfaceRecord { }
