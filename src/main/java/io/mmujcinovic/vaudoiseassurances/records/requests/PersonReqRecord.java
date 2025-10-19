package io.mmujcinovic.vaudoiseassurances.records.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record PersonReqRecord(
        @JsonUnwrapped
        @NotNull
        @Valid
        ClientReqRecord clientRequestRecord, // Common fields
        @NotNull
        @JsonFormat(pattern="yyyy-MM-dd")
        @PastOrPresent
        LocalDate birthdate
) implements ClientReqInterfaceRecord { }
