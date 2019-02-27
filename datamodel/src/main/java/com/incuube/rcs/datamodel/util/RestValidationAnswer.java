package com.incuube.rcs.datamodel.util;

import lombok.Data;


@Data
public class RestValidationAnswer {
    private int code;

    private String errorMessages;
}
