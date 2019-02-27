package com.incuube.receiver.messageconsumer;


import com.incuube.rcs.datamodel.messages.income.UserMessage;
import com.incuube.receiver.messagefactory.UserMessageFactory;
import com.incuube.receiver.messagehandlers.api.MessageHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Component
@Log4j2
public class MessageConsumer {


    private List<MessageHandler> messageHandlers;
    private UserMessageFactory userMessageFactory;


    @Autowired
    public MessageConsumer(MessageHandler[] handlers, UserMessageFactory userMessageFactory) {
        this.messageHandlers = new ArrayList<>(Arrays.asList(handlers));
        this.userMessageFactory = userMessageFactory;
    }

    @SqsListener("${aws.sqs.queue}")
    public void handleMessage(String payload) {

        log.info("Received message - " + payload.replace("\n", ""));

        try {
            Optional<UserMessage> message = userMessageFactory.createMessage(payload);

            if (!message.isPresent()) {
                log.debug("Don't handle that type of message!");
            } else {
                messageHandlers.forEach(h -> h.handleMessage(message.get()));
            }

        } catch (IOException e) {
            log.error("Parsing exception.", e);
        }
    }
}
