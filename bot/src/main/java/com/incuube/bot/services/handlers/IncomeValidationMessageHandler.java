package com.incuube.bot.services.handlers;

import com.incuube.bot.model.common.Action;
import com.incuube.bot.model.common.users.User;
import com.incuube.bot.model.exceptions.ValidationException;
import com.incuube.bot.model.income.IncomeMessage;
import com.incuube.bot.services.ActionProcessorFacade;
import com.incuube.bot.services.util.HandlerOrderConstants;
import com.incuube.bot.services.validators.Validator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO(igordiachenko): Create 'type' field in Validator for different messengers
@Service
@Log4j2
public class IncomeValidationMessageHandler implements IncomeMessageHandler {

    private IncomeMessageHandler nextMessageHandler;

    private ActionProcessorFacade actionProcessorFacade;

    private List<Validator> validators;

    @Autowired
    public IncomeValidationMessageHandler(ActionProcessorFacade actionProcessorFacade, List<Validator> validators) {
        this.actionProcessorFacade = actionProcessorFacade;
        this.validators = validators;
    }

    @Override
    public void handleMessage(IncomeMessage incomeMessage, User user, Action next) {
        try {
            if (next.getExpectedIncomeType() != null && incomeMessage.getIncomeType() != next.getExpectedIncomeType()) {

                actionProcessorFacade.sendRepeatErrorAction(
                        String.format("I expected %s message. Try again", incomeMessage.getIncomeType().toValue()), user);
            }
            validators.forEach(validator -> validator.checkMessageAndAction(next, incomeMessage));

            nextMessageHandler.handleMessage(incomeMessage, user, next);
        } catch (ValidationException ex) {
            log.error("Validation error for message - '{}'. Message is '{}'", incomeMessage, ex.getMessage());
            actionProcessorFacade.sendFatalError(incomeMessage.getUserId(), incomeMessage.getMessenger());
        }
    }

    @Override
    public void setNext(IncomeMessageHandler messageHandler) {
        this.nextMessageHandler = messageHandler;
    }

    @Override
    public int getOrder() {
        return HandlerOrderConstants.VALIDATION_HANDLER_VALUE;
    }
}
