package com.incuube.rcs.datamodel.messages.income;


import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class TextUserMessage extends UserMessage {

    private String text;

    @Override
    public Item getCreateModelObjectForDB() {
        Item item = super.getCreateModelObjectForDB();

        if (text != null) {
            item.withString("userText", text);
        }

        return item;
    }

    @Override
    public UpdateItemSpec getUpdateModelObjectForDB() {

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("messageId", super.getMessageId());

        updateItemSpec.withUpdateExpression("set userText=:r")
                .withValueMap(new ValueMap()
                        .withString(":r", this.text));

        return updateItemSpec;
    }
}
