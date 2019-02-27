package com.incuube.bot.model.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum IncomeType {
    SUGGESTION, TEXT;
    private static Map<String, IncomeType> types = Stream.of(values()).collect(Collectors.toMap(Enum::name, value -> value));

    @JsonCreator
    public static IncomeType forValue(String value) {
        return types.get(value);
    }

    @JsonValue
    public String toValue() {
       return this.name();
    }
}
