package com.incuube.bot.model.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RcsApiBadGatewayException extends RuntimeException {
    private String message;
}
