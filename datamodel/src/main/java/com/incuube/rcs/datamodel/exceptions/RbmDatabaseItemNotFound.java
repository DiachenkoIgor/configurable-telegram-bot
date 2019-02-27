package com.incuube.rcs.datamodel.exceptions;

import lombok.Data;

@Data
public class RbmDatabaseItemNotFound extends Exception {

    private String itemName;

    private String itemType;

}
