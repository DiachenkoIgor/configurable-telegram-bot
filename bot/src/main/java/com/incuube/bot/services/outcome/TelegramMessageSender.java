package com.incuube.bot.services.outcome;

import com.incuube.bot.messagehandling.TelegramListener;
import com.incuube.bot.model.common.Button;
import com.incuube.bot.model.common.users.TelegramUser;
import com.incuube.bot.model.outcome.OutcomeSuggestionMessage;
import com.incuube.bot.model.outcome.OutcomeTextMessage;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

//TODO: Change @PostConstruct logic
@Service
public class TelegramMessageSender {

    private TelegramListener telegramListener;

    @PostConstruct
    public void prepare() {
        this.telegramListener = ApiContext.getInstance(TelegramListener.class);
    }

    public void sendOutcomeMessage(OutcomeTextMessage textMessage, TelegramUser telegramUser) {
        SendMessage message = new SendMessage()
                .setChatId(Long.valueOf(telegramUser.getId()))
                .setText(textMessage.getText());

        try {
            telegramListener.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void sendOutcomeMessage(OutcomeTextMessage textMessage, String chatId) {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(textMessage.getText());

        try {
            telegramListener.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void sendOutcomeMessage(OutcomeSuggestionMessage suggestionMessage, TelegramUser telegramUser) {
        List<Button> buttons = suggestionMessage.getButtons();

        SendMessage message = new SendMessage()
                .setChatId(Long.valueOf(telegramUser.getId()))
                .setText(suggestionMessage.getText());

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        buttons.forEach(button -> {
            ArrayList<InlineKeyboardButton> list = new ArrayList<>();
            list.add(new InlineKeyboardButton().setText(button.getButtonText()).setCallbackData(button.getNextActionId() + "@" + button.getButtonText()));
            rowsInline.add(list);
        });

        markupInline.setKeyboard(rowsInline);

        message.setReplyMarkup(markupInline);

        try {
            telegramListener.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
