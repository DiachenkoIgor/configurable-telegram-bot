package com.incuube.rcs.datamodel.messages.util;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum EventType {

    DELIVERED("DELIVERED", "deliveredEvent"),
    READ("READ", "readEvent");

    private static Map<String, EventType> events = Stream.of(values()).collect(Collectors.toMap(EventType::getGoogleName, n -> n));

    private String googleName;

    private String dbName;



    EventType(String googleName, String dbName) {
        this.googleName = googleName;
        this.dbName = dbName;
    }

    @JsonCreator
    public EventType getByGoogleName(String googleName) {
        return events.get(googleName);
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, EventType> stringEventTypeEntry : events.entrySet()) {
            if (stringEventTypeEntry.getValue() == this) {
                return stringEventTypeEntry.getKey();
            }
        }
        return null;
    }
}
