package com.incuube.bot.model.common;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Button {
    private String nextActionId;
    private String buttonText;
    @JsonAnySetter
    private Map<String, Object> params = new HashMap<>();
}
