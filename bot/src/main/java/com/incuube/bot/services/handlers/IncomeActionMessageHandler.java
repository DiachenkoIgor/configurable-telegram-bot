package com.incuube.bot.services.handlers;

import com.incuube.bot.database.actions.ActionRepository;
import com.incuube.bot.model.common.Action;
import com.incuube.bot.model.common.users.User;
import com.incuube.bot.model.exceptions.BotDabaseException;
import com.incuube.bot.model.income.IncomeMessage;
import com.incuube.bot.model.income.IncomeSuggestionMessage;
import com.incuube.bot.services.ActionProcessorFacade;
import com.incuube.bot.services.util.HandlerOrderConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
public class IncomeActionMessageHandler implements IncomeMessageHandler {

    private IncomeMessageHandler nextMessageHandler;

    private ActionRepository actionRepository;

    private ActionProcessorFacade actionProcessorFacade;

    @Autowired
    public IncomeActionMessageHandler(ActionRepository actionRepository, ActionProcessorFacade actionProcessorFacade) {
        this.actionRepository = actionRepository;
        this.actionProcessorFacade = actionProcessorFacade;
    }

    @Override
    public void handleMessage(IncomeMessage incomeMessage, User user, Action next) {
      //  log.info("Action Handler for message - {}.", incomeMessage);
        try {
            Optional<Action> action;
            switch (incomeMessage.getIncomeType()) {
                case SUGGESTION:
                    IncomeSuggestionMessage suggestionMessage = (IncomeSuggestionMessage) incomeMessage;
                    action = actionRepository.getActionById(suggestionMessage.getPostbackData());
                    break;
                case TEXT:
                    if (user.getCurrentAction() == null) {
                        action = Optional.empty();
                        break;
                    }
                    Optional<Action> actionById = actionRepository.getActionById(user.getCurrentAction());
                    if (actionById.isPresent() && actionById.get().getNextActionId() != null) {
                        action = actionRepository.getActionById(actionById.get().getNextActionId());
                    } else {
                        action = Optional.empty();
                    }
                    break;
                default:
                    action = Optional.empty();
            }
            if (!action.isPresent()) {
                log.error("Action was not found in action handler for message - " + incomeMessage);
                actionProcessorFacade.sendRepeatErrorAction("I can't recognize your message.Please try again", user);
            } else {
                nextMessageHandler.handleMessage(incomeMessage, user, action.get());
            }
        } catch (BotDabaseException ex) {
            actionProcessorFacade.sendFatalError(incomeMessage.getUserId(), incomeMessage.getMessenger());
        }
    }

    @Override
    public void setNext(IncomeMessageHandler messageHandler) {
        this.nextMessageHandler = messageHandler;
    }

    @Override
    public int getOrder() {
        return HandlerOrderConstants.ACTION_HANDLER_VALUE;
    }
}
