package com.incuube.rcs.datamodel.exceptions;

import lombok.Data;

@Data
public class RbmConnectionException extends Exception {

    private String message;


}
