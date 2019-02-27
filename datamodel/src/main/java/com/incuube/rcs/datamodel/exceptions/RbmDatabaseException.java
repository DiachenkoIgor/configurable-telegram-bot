package com.incuube.rcs.datamodel.exceptions;

import lombok.Data;

@Data
public class RbmDatabaseException extends Exception {
    private String message;

}
