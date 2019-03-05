package com.incuube.bot.model.common.users;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.incuube.bot.model.common.util.DbInstance;
import com.incuube.bot.model.income.util.Messengers;
import com.incuube.bot.util.DateUserDeserializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RcsUser.class, name = "rcs_user"),
        @JsonSubTypes.Type(value = TelegramUser.class, name = "telegram_user")
})
@Data
public abstract class User implements DbInstance {
    private String currentAction;
    @JsonDeserialize(using = DateUserDeserializer.class)
    private LocalDateTime lastActionTime;
    @JsonAnySetter
    private Map<String, Object> params = new HashMap<>();
    private Messengers messenger;

}
