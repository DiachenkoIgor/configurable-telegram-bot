package com.incuube.rcs.datamodel.rest;

import com.incuube.rcs.datamodel.util.ValidationConstants;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class RcsTextMessage {

    @NotNull
    @Pattern(message = ValidationConstants.PHONE_REGEX_MESSAGE, regexp = ValidationConstants.PHONE_REGEX)
    private String number;
    @NotNull
    @Size(max = ValidationConstants.TEXT_FIELD_MAX_LENGTH, message = ValidationConstants.TEXT_FIELD_MAX_MESSAGE)
    private String text;

}
