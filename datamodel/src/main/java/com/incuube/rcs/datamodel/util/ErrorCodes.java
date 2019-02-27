package com.incuube.rcs.datamodel.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    ValidationError(666, "666");

    private int errorCode;

    private String errorDescription;
}
