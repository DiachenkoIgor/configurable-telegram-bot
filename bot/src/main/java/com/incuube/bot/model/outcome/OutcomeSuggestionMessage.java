package com.incuube.bot.model.outcome;

import com.incuube.bot.model.common.Button;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OutcomeSuggestionMessage extends OutcomeMessage {
    private List<Button> buttons = new ArrayList<>();
    private String text;
}
