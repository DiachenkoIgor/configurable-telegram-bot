package com.incuube.rcs.datamodel.messages.outcome;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;


@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class TextOutcomeMessage extends OutcomeMessage {

    private String text;


    @Override
    public Item getCreateModelObjectForDB() {

        Item item = super.getCreateModelObjectForDB();

        if (text != null) {
            item.withString("text", text);
        }

        return item;
    }

    @Override
    public UpdateItemSpec getUpdateModelObjectForDB() {

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("messageId", super.getMessageId());

        updateItemSpec.withUpdateExpression("set sendTime=:time, sender=:sender, text=:text")
                .withValueMap(new ValueMap().withLong("time", super.getSendTime())
                        .withString("sender", super.getSender())
                        .withString("text", this.getText()));

        return updateItemSpec;
    }
}
