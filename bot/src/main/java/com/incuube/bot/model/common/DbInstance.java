package com.incuube.bot.model.common;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;

public interface DbInstance {
    Item getCreateModelObjectForDB();

    UpdateItemSpec getUpdateModelObjectForDB();
}
