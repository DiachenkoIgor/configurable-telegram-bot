package com.incuube.bot.services.outcome;

import com.incuube.bot.model.common.users.RcsUser;
import com.incuube.bot.model.common.users.User;
import com.incuube.bot.model.exceptions.BotConfigException;
import com.incuube.bot.model.outcome.OutcomeMessage;
import com.incuube.bot.model.outcome.OutcomeSuggestionMessage;
import com.incuube.bot.model.outcome.OutcomeTextMessage;
import com.incuube.bot.util.JsonConverter;
import com.incuube.bot.util.ParamsExtractor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log4j2
public class OutcomeMessageSender {

    private OutcomeRcsSender outcomeRcsSender;

    private final Pattern placeHolderPattern = Pattern.compile("\\$\\{(.+?)\\}");

    @Autowired
    public OutcomeMessageSender(OutcomeRcsSender outcomeRcsSender) {
        this.outcomeRcsSender = outcomeRcsSender;
    }

    public String sendOutcomeMessage(OutcomeMessage outcomeMessage, User user) {
        switch (user.getMessenger()) {
            case RCS:
                RcsUser rcsUser = (RcsUser) user;
                sendRcsOutcomeMessage(outcomeMessage, rcsUser);
                return rcsUser.getNumber();
            default:
                throw new BotConfigException("Unsupported Messenger! " + user.getMessenger());
        }
    }

    private void sendRcsOutcomeMessage(OutcomeMessage outcomeMessage, RcsUser rcsUser) {
        Optional<OutcomeMessage> message = prepareMessage(outcomeMessage, rcsUser);
        if (!message.isPresent()) {
            throw new BotConfigException("Params not found!!");
        }

        outcomeMessage = message.get();

        if (outcomeMessage instanceof OutcomeTextMessage) {
            outcomeRcsSender.sendOutcomeMessage((OutcomeTextMessage) outcomeMessage, rcsUser);
        }
        if (outcomeMessage instanceof OutcomeSuggestionMessage) {
            outcomeRcsSender.sendOutcomeMessage((OutcomeSuggestionMessage) outcomeMessage, rcsUser);
        }
    }

    private Optional<OutcomeMessage> prepareMessage(OutcomeMessage outcomeMessage, User user) {
        log.info("Params sender for message - {}", outcomeMessage);
        Optional<String> jsonRepresentation = JsonConverter.convertObject(outcomeMessage);
        if (jsonRepresentation.isPresent()) {
            String json = jsonRepresentation.get();
            Matcher matcher = placeHolderPattern.matcher(json);
            while (matcher.find()) {
                String paramName = matcher.group(1);
                if (paramName != null) {
                    Optional<String> paramFromMap = ParamsExtractor.getParamFromMap(user.getParams(), paramName);
                    if (paramFromMap.isPresent()) {
                        json = json.replace(String.format("${%s}", paramName), paramFromMap.get());
                    } else {
                        log.error("Param wasn't found! Param name - '{}'!", paramFromMap);
                        return Optional.empty();
                    }
                }
            }
            return JsonConverter.convertJson(json, OutcomeMessage.class);
        }
        return Optional.of(outcomeMessage);
    }
}
