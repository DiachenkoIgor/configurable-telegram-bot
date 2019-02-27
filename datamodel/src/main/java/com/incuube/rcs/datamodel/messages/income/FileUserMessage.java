package com.incuube.rcs.datamodel.messages.income;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.incuube.rcs.datamodel.messages.util.UserFile;
import com.incuube.rcs.datamodel.util.JsonConverter;
import lombok.Data;

import java.util.Optional;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class FileUserMessage extends UserMessage {

    private UserFile userFile;


    @Override
    public Item getCreateModelObjectForDB() {

        Item item = super.getCreateModelObjectForDB();

        Optional<String> json = JsonConverter.convertObject(this.userFile);

        json.ifPresent(s1 -> item.withJSON("userFile", s1));

        return item;
    }

    @Override
    public UpdateItemSpec getUpdateModelObjectForDB() {
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("messageId", super.getMessageId());

        Optional<String> json = JsonConverter.convertObject(this.userFile);

        json.ifPresent(value -> updateItemSpec.withUpdateExpression("set userFile=:u")
                .withValueMap(new ValueMap()
                        .withJSON(":u", value)));

        return updateItemSpec;
    }
}
