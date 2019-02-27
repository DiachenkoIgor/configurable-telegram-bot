package com.incuube.bot.model.exceptions;

import lombok.Data;

@Data
public class BotDabaseException extends RuntimeException {
    private String message;
}
