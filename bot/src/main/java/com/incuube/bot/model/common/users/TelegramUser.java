package com.incuube.bot.model.common.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.Document;

import java.sql.Timestamp;

@Data
public class TelegramUser extends User {
    @JsonProperty("_id")
    private String id;

    private String first_name;

    private String last_name;

    private String username;

    @Override
    public Document getCreateModelObjectForDB() {
        Document document = new Document("_id", this.id);

        if (this.getCurrentAction() != null) {
            document.append("currentAction", this.getCurrentAction());
        }

        document.append("lastActionTime", Timestamp.valueOf(this.getLastActionTime()).getTime())
                .append("type", "telegram_user")
                .append("messenger", super.getMessenger().toValue())
                .append("params", super.getParams());

        if (this.first_name != null) {
            document.append("first_name", this.first_name);
        }
        if (this.last_name != null) {
            document.append("last_name", this.last_name);
        }
        if (this.username != null) {
            document.append("username", this.username);
        }
        return document;
    }

    @Override
    public Document getUpdateModelObjectForDB() {
        Document document = new Document("_id", this.id);

        Document update = new Document();

        update.append("lastActionTime", Timestamp.valueOf(this.getLastActionTime()).getTime());

        if (super.getCurrentAction() != null) {
            update.append("currentAction", super.getCurrentAction());
        }

        if (this.first_name != null) {
            update.append("first_name", this.first_name);
        }
        if (this.last_name != null) {
            update.append("last_name", this.last_name);
        }
        if (this.username != null) {
            update.append("username", this.username);
        }

        document.append("$set", update);

        return document;
    }
}
