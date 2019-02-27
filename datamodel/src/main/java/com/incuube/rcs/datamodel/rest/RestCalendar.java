package com.incuube.rcs.datamodel.rest;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RestCalendar {
    @NotNull
    private String startTime;
    @NotNull
    private String endTime;
    @NotNull
    private String title;
    @NotNull
    private String description;

}
