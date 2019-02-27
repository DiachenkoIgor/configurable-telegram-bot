package com.incuube.bot.model.income;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class IncomeSuggestionMessage extends IncomeMessage {

    private String postbackData;

    private String buttonText;
}
