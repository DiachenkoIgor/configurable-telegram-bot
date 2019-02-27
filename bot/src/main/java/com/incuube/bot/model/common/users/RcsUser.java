package com.incuube.bot.model.common.users;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class RcsUser extends User {
    @JsonProperty("number_id")
    private String number;


    public Item getCreateModelObjectForDB() {
        Item item = new Item();

        item.withPrimaryKey("number_id", this.number);

        if (this.getCurrentAction() != null) {
            item.withString("currentAction", this.getCurrentAction());
        }

        item.withLong("lastActionTime", Timestamp.valueOf(this.getLastActionTime()).getTime())
                .withString("type", "rcs_user")
                .withString("messenger", super.getMessenger().toValue())
                .withMap("params", super.getParams());

        return item;
    }

    @Override
    public UpdateItemSpec getUpdateModelObjectForDB() {
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("number_id", this.number);

        String updateExpression = "SET lastActionTime=:lastT";

        ValueMap valueMap = new ValueMap();
        valueMap.withLong(":lastT", Timestamp.valueOf(this.getLastActionTime()).getTime());

        if (super.getCurrentAction() != null) {
            updateExpression += ", currentAction=:currentId";
            valueMap.withString(":currentId", super.getCurrentAction());
        }


        updateItemSpec.withUpdateExpression(updateExpression)
                .withValueMap(valueMap);

        return updateItemSpec;
    }
}
