package io.mmujcinovic.vaudoiseassurances.exceptions;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final String userMessage;

    public BadRequestException(String devMessage, String userMessage) {
        super(devMessage);
        this.userMessage = userMessage;
    }
}
