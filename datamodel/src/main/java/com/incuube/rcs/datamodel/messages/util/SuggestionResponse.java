package com.incuube.rcs.datamodel.messages.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;


@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class SuggestionResponse {

    private String postbackData;

    private String text;



}

