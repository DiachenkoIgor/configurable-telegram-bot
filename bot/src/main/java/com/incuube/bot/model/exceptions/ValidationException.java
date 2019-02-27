package com.incuube.bot.model.exceptions;

import lombok.Data;

@Data
public class ValidationException extends RuntimeException {
    private String message;
}
