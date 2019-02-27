package com.incuube.rcs.datamodel.messages.income;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.incuube.rcs.datamodel.util.TimeIncomeMessageDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class UserMessage {

    @JsonProperty("senderPhoneNumber")
    private String number;

    private String messageId;

    @JsonDeserialize(using = TimeIncomeMessageDeserializer.class)
    private long sendTime;


    public Item getCreateModelObjectForDB() {

        return new Item()
                .withPrimaryKey("messageId", this.messageId)
                .withString("senderPhoneNumber", this.number)
                .withLong("sendTime", this.sendTime);
    }

    public abstract UpdateItemSpec getUpdateModelObjectForDB();
}
