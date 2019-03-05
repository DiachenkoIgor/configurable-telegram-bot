package com.incuube.bot.database.util;

import org.bson.json.JsonWriterSettings;

public class MongoDbConverterSettings {
    public static JsonWriterSettings jsonWriterSettingsForLongField() {
        return JsonWriterSettings.builder()
                .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
                .build();
    }
}
