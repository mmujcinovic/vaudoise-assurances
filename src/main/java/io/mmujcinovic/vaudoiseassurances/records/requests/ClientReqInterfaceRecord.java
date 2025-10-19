package io.mmujcinovic.vaudoiseassurances.records.requests;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(CompanyReqRecord.class),
        @JsonSubTypes.Type(PersonReqRecord.class)
})
public sealed interface ClientReqInterfaceRecord permits CompanyReqRecord, PersonReqRecord { }
