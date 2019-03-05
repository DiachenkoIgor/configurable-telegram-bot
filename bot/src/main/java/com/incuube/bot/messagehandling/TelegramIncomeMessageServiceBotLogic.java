package com.incuube.bot.messagehandling;

import com.incuube.bot.model.exceptions.BotIncomeMessageNotSupportedException;
import com.incuube.bot.services.ActionProcessorFacade;
import com.incuube.bot.services.IncomeMessageProcessor;
import com.incuube.bot.services.converters.TelegramIncomeMessageConverter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Log4j2
public class TelegramIncomeMessageServiceBotLogic {

    private final IncomeMessageProcessor incomeMessageProcessor;
    private final ActionProcessorFacade actionProcessorFacade;
    private final TelegramIncomeMessageConverter telegramIncomeMessageConverter;

    @Autowired
    public TelegramIncomeMessageServiceBotLogic(IncomeMessageProcessor incomeMessageProcessor, ActionProcessorFacade actionProcessorFacade, TelegramIncomeMessageConverter telegramIncomeMessageConverter) {
        this.incomeMessageProcessor = incomeMessageProcessor;
        this.actionProcessorFacade = actionProcessorFacade;
        this.telegramIncomeMessageConverter = telegramIncomeMessageConverter;
    }

    @Async("messageReceiverTaskExecutor")
    public void handleMessage(Update updateMessage) {
        try {

            /*Don't forget about saving to db*/
            incomeMessageProcessor.processMessage(
                    telegramIncomeMessageConverter.convertToCommonModel(updateMessage)
            );

        } /*catch (RbmDatabaseException ex) {
            log.error("Income message saving exception.", ex);
        }*/ catch (BotIncomeMessageNotSupportedException ex) {
            log.error("Received unsupported message type. {}", updateMessage);
         //   actionProcessorFacade.unknownMessageHandler(userMessage.getNumber().substring(1), Messengers.RCS);
        }
    }
}
