package com.incuube.bot.model.income;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class IncomeTextMessage extends IncomeMessage {
    private String text;
}
