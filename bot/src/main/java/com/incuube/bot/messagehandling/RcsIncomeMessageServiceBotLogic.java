package com.incuube.bot.messagehandling;


import com.incuube.bot.model.exceptions.BotIncomeMessageNotSupportedException;
import com.incuube.bot.model.exceptions.EventsNotSupportedException;
import com.incuube.bot.model.income.util.Messengers;
import com.incuube.bot.services.ActionProcessorFacade;
import com.incuube.bot.services.IncomeMessageProcessor;
import com.incuube.bot.services.converters.RcsIncomeMessageConverter;
import com.incuube.rcs.datamodel.exceptions.RbmDatabaseException;
import com.incuube.rcs.datamodel.messages.income.UserMessage;
import com.incuube.receiver.messagehandlers.api.MessageHandler;
import com.incuube.receiver.repositories.api.IncomeMessageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RcsIncomeMessageServiceBotLogic implements MessageHandler {

    private final IncomeMessageRepository messageRepository;
    private final RcsIncomeMessageConverter incomeMessageConverter;
    private final IncomeMessageProcessor incomeMessageProcessor;
    private final ActionProcessorFacade actionProcessorFacade;

    @Autowired
    public RcsIncomeMessageServiceBotLogic(IncomeMessageRepository messageRepository, RcsIncomeMessageConverter incomeMessageConverter, IncomeMessageProcessor incomeMessageProcessor, ActionProcessorFacade actionProcessorFacade) {
        this.messageRepository = messageRepository;
        this.incomeMessageConverter = incomeMessageConverter;
        this.incomeMessageProcessor = incomeMessageProcessor;
        this.actionProcessorFacade = actionProcessorFacade;
    }

    @Async("messageReceiverTaskExecutor")
    public void handleMessage(UserMessage userMessage) {
        try {
            /*if (messageRepository.checkIfExistsMessage(userMessage.getMessageId())) {
                messageRepository.incomeMessageUpdate(userMessage);
            } else {
                messageRepository.incomeMessageSave(userMessage);
            }*/
            incomeMessageProcessor.processMessage(
                    incomeMessageConverter.convertToCommonModel(userMessage)
            );

        } /*catch (RbmDatabaseException ex) {
            log.error("Income message saving exception.", ex);
        }*/ catch (BotIncomeMessageNotSupportedException ex) {
            log.error("Received unsupported message type. {}", userMessage);
            actionProcessorFacade.unknownMessageHandler(userMessage.getNumber().substring(1), Messengers.RCS);
        } catch (EventsNotSupportedException ex) {
            log.info("Events are not supported");
        }
    }
}
