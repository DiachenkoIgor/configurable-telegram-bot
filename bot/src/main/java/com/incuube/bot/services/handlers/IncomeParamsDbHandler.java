package com.incuube.bot.services.handlers;

import com.incuube.bot.database.common.ParamDynamoDbRepository;
import com.incuube.bot.model.common.Action;
import com.incuube.bot.model.common.users.User;
import com.incuube.bot.model.common.util.DbSaverEntity;
import com.incuube.bot.model.exceptions.BotDabaseException;
import com.incuube.bot.model.exceptions.DbParamNotFoundException;
import com.incuube.bot.model.income.IncomeMessage;
import com.incuube.bot.services.ActionProcessorFacade;
import com.incuube.bot.services.util.HandlerOrderConstants;
import com.incuube.bot.util.JsonConverter;
import com.incuube.bot.util.ParamsExtractor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log4j2
public class IncomeParamsDbHandler implements IncomeMessageHandler {

    private IncomeMessageHandler nextMessageHandler;

    private ActionProcessorFacade actionProcessorFacade;

    private ParamDynamoDbRepository paramDynamoDbRepository;

    private final Pattern placeHolderPattern = Pattern.compile("\\$\\{(.+?)\\}");

    private final String defaultUserMessage = "U send me wrong type of message. Please try again";

    @Autowired
    public IncomeParamsDbHandler(ActionProcessorFacade actionProcessorFacade, ParamDynamoDbRepository paramDynamoDbRepository) {
        this.actionProcessorFacade = actionProcessorFacade;
        this.paramDynamoDbRepository = paramDynamoDbRepository;
    }

    //Saving params have some rules for Dynamo DB
    @Override
    public void handleMessage(IncomeMessage incomeMessage, User user, Action next) {
        try {
            Optional<Object> dbParams = ParamsExtractor.getParamFromMap(next.getParams(), "db_params");

            dbParams.ifPresent(o -> processSaveToDbAction(incomeMessage, (List<Object>) o, user));

            nextMessageHandler.handleMessage(incomeMessage, user, next);
        } catch (DbParamNotFoundException ex) {
            log.error("Params handler problem. {}", ex.getLogMessage());
            //If parameter is missed, user send me wrong logMessage
            actionProcessorFacade.sendRepeatErrorAction(ex.getUserMessage(), user);
        } catch (BotDabaseException ex) {
            actionProcessorFacade.sendFatalError(incomeMessage.getUserId(), incomeMessage.getMessenger());
        }catch (Exception ex){
            actionProcessorFacade.sendRepeatErrorAction("Что то пошло не так. Пожалуйста повторите действие!", user);
        }
    }

    private void processSaveToDbAction(IncomeMessage incomeMessage, List<Object> dbParams, User user) {
        List<DbSaverEntity> saverEntities = new LinkedList<>();
        for (Object dbParam : dbParams) {
            Optional<String> convertObject = JsonConverter.convertObject(dbParam);
            if (convertObject.isPresent()) {
                if (!checkMessenger(convertObject.get(), incomeMessage)) {
                    continue;
                }
            }
            convertObject.ifPresent(s -> saverEntities.add(prepareDbSaverEntity(incomeMessage, s)));
        }
        saverEntities.forEach(this::saveParamToDb);
        saverEntities.forEach(entity -> this.setParamsToUser(user, entity));
    }

    private void setParamsToUser(User user, DbSaverEntity dbSaverEntity) {
        String field = dbSaverEntity.getField();
        String fieldForSetting = field.substring(field.indexOf('.') + 1);
        String[] path = fieldForSetting.split("\\.");

        Map<String, Object> mapForSetting = user.getParams();
        for (int i = 0; i < path.length; i++) {
            if (i != path.length - 1) {
                Map<String, Object> newMap = new HashMap<>();
                mapForSetting.put(path[i], newMap);
                mapForSetting = newMap;
            } else {
                mapForSetting.put(path[i], dbSaverEntity.getParamValue());
            }
        }
    }

    private boolean checkMessenger(String dbEntity, IncomeMessage incomeMessage) {
        if (dbEntity.contains("\"messenger\"")) {
            Optional<DbSaverEntity> dbSaverEntityOptional = JsonConverter.convertJson(dbEntity, DbSaverEntity.class);
            if (dbSaverEntityOptional.isPresent()) {
                DbSaverEntity dbSaverEntity = dbSaverEntityOptional.get();
                return dbSaverEntity.getMessengers() == incomeMessage.getMessenger();
            }
        }
        return true;
    }

    private void saveParamToDb(DbSaverEntity dbSaverEntity) {
        paramDynamoDbRepository.saveParamToSomeTable(dbSaverEntity);
    }

    private DbSaverEntity prepareDbSaverEntity(IncomeMessage incomeMessage, String json) {
        Matcher matcher = placeHolderPattern.matcher(json);

        while (matcher.find()) {
            String paramName = matcher.group(1);
            if (paramName != null) {
                Optional<String> paramFromMap = ParamsExtractor.getParamFromMap(incomeMessage.getParams(), paramName);
                if (paramFromMap.isPresent()) {
                    String value=paramFromMap.get();
                    if(value.equals("\\")){
                        value="\\\\";
                    }
                    if(value.equals("\"")){
                        value="\\\"";
                    }
                    if(value.contains("\n")){
                        value=value.replace("\n","\\n");
                    }
                    json = json.replace(String.format("${%s}", paramName), value);
                    continue;
                }
                Optional<DbSaverEntity> dbSaverEntityOptional = JsonConverter.convertJson(json, DbSaverEntity.class);
                dbSaverEntityOptional.ifPresent(value -> {
                    throw dbParamNotFoundException(
                            String.format("Param with name %s wasn't found.", paramName), value.getErrorMessage());
                });
                throw dbParamNotFoundException(String.format("Param with name %s wasn't found.", paramName));
            }
        }
        return JsonConverter.convertJson(json, DbSaverEntity.class).get();
    }

    @Override
    public void setNext(IncomeMessageHandler messageHandler) {
        this.nextMessageHandler = messageHandler;
    }

    @Override
    public int getOrder() {
        return HandlerOrderConstants.PARAMS_DB_HANDLER_VALUE;
    }

    private DbParamNotFoundException dbParamNotFoundException(String logMessage) {
        return dbParamNotFoundException(logMessage, defaultUserMessage);
    }

    private DbParamNotFoundException dbParamNotFoundException(String logMessage, String userMessage) {
        DbParamNotFoundException dbParamNotFoundException = new DbParamNotFoundException(logMessage);
        dbParamNotFoundException.setLogMessage(logMessage);
        if (userMessage != null) {
            dbParamNotFoundException.setUserMessage(userMessage);
        } else {
            dbParamNotFoundException.setUserMessage(defaultUserMessage);
        }
        return dbParamNotFoundException;
    }
}

