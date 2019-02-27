package com.incuube.bot.model.common.util;

import com.incuube.bot.model.income.util.Messengers;
import lombok.Data;

@Data
public class DbSaverEntity {
    private String paramValue;
    private String table;
    private String idName;
    private String idValue;
    private String field;
    private Messengers messengers;
    private String errorMessage;
}
