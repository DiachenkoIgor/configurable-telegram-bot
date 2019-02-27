package com.incuube.rcs.datamodel.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.incuube.rcs.datamodel.util.ValidationConstants;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RcsSuggestionActionMessage {
    @NotNull
    @Size(max = ValidationConstants.POSTBACK_FIELD_MAX_LENGTH, message = ValidationConstants.POSTBACK_FIELD_MAX_MESSAGE)
    private String postbackData;
    @NotNull
    @Size(max = ValidationConstants.BUTTON_TEXT_FIELD_MAX_LENGTH, message = ValidationConstants.BUTTON_TEXT_FIELD_MAX_MESSAGE)
    private String buttonText;

    @Valid
    private RestCalendar calendar;
    @Valid
    private RestDialAction dialAction;
    @Valid
    private RestLocation location;
    @Valid
    private RestUrlAction urlAction;

}
