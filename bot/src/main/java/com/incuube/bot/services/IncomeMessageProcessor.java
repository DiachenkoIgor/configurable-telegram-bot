package com.incuube.bot.services;

import com.incuube.bot.model.income.IncomeMessage;

public interface IncomeMessageProcessor {
    void processMessage(IncomeMessage incomeMessage);
}
