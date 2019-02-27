package com.incuube.bot.services.validators;

import com.incuube.bot.model.common.Action;
import com.incuube.bot.model.income.IncomeMessage;

public interface Validator {
    void checkMessageAndAction(Action action, IncomeMessage incomeMessage);
}
