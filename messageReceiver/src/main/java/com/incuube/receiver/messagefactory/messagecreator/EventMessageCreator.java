package com.incuube.receiver.messagefactory.messagecreator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incuube.rcs.datamodel.messages.income.EventUserMessage;
import com.incuube.rcs.datamodel.messages.income.UserMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@Log4j2
public class EventMessageCreator implements MessageCreator {

    private final ObjectMapper mapper;

    public EventMessageCreator(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    @Override
    public boolean support(String json) {
        try {
            JsonNode jsonNode = mapper.readTree(json);
            return jsonNode.get("eventType") != null && jsonNode.get("eventType").isTextual() && !jsonNode.get("eventType").asText().equals("IS_TYPING");

        } catch (IOException e) {
            log.error("EventMessage parsing exception.", e);
            return false;
        }
    }

    @Override
    public UserMessage parseMessage(String json) throws IOException {
        return mapper.readValue(json, EventUserMessage.class);
    }
}
