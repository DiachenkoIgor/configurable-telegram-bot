package com.incuube.bot.services;

import com.incuube.bot.database.actions.ActionRepository;
import com.incuube.bot.database.users.UserRepository;
import com.incuube.bot.model.common.Action;
import com.incuube.bot.model.common.users.RcsUser;
import com.incuube.bot.model.common.users.User;
import com.incuube.bot.model.exceptions.BotConfigException;
import com.incuube.bot.model.exceptions.RcsApiBadGatewayException;
import com.incuube.bot.model.income.util.Messengers;
import com.incuube.bot.model.outcome.OutcomeTextMessage;
import com.incuube.bot.services.outcome.OutcomeMessageSender;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

//TODO(igordiachenko): Create logic
//TODO(igordiachenko): Exception creating
@Service
@Log4j2
public class ActionProcessorFacade {

    private OutcomeMessageSender outcomeMessageSender;

    private UserRepository userRepository;

    private ActionRepository actionRepository;

    @Autowired
    public ActionProcessorFacade(OutcomeMessageSender outcomeMessageSender, UserRepository userRepository, ActionRepository actionRepository) {
        this.outcomeMessageSender = outcomeMessageSender;
        this.userRepository = userRepository;
        this.actionRepository = actionRepository;
    }

    public void sendDefaultAction(User user) {
        try {
            Optional<Action> actionById = actionRepository.getActionById("system.action.default");
            if (actionById.isPresent()) {
                Action defaultAction = actionById.get();
                String userId = outcomeMessageSender.sendOutcomeMessage(defaultAction.getOutcomeMessage(), user);
                log.info("Successful sending default message to user - {}", userId);
                user.setCurrentAction(defaultAction.getId());
                user.setLastActionTime(LocalDateTime.now());
                userRepository.saveUserToDb(user);
            } else {
                throw new BotConfigException("Default system action ('system.action.default')' was missing!");
            }
        } catch (BotConfigException ex) {
            log.error("Send default problem. Configuration error - '{}'", ex.getMessage());
        } catch (RcsApiBadGatewayException ex) {
            log.fatal(ex.getMessage());
        }

    }

    public void sendAction(Action action, User user) {
        try {
            String userId = outcomeMessageSender.sendOutcomeMessage(action.getOutcomeMessage(), user);
            log.info("Successful sending next message to user - {}", userId);
            user.setCurrentAction(action.getId());
            user.setLastActionTime(LocalDateTime.now());
            userRepository.updateUser(user);
        } catch (BotConfigException ex) {
            log.error("Send action problem. Configuration error - '{}'", ex.getMessage());
        } catch (RcsApiBadGatewayException ex) {
            log.fatal(ex.getMessage());
        }
    }

    public void sendFatalError(String identifier, Messengers messengers) {
        try {
            Optional<Action> actionById = actionRepository.getActionById("system.action.error");

            if (!actionById.isPresent()) {
                throw new BotConfigException("Default system action ('system.action.error')' was missing!");
            }

            if (messengers == Messengers.RCS) {
                RcsUser rcsUser = new RcsUser();
                rcsUser.setNumber(identifier);
                String userId = outcomeMessageSender.sendOutcomeMessage(actionById.get().getOutcomeMessage(), rcsUser);
                log.error("Fatal error for user - {}", userId);
            }
        } catch (BotConfigException ex) {
            log.error("Send fatal error problem. Configuration error - '{}'", ex.getMessage());
        } catch (RcsApiBadGatewayException ex) {
            log.fatal(ex.getMessage());
        }
    }

    public void sendRepeatErrorAction(String message, User user) {
        try {
            OutcomeTextMessage outcomeTextMessage = new OutcomeTextMessage();
            outcomeTextMessage.setText(message);

            String userId = outcomeMessageSender.sendOutcomeMessage(outcomeTextMessage, user);
            log.info("Was send error message to user - {}", userId);

            Optional<Action> actionById = actionRepository.getActionById(user.getCurrentAction());
            if (actionById.isPresent()) {
                sendAction(actionById.get(), user);
            } else {
                throw new BotConfigException("Param 'user.currentAction' was missing!");
            }
        } catch (BotConfigException ex) {
            log.error("Send repeat error action problem. Configuration error - '{}'", ex.getMessage());
        } catch (RcsApiBadGatewayException ex) {
            log.fatal(ex.getMessage());
        }
    }

    public void unknownMessageHandler(String userIdentifier, Messengers messengers) {
        try {
            Optional<User> userFromDb = userRepository.getUserFromDb(userIdentifier, messengers);
            if (userFromDb.isPresent()) {
                sendRepeatErrorAction("Please send valid message.", userFromDb.get());
            } else {
                if (messengers == Messengers.RCS) {
                    RcsUser rcsUser = new RcsUser();
                    rcsUser.setMessenger(Messengers.RCS);
                    rcsUser.setNumber(userIdentifier);
                    sendDefaultAction(rcsUser);
                }
            }
        } catch (BotConfigException ex) {
            log.error("Send repeat error action problem. Configuration error - '{}'", ex.getMessage());
        } catch (RcsApiBadGatewayException ex) {
            log.fatal(ex.getMessage());
        }
    }
}
