package com.incuube.bot.services.converters;

import com.incuube.bot.database.actions.ActionRepository;
import com.incuube.bot.database.users.UserRepository;
import com.incuube.bot.model.common.IncomeType;
import com.incuube.bot.model.exceptions.BotIncomeMessageNotSupportedException;
import com.incuube.bot.model.income.IncomeMessage;
import com.incuube.bot.model.income.IncomeSuggestionMessage;
import com.incuube.bot.model.income.IncomeTextMessage;
import com.incuube.bot.model.income.util.Messengers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class TelegramIncomeMessageConverter {

    public IncomeMessage convertToCommonModel(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            return textMessage(update);
        }
        if (update.hasCallbackQuery()) {
            return suggestionMessage(update);
        }

        throw new BotIncomeMessageNotSupportedException();
    }

    private IncomeMessage textMessage(Update update) {
        IncomeTextMessage message = new IncomeTextMessage();
        message.setMessenger(Messengers.TELEGRAM);
        message.setText(update.getMessage().getText());
        message.setUserId(String.valueOf(update.getMessage().getFrom().getId()));
        message.setIncomeType(IncomeType.TEXT);

        message.getParams().put("messageId", update.getMessage().getMessageId());
        message.getParams().put("sendTime", update.getMessage().getDate());
        message.getParams().put("text", update.getMessage().getText());
        message.getParams().put("userId", String.valueOf(update.getMessage().getFrom().getId()));

        if (update.getMessage().getFrom().getFirstName() != null) {
            message.getParams().put("first_name", update.getMessage().getFrom().getFirstName());
        }
        if (update.getMessage().getFrom().getLastName() != null) {
            message.getParams().put("last_name", update.getMessage().getFrom().getLastName());
        }
        if (update.getMessage().getFrom().getUserName() != null) {
            message.getParams().put("username", update.getMessage().getFrom().getUserName());
        }

        return message;
    }

    private IncomeMessage suggestionMessage(Update update) {
        IncomeSuggestionMessage incomeSuggestionMessage = new IncomeSuggestionMessage();

        incomeSuggestionMessage.setMessenger(Messengers.TELEGRAM);
        incomeSuggestionMessage.setUserId(String.valueOf(update.getCallbackQuery().getFrom().getId()));
        incomeSuggestionMessage.setIncomeType(IncomeType.SUGGESTION);
        if(update.getCallbackQuery().getData().contains("@")) {
            String[] values = update.getCallbackQuery().getData().split("@");
            incomeSuggestionMessage.setPostbackData(values[0]);
            incomeSuggestionMessage.setButtonText(values[1]);
            incomeSuggestionMessage.getParams().put("buttonText", incomeSuggestionMessage.getButtonText());
        }else {
            incomeSuggestionMessage.setPostbackData(update.getCallbackQuery().getData());
        }


        incomeSuggestionMessage.getParams().put("messageId", update.getCallbackQuery().getId());
        incomeSuggestionMessage.getParams().put("postbackData", update.getCallbackQuery().getData());
        incomeSuggestionMessage.getParams().put("userId", String.valueOf(update.getCallbackQuery().getFrom().getId()));


        if (update.getCallbackQuery().getFrom().getFirstName() != null) {
            incomeSuggestionMessage.getParams().put("first_name", update.getCallbackQuery().getFrom().getFirstName());
        }
        if (update.getCallbackQuery().getFrom().getLastName() != null) {
            incomeSuggestionMessage.getParams().put("last_name", update.getCallbackQuery().getFrom().getLastName());
        }
        if (update.getCallbackQuery().getFrom().getUserName() != null) {
            incomeSuggestionMessage.getParams().put("username", update.getCallbackQuery().getFrom().getUserName());
        }

        return incomeSuggestionMessage;
    }


}
