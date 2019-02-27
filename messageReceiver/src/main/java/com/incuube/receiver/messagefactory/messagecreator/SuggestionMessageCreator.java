package com.incuube.receiver.messagefactory.messagecreator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incuube.rcs.datamodel.messages.income.SuggestionUserMessage;
import com.incuube.rcs.datamodel.messages.income.UserMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Log4j2
public class SuggestionMessageCreator implements MessageCreator {
    private ObjectMapper mapper;

    @Autowired
    public SuggestionMessageCreator(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean support(String json) {
        try {
            JsonNode jsonNode = mapper.readTree(json);
            return jsonNode.get("suggestionResponse") != null && jsonNode.get("suggestionResponse").isObject();

        } catch (IOException e) {
            log.error("SuggestionMessage parsing exception.", e);
            return false;
        }

    }

    @Override
    public UserMessage parseMessage(String json) throws IOException {
        return mapper.readValue(json, SuggestionUserMessage.class);
    }
}
