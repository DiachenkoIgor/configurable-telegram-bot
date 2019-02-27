package com.incuube.receiver.messagehandlers.api;

import com.incuube.rcs.datamodel.messages.income.UserMessage;

public interface MessageHandler {

    void handleMessage(UserMessage message);
}
