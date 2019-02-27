package com.incuube.agent.util;

import com.incuube.rcs.datamodel.rest.RcsSuggestionActionMessage;
import com.incuube.rcs.datamodel.rest.RcsSuggestionMessage;
import com.incuube.rcs.datamodel.util.ValidationConstants;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestValidator {
    private static final Pattern PHONE_PATTERN = Pattern.compile(ValidationConstants.PHONE_REGEX);

    private static final Validator BEAN_VALIDATOR;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        BEAN_VALIDATOR = factory.getValidator();
    }

    public static Optional<String> validateRcsSuggestion(List<RcsSuggestionMessage> messages) {
        if (messages.size() > ValidationConstants.SUGGESTIONS_MAX_QUANTITY_VALUE) {
            return Optional.of(ValidationConstants.SUGGESTIONS_MAX_QUANTITY_MESSAGE);
        }
        for (RcsSuggestionMessage rcsSuggestionMessage : messages) {
            if (rcsSuggestionMessage.getButtonText() == null) {
                return Optional.of("'buttonText' field is null!");
            }
            if (rcsSuggestionMessage.getPostbackData() == null) {
                return Optional.of("'postbackData' field is null!");
            }

        }
        return Optional.empty();
    }

    public static Optional<String> validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != ValidationConstants.PHONE_SIZE) {
            return Optional.of("Invalid phone number(null or wrong length or starts with '+')");
        }

        Matcher matcher = PHONE_PATTERN.matcher(phoneNumber);

        if (matcher.find()) {
            return Optional.empty();
        }

        return Optional.of(ValidationConstants.PHONE_REGEX_MESSAGE);
    }

    public static Optional<String> validateRcsSuggestionAction(List<RcsSuggestionActionMessage> messages) {
        if (messages.size() > ValidationConstants.SUGGESTIONS_MAX_QUANTITY_VALUE) {
            return Optional.of(ValidationConstants.SUGGESTIONS_MAX_QUANTITY_MESSAGE);
        }
        for (int i = 0; i < messages.size(); i++) {
            RcsSuggestionActionMessage actionMessage = messages.get(i);

            Set<ConstraintViolation<RcsSuggestionActionMessage>> validate = BEAN_VALIDATOR.validate(actionMessage);
            Iterator<ConstraintViolation<RcsSuggestionActionMessage>> violationIterator = validate.iterator();

            if (violationIterator.hasNext()) {
                ConstraintViolation<RcsSuggestionActionMessage> violation = violationIterator.next();
                return Optional.of("Invalid action Message on position - " + i + ". Field '" + violation.getPropertyPath().toString() + "' " + violation.getMessage());
            }
            int check = 0;

            check += actionMessage.getCalendar() == null ? 0 : 1;
            check += actionMessage.getDialAction() == null ? 0 : 1;
            check += actionMessage.getUrlAction() == null ? 0 : 1;
            check += actionMessage.getLocation() == null ? 0 : 1;

            if (check != 1) {
                return Optional.of("Invalid action Message on position - " + i + ". Only one of this field must be set:'calendar','dialAction','urlAction','location'.");
            }
        }
        return Optional.empty();
    }
}
