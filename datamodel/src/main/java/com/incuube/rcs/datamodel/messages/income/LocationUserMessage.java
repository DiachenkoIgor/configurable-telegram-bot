package com.incuube.rcs.datamodel.messages.income;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.incuube.rcs.datamodel.messages.util.Location;
import com.incuube.rcs.datamodel.util.JsonConverter;
import lombok.Data;

import java.util.Optional;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class LocationUserMessage extends UserMessage {


    private Location location;

    @Override
    public Item getCreateModelObjectForDB() {

        Item item = super.getCreateModelObjectForDB();

        if (this.location != null) {
            Optional<String> result = JsonConverter.convertObject(this.location);
            result.ifPresent(s -> item.withJSON("location", s));
        }

        return item;
    }

    @Override
    public UpdateItemSpec getUpdateModelObjectForDB() {

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("messageId", super.getMessageId());

        Optional<String> json = JsonConverter.convertObject(this.location);

        json.ifPresent(value -> updateItemSpec.withUpdateExpression("set location=:l")
                .withValueMap(new ValueMap()
                        .withJSON(":l", value)));

        return updateItemSpec;
    }
}


