package com.incuube.rcs.datamodel.messages.income;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import com.incuube.rcs.datamodel.messages.util.EventType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class EventUserMessage extends UserMessage {

    private EventType eventType;

    private String eventId;

    @Override
    public Item getCreateModelObjectForDB() {

        Item item = new Item()
                .withPrimaryKey("messageId", super.getMessageId())
                .withString("senderPhoneNumber", super.getNumber());

        String name = eventType.getDbName();

        item.withMap(name, prepareData());

        return item;
    }

    public UpdateItemSpec getUpdateModelObjectForDB() {

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("messageId", super.getMessageId());

        String name = eventType.getDbName();

        updateItemSpec.withUpdateExpression("set " + name + "=:r")
                .withValueMap(new ValueMap()
                        .withMap(":r", prepareData()));

        return updateItemSpec;
    }

    private Map<String, Object> prepareData() {

        Map<String, Object> data = new HashMap<>();
        data.put("eventType", this.getEventType().getGoogleName());
        data.put("eventId", this.eventId);
        data.put("sendTime", super.getSendTime());

        return data;
    }
}
