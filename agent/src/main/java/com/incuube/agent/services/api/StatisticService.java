package com.incuube.agent.services.api;

import com.incuube.rcs.datamodel.exceptions.RbmConnectionException;

import java.io.ByteArrayInputStream;
import java.io.IOException;


public interface StatisticService {

    ByteArrayInputStream getAllStatistic() throws IOException, RbmConnectionException;

    String getFileName();

    void deleteWithRange(long from, long to) throws RbmConnectionException;
}
