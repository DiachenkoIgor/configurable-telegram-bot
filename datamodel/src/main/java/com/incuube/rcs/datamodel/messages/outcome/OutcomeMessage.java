package com.incuube.rcs.datamodel.messages.outcome;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.incuube.rcs.datamodel.util.TimeIncomeMessageDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class OutcomeMessage {

    private String messageId;
    @JsonDeserialize(using = TimeIncomeMessageDeserializer.class)
    private long sendTime;

    private String senderPhoneNumber;

    private String sender;

    public abstract UpdateItemSpec getUpdateModelObjectForDB();


    public Item getCreateModelObjectForDB() {
        Item item = new Item();

        item.withPrimaryKey("messageId", this.messageId)
                .withString("senderPhoneNumber", this.senderPhoneNumber)
                .withString("sender", this.sender)
                .withLong("sendTime", this.sendTime);
        return item;
    }


}
