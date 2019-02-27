package com.incuube.rcs.datamodel.rest;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RestUrlAction {
    @NotNull
    private String url;
}
