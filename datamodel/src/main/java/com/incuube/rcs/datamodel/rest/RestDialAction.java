package com.incuube.rcs.datamodel.rest;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RestDialAction {
    @NotNull

    private String number;

}
