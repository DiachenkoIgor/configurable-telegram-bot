package com.incuube.bot.model.common.util;


import org.bson.Document;

public interface DbInstance {
    Document getCreateModelObjectForDB();

    Document getUpdateModelObjectForDB();

}
