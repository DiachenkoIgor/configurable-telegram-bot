package com.incuube.agent.controllers;

import com.incuube.agent.services.api.StatisticService;
import com.incuube.rcs.datamodel.exceptions.RbmConnectionException;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

//TODO(igordiachenko): Change for async(many request will shut down application).
@RestController
@RequestMapping("/statistic")
@Log4j2
public class StatisticController {

    private StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> handleAllStatisticRequest() throws IOException, RbmConnectionException {

        log.info("Request for creation all statistic");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + statisticService.getFileName());

        return new ResponseEntity<>(new InputStreamResource(statisticService.getAllStatistic()), headers, HttpStatus.OK);
    }

    @DeleteMapping(value = "/range")
    public ResponseEntity<?> handleAllStatisticRequest(@RequestParam("from") long from,
                                                       @RequestParam("to") long to) throws RbmConnectionException {

        if (from >= to) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong 'from' or 'to' parameter!");
        }
        log.info("Request for deleting statistic");

        statisticService.deleteWithRange(from, to);

        return ResponseEntity.ok().build();
    }

}
