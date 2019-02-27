package com.incuube.bot.services.handlers;

import com.incuube.bot.database.actions.ActionRepository;
import com.incuube.bot.model.common.Action;
import com.incuube.bot.model.common.users.User;
import com.incuube.bot.model.income.IncomeMessage;
import com.incuube.bot.services.ActionProcessorFacade;
import com.incuube.bot.services.util.HandlerOrderConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NextActionMessageHandler implements IncomeMessageHandler {

    private ActionRepository actionRepository;

    private ActionProcessorFacade actionProcessorFacade;

    @Autowired
    public NextActionMessageHandler(ActionRepository actionRepository, ActionProcessorFacade actionProcessorFacade) {
        this.actionRepository = actionRepository;
        this.actionProcessorFacade = actionProcessorFacade;
    }

    @Override
    public void handleMessage(IncomeMessage incomeMessage, User user, Action next) {
        actionProcessorFacade.sendAction(next, user);

    }

    @Override
    public void setNext(IncomeMessageHandler messageHandler) {

    }

    @Override
    public int getOrder() {
        return HandlerOrderConstants.NEXT_ACTION_HANDLER_VALUE;
    }
}
