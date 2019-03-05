package com.incuube.rcs.datamodel.messages.outcome;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.incuube.rcs.datamodel.rest.RcsSuggestionActionMessage;
import com.incuube.rcs.datamodel.util.JsonConverter;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class SuggestionActionOutcomeMessage extends OutcomeMessage {

    private List<RcsSuggestionActionMessage> rcsSuggestionActionMessages;

    @Override
    public UpdateItemSpec getUpdateModelObjectForDB() {

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("messageId", super.getMessageId());

        if (this.rcsSuggestionActionMessages != null && !rcsSuggestionActionMessages.isEmpty()) {

            Optional<String> json = JsonConverter.convertObject(this.rcsSuggestionActionMessages);

            json.ifPresent(value -> updateItemSpec.withUpdateExpression("set sendTime=:time, sender=:sender, suggestionActions=:actions")
                    .withValueMap(new ValueMap().withLong("time", super.getSendTime())
                            .withString("sender", super.getSender())
                            .withJSON("actions",
                                    value)));


        }
        return updateItemSpec;
    }

    @Override
    public Item getCreateModelObjectForDB() {
        Item item = super.getCreateModelObjectForDB();


        if (this.rcsSuggestionActionMessages != null && !rcsSuggestionActionMessages.isEmpty()) {
            Optional<String> json = JsonConverter.convertObject(rcsSuggestionActionMessages);
            json.ifPresent(value ->
                    item.withJSON("suggestionActions", value));
        }
        return item;
    }

}