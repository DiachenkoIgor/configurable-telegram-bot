package com.incuube.bot.model.exceptions;

public class BotIncomeMessageNotSupportedException extends RuntimeException {

    public BotIncomeMessageNotSupportedException(String message) {
        super(message);
    }

    public BotIncomeMessageNotSupportedException() {
    }
}
