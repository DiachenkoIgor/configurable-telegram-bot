package com.incuube.bot.model.income;

import com.incuube.bot.model.common.IncomeType;
import com.incuube.bot.model.income.util.Messengers;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public abstract class IncomeMessage {

    private Map<String, Object> params = new HashMap<>();
    private String userId;
    private Messengers messenger;
    private IncomeType incomeType;

}
