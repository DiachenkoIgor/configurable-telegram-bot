package com.incuube.bot.model.outcome;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OutcomeSuggestionMessage.class, name = "suggestion"),
        @JsonSubTypes.Type(value = OutcomeTextMessage.class, name = "text")
})
@Data
public abstract class OutcomeMessage {
    @JsonAnySetter
    private Map<String, Object> params = new HashMap<>();
}

