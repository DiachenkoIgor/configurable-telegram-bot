package com.incuube.bot.model.common;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.incuube.bot.model.common.util.DbInstance;
import com.incuube.bot.model.outcome.OutcomeMessage;
import com.incuube.bot.util.JsonConverter;
import lombok.Data;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Action implements DbInstance {

    private String id;
    @JsonAnySetter
    private Map<String, Object> params = new HashMap<>();
    private OutcomeMessage outcomeMessage;
    private IncomeType expectedIncomeType;
    private String nextActionId;
    private String errorActionId;

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("_id")
    public void setIdForMongo(String id) {
        this.id = id;
    }

    public Document getCreateModelObjectForDB() {
        Document document = new Document("_id", this.id);

        if (this.getNextActionId() != null) {
            document.append("nextActionId", this.nextActionId);
        }
        if (this.getErrorActionId() != null) {
            document.append("errorActionId", this.errorActionId);
        }
        if (this.getOutcomeMessage() != null) {
            Optional<String> s = JsonConverter.convertObject(outcomeMessage);
            s.ifPresent(value -> document.append("outcomeMessage", Document.parse(value)));
        }
        document.append("params", this.params);

        if (this.expectedIncomeType != null) {
            document.append("expectedIncomeType", expectedIncomeType.toValue());
        }

        return document;
    }

    @Override
    public Document getUpdateModelObjectForDB() {
        throw new UnsupportedOperationException("There is no realization for update in Action class.");
    }
}
