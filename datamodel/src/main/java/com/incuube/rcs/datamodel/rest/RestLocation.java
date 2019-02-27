package com.incuube.rcs.datamodel.rest;

import com.incuube.rcs.datamodel.util.ValidationConstants;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

//TODO(igordiachenko): textMaps(label) size
@Data
public class RestLocation {
    @NotNull
    @Min(value = ValidationConstants.LATITUDE_FIELD_MIN_VALUE, message = ValidationConstants.LATITUDE_FIELD_MIN_MESSAGE)
    @Max(value = ValidationConstants.LATITUDE_FIELD_MAX_VALUE, message = ValidationConstants.LATITUDE_FIELD_MAX_MESSAGE)
    private double latitude;
    @NotNull
    @Min(value = ValidationConstants.LONGITUDE_FIELD_MIN_VALUE, message = ValidationConstants.LONGITUDE_FIELD_MIN_MESSAGE)
    @Max(value = ValidationConstants.LONGITUDE_FIELD_MAX_VALUE, message = ValidationConstants.LONGITUDE_FIELD_MAX_MESSAGE)
    private double longitude;
    @NotNull
    private String textMaps;

}
