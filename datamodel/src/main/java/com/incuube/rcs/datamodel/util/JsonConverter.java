package com.incuube.rcs.datamodel.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

@Log4j2
public class JsonConverter {
    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    }

    public static Optional<String> convertObject(Object object) {
        try {
            return Optional.of(
                    MAPPER.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            log.error("Error serialization object for saving outcome logMessage." + e.getMessage());
            return Optional.empty();
        }
    }
}
