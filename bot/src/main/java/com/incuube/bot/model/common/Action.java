package com.incuube.bot.model.common;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.incuube.bot.model.outcome.OutcomeMessage;
import com.incuube.bot.util.JsonConverter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Action {
    private String id;
    @JsonAnySetter
    private Map<String, Object> params = new HashMap<>();
    private OutcomeMessage outcomeMessage;
    private IncomeType expectedIncomeType;
    private String nextActionId;
    private String errorActionId;

    public Item getCreateModelObjectForDB() {
        Item item = new Item();

        item
                .withPrimaryKey("id", this.id);
        if (this.getNextActionId() != null) {
            item.withString("nextActionId", this.nextActionId);
        }
        if (this.getErrorActionId() != null) {
            item.withString("errorActionId", this.errorActionId);
        }
        if (this.getOutcomeMessage() != null) {
            Optional<String> s = JsonConverter.convertObject(outcomeMessage);
            s.ifPresent(value -> item.withJSON("outcomeMessage", value));
        }
        if (this.expectedIncomeType != null) {
            item.withString("expectedIncomeType", expectedIncomeType.toValue());
        }

        item.withMap("params", this.params);

        return item;
    }
}
