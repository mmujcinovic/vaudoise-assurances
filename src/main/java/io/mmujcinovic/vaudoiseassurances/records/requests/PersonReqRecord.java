package io.mmujcinovic.vaudoiseassurances.records.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record PersonReqRecord(
        @NotNull(message = "Client data is required")
        @Valid
        @JsonUnwrapped
        ClientReqRecord clientRequestRecord, // Common fields
        @NotNull(message = "Birthdate is required")
        @JsonFormat(pattern="yyyy-MM-dd")
        @PastOrPresent(message = "Birthdate cannot be in the future")
        LocalDate birthdate
) implements ClientReqInterfaceRecord { }
