package com.incuube.receiver.messagefactory.messagecreator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incuube.rcs.datamodel.messages.income.TextUserMessage;
import com.incuube.rcs.datamodel.messages.income.UserMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Log4j2
public class TextMessageCreator implements MessageCreator {
    private ObjectMapper mapper;

    @Autowired
    public TextMessageCreator(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean support(String json) {
        try {
            JsonNode jsonNode = mapper.readTree(json);
            return jsonNode.get("text") != null && jsonNode.get("text").isTextual();

        } catch (IOException e) {
            log.error("TextMessage parsing exception.", e);
            return false;
        }
    }

    @Override
    public UserMessage parseMessage(String json) throws IOException {
        return mapper.readValue(json, TextUserMessage.class);
    }
}
