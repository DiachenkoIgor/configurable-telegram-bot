package com.incuube.agent.repositories.api;

import com.incuube.rcs.datamodel.statistic.StatisticMessage;

import java.util.List;

public interface StatisticMessageRepository {
    List<StatisticMessage> allStatistic() throws Exception;

    void deleteRange(long from, long to) throws Exception;
}
