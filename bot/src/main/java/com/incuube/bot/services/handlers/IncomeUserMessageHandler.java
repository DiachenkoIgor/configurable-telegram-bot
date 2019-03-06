package com.incuube.bot.services.handlers;

import com.incuube.bot.database.users.UserRepository;
import com.incuube.bot.model.common.Action;
import com.incuube.bot.model.common.users.RcsUser;
import com.incuube.bot.model.common.users.TelegramUser;
import com.incuube.bot.model.common.users.User;
import com.incuube.bot.model.exceptions.BotDabaseException;
import com.incuube.bot.model.income.IncomeMessage;
import com.incuube.bot.model.income.util.Messengers;
import com.incuube.bot.services.ActionProcessorFacade;
import com.incuube.bot.services.util.HandlerOrderConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

//TODO(igordiachenko): Exceptions handling
@Service
@Log4j2
public class IncomeUserMessageHandler implements IncomeMessageHandler {

    private IncomeMessageHandler nextMessageHandler;

    private ActionProcessorFacade actionProcessorFacade;

    private UserRepository userRepository;

    @Autowired
    public IncomeUserMessageHandler(UserRepository userRepository, ActionProcessorFacade processorFacade) {
        this.actionProcessorFacade = processorFacade;
        this.userRepository = userRepository;
    }

    @Override
    public void handleMessage(IncomeMessage incomeMessage, User user, Action next) {
      //  log.info("User Handler for message - {}.", incomeMessage);
        try {
            Optional<User> userFromDb = userRepository.getUserFromDb(incomeMessage.getUserId(), incomeMessage.getMessenger());
            if (!userFromDb.isPresent()) {
           //     log.info("User is null for message - " + incomeMessage);
                switch (incomeMessage.getMessenger()) {
                    case TELEGRAM:
                        user = telegramUserPreparation(incomeMessage);
                        break;
                    default:
                        actionProcessorFacade.sendFatalError(incomeMessage.getUserId(), incomeMessage.getMessenger());
                        break;
                }
                actionProcessorFacade.sendDefaultAction(user);
            } else {
                if (incomeMessage.getMessenger() == Messengers.TELEGRAM) {
                    checkTelegramUserForUpToDate((TelegramUser) userFromDb.get(), incomeMessage);
                }
                nextMessageHandler.handleMessage(incomeMessage, userFromDb.get(), next);
            }
        } catch (BotDabaseException bot) {
            actionProcessorFacade.sendFatalError(incomeMessage.getUserId(), incomeMessage.getMessenger());
        }
    }

    private void checkTelegramUserForUpToDate(TelegramUser telegramUser, IncomeMessage incomeMessage) {
        boolean update = false;
        if (incomeMessage.getParams().containsKey("first_name") && !incomeMessage.getParams().get("first_name").equals(telegramUser.getFirst_name())) {
            telegramUser.setFirst_name((String) incomeMessage.getParams().get("first_name"));
            update = true;
        }
        if (incomeMessage.getParams().containsKey("last_name") && !incomeMessage.getParams().get("last_name").equals(telegramUser.getLast_name())) {
            telegramUser.setLast_name((String) incomeMessage.getParams().get("last_name"));
            update = true;
        }
        if (incomeMessage.getParams().containsKey("username") && !incomeMessage.getParams().get("username").equals(telegramUser.getUsername())) {
            telegramUser.setUsername((String) incomeMessage.getParams().get("username"));
            update = true;
        }

        if (update) {
            userRepository.updateUser(telegramUser);
        }
    }

    private TelegramUser telegramUserPreparation(IncomeMessage incomeMessage) {
        TelegramUser telegramUser = new TelegramUser();
        telegramUser.setMessenger(Messengers.TELEGRAM);
        telegramUser.setId(incomeMessage.getUserId());

        if (incomeMessage.getParams().containsKey("first_name")) {
            telegramUser.setFirst_name((String) incomeMessage.getParams().get("first_name"));
        }
        if (incomeMessage.getParams().containsKey("last_name")) {
            telegramUser.setLast_name((String) incomeMessage.getParams().get("last_name"));
        }
        if (incomeMessage.getParams().containsKey("username")) {
            telegramUser.setUsername((String) incomeMessage.getParams().get("username"));
        }
        return telegramUser;
    }

    @Override
    public void setNext(IncomeMessageHandler messageHandler) {
        this.nextMessageHandler = messageHandler;
    }

    @Override
    public int getOrder() {
        return HandlerOrderConstants.USER_HANDLER_VALUE;
    }
}
