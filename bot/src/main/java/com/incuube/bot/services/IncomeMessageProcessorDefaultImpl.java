package com.incuube.bot.services;

import com.incuube.bot.model.income.IncomeMessage;
import com.incuube.bot.services.handlers.IncomeMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

//TODO(igordiachenko): Implementation of processMessage method
@Service

public class IncomeMessageProcessorDefaultImpl implements IncomeMessageProcessor {

    private IncomeMessageHandler firstHandler;

    @Autowired
    public IncomeMessageProcessorDefaultImpl(@Qualifier("firstHandler") IncomeMessageHandler firstHandler) {
        this.firstHandler = firstHandler;
    }

    @Override
    public void processMessage(IncomeMessage incomeMessage) {
        firstHandler.handleMessage(incomeMessage, null, null);
    }

}
