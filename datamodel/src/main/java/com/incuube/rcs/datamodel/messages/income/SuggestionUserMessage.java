package com.incuube.rcs.datamodel.messages.income;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.incuube.rcs.datamodel.messages.util.SuggestionResponse;
import com.incuube.rcs.datamodel.util.JsonConverter;
import lombok.Data;

import java.util.Optional;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class SuggestionUserMessage extends UserMessage {

    private SuggestionResponse suggestionResponse;


    @Override
    public Item getCreateModelObjectForDB() {

        Item item = super.getCreateModelObjectForDB();

        if (this.suggestionResponse != null) {
            Optional<String> result = JsonConverter.convertObject(this.suggestionResponse);
            result.ifPresent(s -> item.withJSON("suggestionResponse", s));
        }

        return item;
    }

    @Override
    public UpdateItemSpec getUpdateModelObjectForDB() {

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("messageId", super.getMessageId());

        Optional<String> json = JsonConverter.convertObject(this.suggestionResponse);

        json.ifPresent(value -> updateItemSpec.withUpdateExpression("set suggestionResponse=:s")
                .withValueMap(new ValueMap()
                        .withJSON(":s", value)));

        return updateItemSpec;
    }
}

