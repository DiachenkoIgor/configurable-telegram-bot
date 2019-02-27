package com.incuube.bot.model.common.util;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;

public interface DbInstance {
    Item getCreateModelObjectForDB();

    UpdateItemSpec getUpdateModelObjectForDB();
}
