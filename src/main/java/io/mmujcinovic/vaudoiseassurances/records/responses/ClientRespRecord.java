package io.mmujcinovic.vaudoiseassurances.records.responses;

public record ClientRespRecord(
        Long id,
        String name,
        String phone,
        String email,
        boolean active
) { }
