package com.incuube.rcs.datamodel.rest;

import com.incuube.rcs.datamodel.util.ValidationConstants;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RcsFile {
    @NotNull
    private String url;
    @NotNull
    @Size(max = ValidationConstants.FILE_NAME_FIELD_MAX_LENGTH, message = ValidationConstants.FILE_NAME_FIELD_MAX_MESSAGE)
    private String name;

    private String googleName;

    private String thumbnailUrl;

}
