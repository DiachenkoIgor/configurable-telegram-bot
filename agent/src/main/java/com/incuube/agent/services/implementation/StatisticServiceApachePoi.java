package com.incuube.agent.services.implementation;

import com.incuube.agent.repositories.api.StatisticMessageRepository;
import com.incuube.agent.services.api.StatisticService;
import com.incuube.agent.util.StatisticColumnsConstants;
import com.incuube.rcs.datamodel.exceptions.RbmConnectionException;
import com.incuube.rcs.datamodel.statistic.StatisticMessage;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

//TODO(igordiachenko): Think about table for statistic
@Component
@Log4j2
public class StatisticServiceApachePoi implements StatisticService {

    private StatisticMessageRepository statisticMessageRepository;

    private String fileName = "statistic-";

    private final String extension = ".xlsx";

    @Autowired
    public StatisticServiceApachePoi(StatisticMessageRepository statisticMessageRepository) {
        this.statisticMessageRepository = statisticMessageRepository;
    }


    @Override
    public ByteArrayInputStream getAllStatistic() throws RbmConnectionException {
        try {
            List<StatisticMessage> messages = statisticMessageRepository.allStatistic();
            log.info("Received statistic messages. Quantity - " + messages.size());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Workbook wb = new XSSFWorkbook();
            if (!messages.isEmpty()) {
                Sheet agentSheet = wb.createSheet("Statistic");
                Row agentStartRow = agentSheet.createRow(0);
                prepareAgentSheet(agentStartRow);

                int agentCounter = 1;

                for (StatisticMessage message : messages) {
                    fillAgentRow(agentSheet.createRow(agentCounter++), message);
                }
            }
            wb.write(out);
            wb.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception ex) {
            log.error("Error with creation statistic." + ex.getMessage());
            RbmConnectionException connectionException = new RbmConnectionException();
            connectionException.setMessage(ex.getMessage());
            connectionException.initCause(ex);
            throw connectionException;
        }

    }

    @Override
    public String getFileName() {
        return fileName + LocalDate.now().toString() + extension;
    }

    @Override
    public void deleteWithRange(long from, long to) throws RbmConnectionException {
        try {
            statisticMessageRepository.deleteRange(from, to);
            log.info(String.format("Statistics with range from %d to %d were deleted.", from, to));
        } catch (Exception e) {
            log.error("Error with deletion messages." + e.getMessage());
            RbmConnectionException connectionException = new RbmConnectionException();
            connectionException.setMessage(e.getMessage());
            connectionException.initCause(e);
            throw connectionException;
        }
    }

    private void prepareAgentSheet(Row startRow) {
        startRow.createCell(0).setCellValue(StatisticColumnsConstants.PHONE_NUMBER_COLUMN);
        startRow.createCell(1).setCellValue(StatisticColumnsConstants.MESSAGE_ID_COLUMN);
        startRow.createCell(2).setCellValue(StatisticColumnsConstants.DELIVERY_TIME_COLUMN);
        startRow.createCell(3).setCellValue(StatisticColumnsConstants.READ_TIME_COLUMN);
        startRow.createCell(4).setCellValue(StatisticColumnsConstants.DELIVERD_STATUS_COLUMN);
        startRow.createCell(5).setCellValue(StatisticColumnsConstants.READ_STATUS_COLUMN);
        startRow.createCell(6).setCellValue(StatisticColumnsConstants.SENDER_COLUMN);
    }

    private void fillAgentRow(Row row, StatisticMessage message) {
        row.createCell(0).setCellValue(convertPhoneNumber(message.getPhoneNumber()));
        row.createCell(1).setCellValue(message.getMessageId());
        row.createCell(2).setCellValue(message.getDeliveryTime());
        row.createCell(3).setCellValue(message.getReadTime());
        row.createCell(4).setCellValue(message.isDelivered());
        row.createCell(5).setCellValue(message.isRead());
        row.createCell(6).setCellValue(message.getSender());
    }

    private String convertPhoneNumber(String number) {
        return number.substring(1);
    }

}
