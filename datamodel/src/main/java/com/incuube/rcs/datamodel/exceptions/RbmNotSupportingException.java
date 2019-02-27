package com.incuube.rcs.datamodel.exceptions;

import lombok.Data;

@Data
public class RbmNotSupportingException extends Exception {

    private String message;

    private int code;

}
