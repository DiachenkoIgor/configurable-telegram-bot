package com.incuube.bot.services.converters;

import com.incuube.bot.model.common.IncomeType;
import com.incuube.bot.model.exceptions.BotIncomeMessageNotSupportedException;
import com.incuube.bot.model.exceptions.EventsNotSupportedException;
import com.incuube.bot.model.income.IncomeMessage;
import com.incuube.bot.model.income.IncomeSuggestionMessage;
import com.incuube.bot.model.income.IncomeTextMessage;
import com.incuube.bot.model.income.util.Messengers;
import com.incuube.rcs.datamodel.messages.income.EventUserMessage;
import com.incuube.rcs.datamodel.messages.income.SuggestionUserMessage;
import com.incuube.rcs.datamodel.messages.income.TextUserMessage;
import com.incuube.rcs.datamodel.messages.income.UserMessage;
import org.springframework.stereotype.Service;

@Service
public class RcsIncomeMessageConverter {

    public IncomeMessage convertToCommonModel(UserMessage userMessage) {
        if (userMessage instanceof TextUserMessage) {
            return convertTextMessageToCommonModel((TextUserMessage) userMessage);
        }
        if (userMessage instanceof SuggestionUserMessage) {
            return convertSuggestionMessageToCommonModel((SuggestionUserMessage) userMessage);
        }
        if (userMessage instanceof EventUserMessage) {
            throw new EventsNotSupportedException();
        }
        throw new BotIncomeMessageNotSupportedException();
    }

    private IncomeMessage convertTextMessageToCommonModel(TextUserMessage textUserMessage) {
        IncomeTextMessage message = new IncomeTextMessage();
        message.setMessenger(Messengers.RCS);
        message.setText(textUserMessage.getText());
        message.setUserId(textUserMessage.getNumber().substring(1));
        message.setIncomeType(IncomeType.TEXT);

        message.getParams().put("messageId", textUserMessage.getMessageId());
        message.getParams().put("sendTime", textUserMessage.getSendTime());
        message.getParams().put("text", textUserMessage.getText());
        message.getParams().put("userId", textUserMessage.getNumber().substring(1));

        return message;
    }

    private IncomeMessage convertSuggestionMessageToCommonModel(SuggestionUserMessage suggestionUserMessage) {
        IncomeSuggestionMessage incomeSuggestionMessage = new IncomeSuggestionMessage();
        incomeSuggestionMessage.setButtonText(suggestionUserMessage.getSuggestionResponse().getText());
        incomeSuggestionMessage.setPostbackData(suggestionUserMessage.getSuggestionResponse().getPostbackData());
        incomeSuggestionMessage.setMessenger(Messengers.RCS);
        incomeSuggestionMessage.setUserId(suggestionUserMessage.getNumber().substring(1));
        incomeSuggestionMessage.setIncomeType(IncomeType.SUGGESTION);

        incomeSuggestionMessage.getParams().put("sendTime", suggestionUserMessage.getSendTime());
        incomeSuggestionMessage.getParams().put("messageId", suggestionUserMessage.getMessageId());
        incomeSuggestionMessage.getParams().put("postbackData", suggestionUserMessage.getSuggestionResponse().getPostbackData());
        incomeSuggestionMessage.getParams().put("buttonText", suggestionUserMessage.getSuggestionResponse().getText());
        incomeSuggestionMessage.getParams().put("userId", suggestionUserMessage.getNumber().substring(1));

        return incomeSuggestionMessage;
    }


}
