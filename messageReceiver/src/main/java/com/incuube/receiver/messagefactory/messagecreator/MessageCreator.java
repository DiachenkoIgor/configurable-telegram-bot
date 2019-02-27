package com.incuube.receiver.messagefactory.messagecreator;


import com.incuube.rcs.datamodel.messages.income.UserMessage;

import java.io.IOException;

public interface MessageCreator {

    boolean support(String json);

    UserMessage parseMessage(String json) throws IOException;
}
