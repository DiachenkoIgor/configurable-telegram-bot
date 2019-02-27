package com.incuube.bot.model.income.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum Messengers {
    RCS("RCSUsers");

    private static Map<String, Messengers> types = Stream.of(values()).collect(Collectors.toMap(Enum::name, value -> value));

    private String tableName;

    @JsonCreator
    public static Messengers forValue(String value) {
        return types.get(value);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }

    Messengers(String tableName) {
        this.tableName = tableName;
    }
}
