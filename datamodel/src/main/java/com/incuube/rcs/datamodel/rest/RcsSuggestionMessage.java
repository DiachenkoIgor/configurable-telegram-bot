package com.incuube.rcs.datamodel.rest;

import com.incuube.rcs.datamodel.util.ValidationConstants;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RcsSuggestionMessage {

    @NotNull
    @Size(max = ValidationConstants.POSTBACK_FIELD_MAX_LENGTH, message = ValidationConstants.POSTBACK_FIELD_MAX_MESSAGE)
    private String postbackData;
    @NotNull
    @Size(max = ValidationConstants.BUTTON_TEXT_FIELD_MAX_LENGTH, message = ValidationConstants.BUTTON_TEXT_FIELD_MAX_MESSAGE)
    private String buttonText;

}
