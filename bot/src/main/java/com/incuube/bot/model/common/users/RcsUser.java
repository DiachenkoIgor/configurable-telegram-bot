package com.incuube.bot.model.common.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.Document;

import java.sql.Timestamp;

@Data
public class RcsUser extends User {
    @JsonProperty("_id")
    private String number;


    public Document getCreateModelObjectForDB() {
        Document document = new Document("_id", this.number);

        if (this.getCurrentAction() != null) {
            document.append("currentAction", this.getCurrentAction());
        }

        document.append("lastActionTime", Timestamp.valueOf(this.getLastActionTime()).getTime())
                .append("type", "rcs_user")
                .append("messenger", super.getMessenger().toValue())
                .append("params", super.getParams());

        return document;
    }

    @Override
    public Document getUpdateModelObjectForDB() {
        Document document = new Document("_id", this.number);

        Document actionTime = new Document("lastActionTime", Timestamp.valueOf(this.getLastActionTime()).getTime());
        document.append("$set", actionTime);

        if (super.getCurrentAction() != null) {
            Document currentAction = new Document("currentAction", super.getCurrentAction());
            document.append("$set", currentAction);
        }

        return document;
    }
}
