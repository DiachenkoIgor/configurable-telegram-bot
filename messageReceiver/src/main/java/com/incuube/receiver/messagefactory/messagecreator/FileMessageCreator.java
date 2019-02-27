package com.incuube.receiver.messagefactory.messagecreator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incuube.rcs.datamodel.messages.income.FileUserMessage;
import com.incuube.rcs.datamodel.messages.income.UserMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Log4j2
public class FileMessageCreator implements MessageCreator {

    private final ObjectMapper mapper;

    public FileMessageCreator(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean support(String json) {
        try {
            JsonNode jsonNode = mapper.readTree(json);
            return jsonNode.get("userFile") != null && jsonNode.get("userFile").isObject();

        } catch (IOException e) {
            log.error("FileMessage parsing exception.", e);
            return false;
        }
    }

    @Override
    public UserMessage parseMessage(String json) throws IOException {
        return mapper.readValue(json, FileUserMessage.class);
    }
}
