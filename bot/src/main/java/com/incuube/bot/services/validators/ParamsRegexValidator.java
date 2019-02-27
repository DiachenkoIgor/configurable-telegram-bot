package com.incuube.bot.services.validators;

import com.incuube.bot.model.common.Action;
import com.incuube.bot.model.exceptions.ValidationException;
import com.incuube.bot.model.income.IncomeMessage;
import com.incuube.bot.model.income.util.Messengers;
import com.incuube.bot.util.ParamsExtractor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class ParamsRegexValidator implements Validator {

    @Override
    public void checkMessageAndAction(Action action, IncomeMessage incomeMessage) {
        if (incomeMessage.getMessenger() == Messengers.RCS) {
            Optional<Object> regexValidator = ParamsExtractor.getParamFromMap(action.getParams(), "RcsRegexValidator");
            regexValidator.ifPresent(o -> rcsParamRegexValidation((Map<String, Object>) o, incomeMessage.getParams(), action));
        }
    }

    private void rcsParamRegexValidation(Map<String, Object> validatorParams, Map<String, Object> params, Action action) {
        for (Map.Entry<String, Object> entry : validatorParams.entrySet()) {
            Optional<String> paramFromMap = ParamsExtractor.getParamFromMap(params, entry.getKey());
            if (!paramFromMap.isPresent()) {
                throw validationException(entry.getKey(), "Required parameter is missing - ");
            }
            String paramValue = paramFromMap.get();
            if (!paramValue.matches((String) entry.getValue())) {
                throw validationException(entry.getKey(), "Parameter is not valid - ");
            }
        }

    }

    private ValidationException validationException(String param, String message) {
        ValidationException validationException = new ValidationException();
        validationException.setMessage(message + param);
        return validationException;
    }
}
