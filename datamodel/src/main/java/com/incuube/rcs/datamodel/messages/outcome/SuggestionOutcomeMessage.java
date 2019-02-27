package com.incuube.rcs.datamodel.messages.outcome;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.incuube.rcs.datamodel.rest.RcsSuggestionMessage;
import com.incuube.rcs.datamodel.util.JsonConverter;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class SuggestionOutcomeMessage extends OutcomeMessage {

    private List<RcsSuggestionMessage> rcsSuggestionMessages;

    @Override
    public UpdateItemSpec getUpdateModelObjectForDB() {

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("messageId", super.getMessageId());

        if (this.rcsSuggestionMessages != null && !this.rcsSuggestionMessages.isEmpty()) {
            Optional<String> json = JsonConverter.convertObject(rcsSuggestionMessages);

            json.ifPresent(value -> updateItemSpec.withUpdateExpression("set sendTime=:time, sender=:sender, suggestionReplies=:buttonText")
                    .withValueMap(new ValueMap().withLong("time", super.getSendTime())
                            .withString("sender", super.getSender())
                            .withJSON("buttonText",
                                    value)));
        }
        return updateItemSpec;
    }

    @Override
    public Item getCreateModelObjectForDB() {
        Item item = super.getCreateModelObjectForDB();

        if (this.rcsSuggestionMessages != null && !this.rcsSuggestionMessages.isEmpty()) {
            Optional<String> json = JsonConverter.convertObject(rcsSuggestionMessages);

            json.ifPresent(value -> item.withJSON("suggestionReplies",
                    value));
        }

        return item;
    }


}
