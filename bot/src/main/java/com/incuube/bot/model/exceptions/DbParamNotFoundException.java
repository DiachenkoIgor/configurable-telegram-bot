package com.incuube.bot.model.exceptions;

import lombok.Data;

@Data
public class DbParamNotFoundException extends RuntimeException {
    private String logMessage;
    private String userMessage;

    public DbParamNotFoundException(String message) {
        super(message);
    }
}
