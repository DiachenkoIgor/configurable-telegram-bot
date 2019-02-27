package com.incuube.receiver.messagefactory;


import com.incuube.rcs.datamodel.messages.income.UserMessage;
import com.incuube.receiver.messagefactory.messagecreator.MessageCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserMessageFactory {

    private List<MessageCreator> creators;

    @Autowired
    public UserMessageFactory(MessageCreator[] creators) {
        this.creators = new ArrayList<>(Arrays.asList(creators));
    }

    public Optional<UserMessage> createMessage(String json) throws IOException {

        for (MessageCreator creator : creators) {
            if (creator.support(json)) {
                return Optional.of(creator.parseMessage(json));
            }
        }

        return Optional.empty();
    }
}
