package com.incuube.receiver.messagefactory.messagecreator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incuube.rcs.datamodel.messages.income.LocationUserMessage;
import com.incuube.rcs.datamodel.messages.income.UserMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Log4j2
public class LocationMessageCreator implements MessageCreator {

    private final ObjectMapper mapper;

    @Autowired
    public LocationMessageCreator(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean support(String json) {
        try {
            JsonNode jsonNode = mapper.readTree(json);
            return jsonNode.get("location") != null && jsonNode.get("location").isObject();
        } catch (IOException e) {
            log.error("LocationMessage parsing exception.", e);
            return false;
        }
    }

    @Override
    public UserMessage parseMessage(String json) throws IOException {
        return mapper.readValue(json, LocationUserMessage.class);
    }
}
