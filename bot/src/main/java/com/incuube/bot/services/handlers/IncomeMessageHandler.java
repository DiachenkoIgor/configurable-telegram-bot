package com.incuube.bot.services.handlers;

import com.incuube.bot.model.common.Action;
import com.incuube.bot.model.common.users.User;
import com.incuube.bot.model.income.IncomeMessage;
import org.springframework.core.Ordered;

public interface IncomeMessageHandler extends Ordered {
    void handleMessage(IncomeMessage incomeMessage, User user, Action next);

    void setNext(IncomeMessageHandler messageHandler);
}
