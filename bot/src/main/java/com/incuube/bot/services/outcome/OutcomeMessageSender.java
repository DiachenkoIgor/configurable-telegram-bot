package com.incuube.bot.services.outcome;

import com.incuube.bot.model.common.users.TelegramUser;
import com.incuube.bot.model.common.users.User;
import com.incuube.bot.model.exceptions.BotConfigException;
import com.incuube.bot.model.outcome.OutcomeMessage;
import com.incuube.bot.model.outcome.OutcomeSuggestionMessage;
import com.incuube.bot.model.outcome.OutcomeTextMessage;
import com.incuube.bot.util.JsonConverter;
import com.incuube.bot.util.ParamsExtractor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log4j2
public class OutcomeMessageSender {

    private TelegramMessageSender telegramMessageSender;
    @Value("${bot.telegram.serviceChat}")
    private String serviceChatId;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Pattern placeHolderPattern = Pattern.compile("\\$\\{(.+?)\\}");

    @Autowired
    public OutcomeMessageSender(TelegramMessageSender telegramMessageSender) {
        this.telegramMessageSender = telegramMessageSender;
    }

    public String sendOutcomeMessage(OutcomeMessage outcomeMessage, User user) {
        switch (user.getMessenger()) {
            case TELEGRAM:
                TelegramUser telegramUser = (TelegramUser) user;

                sendTelegramOutcomeMessage(outcomeMessage, telegramUser);
                return telegramUser.getId();
            default:
                throw new BotConfigException("Unsupported Messenger! " + user.getMessenger());
        }
    }

    private void sendTelegramOutcomeMessage(OutcomeMessage outcomeMessage, TelegramUser telegramUser) {
        if (outcomeMessage.getParams().get("special_action") != null) {
            sendSpecialAction(telegramUser);
        }
        Optional<OutcomeMessage> message = prepareMessage(outcomeMessage, telegramUser);
        if (!message.isPresent()) {
            throw new BotConfigException("Params not found!!");
        }

        outcomeMessage = message.get();

        if (outcomeMessage instanceof OutcomeTextMessage) {
            telegramMessageSender.sendOutcomeMessage((OutcomeTextMessage) outcomeMessage, telegramUser);
        }
        if (outcomeMessage instanceof OutcomeSuggestionMessage) {
            telegramMessageSender.sendOutcomeMessage((OutcomeSuggestionMessage) outcomeMessage, telegramUser);
        }
    }

    private void sendSpecialAction(TelegramUser telegramUser) {
        StringBuilder sb = new StringBuilder("Request:\n");
        sb.append("User id (technical) - ").append(telegramUser.getId()).append("\n")
                .append("User first name - ").append(telegramUser.getFirst_name()).append("\n");
        if (telegramUser.getLast_name() != null) {
            sb.append("User last name - ").append(telegramUser.getLast_name()).append("\n");
        }
        if (telegramUser.getUsername() != null) {
            sb.append("Username - ").append(telegramUser.getUsername()).append("\n");
        }
        sb.append("City - \"").append(telegramUser.getParams().get("city")).append("\"\n");
        sb.append("Days - \"").append(telegramUser.getParams().get("days")).append("\"\n");
        sb.append("Month - \"").append(telegramUser.getParams().get("month")).append("\"\n");
        sb.append("Persons - \"").append(telegramUser.getParams().get("persons")).append("\"\n");
        sb.append("Time -\"").append(telegramUser.getLastActionTime().plusHours(2).format(formatter)).append("\"\n");
        sb.append("Connection message -\"").append((String) telegramUser.getParams().get("contact_info")).append("\"");

    
        OutcomeTextMessage textMessage = new OutcomeTextMessage();
        textMessage.setText(sb.toString());

        Optional<OutcomeMessage> outcomeMessage = prepareMessage(textMessage, telegramUser);

        if (!outcomeMessage.isPresent()) {
            throw new BotConfigException("Params setting for special action is invalid!!");
        }

        telegramMessageSender.sendOutcomeMessage((OutcomeTextMessage) outcomeMessage.get(), serviceChatId);
    }

    private Optional<OutcomeMessage> prepareMessage(OutcomeMessage outcomeMessage, User user) {
  //      log.info("Params sender for message - {}", outcomeMessage);
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
