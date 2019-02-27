package com.incuube.rcs.datamodel.messages.outcome;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class FileOutcomeMessage extends OutcomeMessage {

    private String fileName;


    @Override
    public UpdateItemSpec getUpdateModelObjectForDB() {
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("messageId", super.getMessageId());

        updateItemSpec.withUpdateExpression("set sendTime=:time, sender=:sender, fileName=:name")
                .withValueMap(new ValueMap().withLong("time", super.getSendTime())
                        .withString("sender", super.getSender())
                        .withString("name", this.fileName));

        return updateItemSpec;
    }

    @Override
    public Item getCreateModelObjectForDB() {
        Item item = super.getCreateModelObjectForDB();

        if (this.fileName != null) {
            item.with("fileName", fileName);
        }

        return item;
    }


}
