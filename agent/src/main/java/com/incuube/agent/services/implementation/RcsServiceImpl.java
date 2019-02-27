package com.incuube.agent.services.implementation;


import com.google.api.services.rcsbusinessmessaging.v1.model.Capabilities;
import com.google.api.services.rcsbusinessmessaging.v1.model.Suggestion;
import com.incuube.agent.googlelib.RbmApiHelper;
import com.incuube.agent.googlelib.util.DomainConverter;
import com.incuube.agent.repositories.api.OutcomeMessageRepository;
import com.incuube.agent.services.api.RcsService;
import com.incuube.rcs.datamodel.messages.outcome.*;
import com.incuube.rcs.datamodel.rest.RcsFile;
import com.incuube.rcs.datamodel.rest.RcsSuggestionActionMessage;
import com.incuube.rcs.datamodel.rest.RcsSuggestionMessage;
import com.incuube.rcs.datamodel.rest.RcsTextMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@Async("restClientTaskExecutor")
public class RcsServiceImpl implements RcsService {

    private RbmApiHelper rbmApiHelper;

    private OutcomeMessageRepository messageRepository;

    @Autowired
    public RcsServiceImpl(RbmApiHelper rbmApiHelper, OutcomeMessageRepository messageRepository) {
        this.rbmApiHelper = rbmApiHelper;
        this.messageRepository = messageRepository;
    }

    public void sendTextMessage(RcsTextMessage message, DeferredResult<ResponseEntity<?>> deferredResult) {

        try {
            String messageId = rbmApiHelper.sendTextMessage(message.getText(), message.getNumber());

            log.info("Text Message was sent. Id is " + messageId);

            TextOutcomeMessage textMessage = new TextOutcomeMessage();
            textMessage.setText(message.getText());

            messageRepository.outcomeMessageSave(
                    prepareOutcomeMessage(message.getNumber(), messageId, textMessage));

            deferredResult.setResult(ResponseEntity.ok().build());

        } catch (Exception ex) {
            deferredResult.setErrorResult(ex);
        }

    }

    public void sendSuggestionActionMessages(List<RcsSuggestionActionMessage> actionMessages, DeferredResult<ResponseEntity<?>> deferredResult, String phone, String text) {

        try {
            List<Suggestion> collect = actionMessages.stream().map(DomainConverter::convertSuggestionAction).collect(Collectors.toList());

            String messageId = rbmApiHelper.sendSuggestions(collect, phone, text);

            log.info("Message with suggestions actions was sent. Id is " + messageId);

            SuggestionActionOutcomeMessage outcomeMessage = new SuggestionActionOutcomeMessage();
            outcomeMessage.setRcsSuggestionActionMessages(actionMessages);

            messageRepository.outcomeMessageSave(
                    prepareOutcomeMessage(phone, messageId, outcomeMessage)
            );

            deferredResult.setResult(ResponseEntity.ok().build());
        } catch (Exception ex) {
            deferredResult.setErrorResult(ex);
        }

    }


    public void sendSuggestion(List<RcsSuggestionMessage> suggestionMessages, String phone, String text, DeferredResult<ResponseEntity<?>> deferredResult) {

        try {
            List<Suggestion> collect = suggestionMessages.stream().map(DomainConverter::convertSuggestionReply).collect(Collectors.toList());

            String messageId = rbmApiHelper.sendSuggestions(collect, phone, text);

            log.info("Message with suggestions  was sent. Id is " + messageId);

            SuggestionOutcomeMessage suggestionMessage = new SuggestionOutcomeMessage();
            suggestionMessage.setRcsSuggestionMessages(suggestionMessages);

            messageRepository.outcomeMessageSave(
                    prepareOutcomeMessage(phone, messageId, suggestionMessage));

            deferredResult.setResult(ResponseEntity.ok().build());

        } catch (Exception ex) {
            deferredResult.setErrorResult(ex);
        }

    }

    public void sendFile(String number, String fileName, DeferredResult<ResponseEntity<?>> deferredResult) {

        try {
            String googleFileName = messageRepository.getGoogleFileName(fileName);
            String messageId = rbmApiHelper.sendFile(number, googleFileName);

            log.info("Message with file  was sent. Id is " + messageId + " . File name is " + fileName);

            FileOutcomeMessage fileMessage = new FileOutcomeMessage();
            fileMessage.setFileName(fileName);

            messageRepository.outcomeMessageSave(
                    prepareOutcomeMessage(number, messageId, fileMessage)
            );

            deferredResult.setResult(ResponseEntity.ok().build());

        } catch (Exception ex) {
            deferredResult.setErrorResult(ex);
        }
    }

    public void uploadFile(RcsFile rcsFile, DeferredResult<ResponseEntity<?>> deferredResult) {

        try {
            rcsFile.setGoogleName(
                    rbmApiHelper.uploadFile(rcsFile.getUrl(), rcsFile.getThumbnailUrl()));

            log.info("File was uploaded. File name is " + rcsFile.getName());

            messageRepository.saveFile(rcsFile);

            deferredResult.setResult(ResponseEntity.ok().build());

        } catch (Exception ex) {
            deferredResult.setErrorResult(ex);
        }
    }

    public void sendCheckRequest(String number, DeferredResult<ResponseEntity<?>> deferredResult) {

        try {
            Capabilities capability = rbmApiHelper.getCapability(number);

            log.info("Number - " + number + " is rcs enabled.");

            deferredResult.setResult(ResponseEntity.ok(capability));

        } catch (Exception ex) {
            deferredResult.setErrorResult(ex);
        }
    }

    public void sendReadEvent(String messageId, String number, DeferredResult<ResponseEntity<?>> deferredResult) {

        try {
            rbmApiHelper.sendReadMessage(messageId, number);

            log.info("Successful read event for Message id - " + messageId);

            deferredResult.setResult(ResponseEntity.ok().build());

        } catch (Exception ex) {
            deferredResult.setErrorResult(ex);
        }
    }

    private OutcomeMessage prepareOutcomeMessage(String phone, String messageId, OutcomeMessage message) {

        message.setMessageId(messageId);
        message.setSendTime(Instant.now().toEpochMilli());
        message.setSenderPhoneNumber(phone);
        message.setSender("Incuube agent");

        return message;
    }

}
