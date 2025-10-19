package io.mmujcinovic.vaudoiseassurances.records.responses;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(CompanyRespRecord.class),
        @JsonSubTypes.Type(PersonRespRecord.class)
})
public sealed interface ClientRespInterfaceRecord permits CompanyRespRecord, PersonRespRecord { }
