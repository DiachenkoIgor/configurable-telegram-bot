
package com.incuube.rcs.datamodel.util;

public class ValidationConstants {

    public static final int POSTBACK_FIELD_MAX_LENGTH = 2040;
    public static final String POSTBACK_FIELD_MAX_MESSAGE = "Max 'postback' size is " + POSTBACK_FIELD_MAX_LENGTH;

    public static final int TEXT_FIELD_MAX_LENGTH = 3070;
    public static final String TEXT_FIELD_MAX_MESSAGE = "Max 'buttonText' size is " + TEXT_FIELD_MAX_LENGTH;

    public static final int FILE_NAME_FIELD_MAX_LENGTH = 100;
    public static final String FILE_NAME_FIELD_MAX_MESSAGE = "Max 'fileName' size is  " + FILE_NAME_FIELD_MAX_LENGTH;

    public static final int BUTTON_TEXT_FIELD_MAX_LENGTH = 25;
    public static final String BUTTON_TEXT_FIELD_MAX_MESSAGE = "Max 'buttonText' size is " + BUTTON_TEXT_FIELD_MAX_LENGTH;

    public static final String PHONE_REGEX = "^380{1}[0-9]{9}$";
    public static final String PHONE_REGEX_MESSAGE = "Invalid phone number. Phone number example - 380956785647";

    public static final int LATITUDE_FIELD_MAX_VALUE = 90;
    public static final String LATITUDE_FIELD_MAX_MESSAGE = "Max 'latitude' size is " + LATITUDE_FIELD_MAX_VALUE;

    public static final int LATITUDE_FIELD_MIN_VALUE = -90;
    public static final String LATITUDE_FIELD_MIN_MESSAGE = "Min 'latitude' size is " + LATITUDE_FIELD_MIN_VALUE;

    public static final int LONGITUDE_FIELD_MAX_VALUE = 180;
    public static final String LONGITUDE_FIELD_MAX_MESSAGE = "Max 'longitude' size is " + LONGITUDE_FIELD_MAX_VALUE;

    public static final int LONGITUDE_FIELD_MIN_VALUE = -180;
    public static final String LONGITUDE_FIELD_MIN_MESSAGE = "Min 'longitude' size is " + LONGITUDE_FIELD_MIN_VALUE;

    public static final int SUGGESTIONS_MAX_QUANTITY_VALUE = 11;
    public static final String SUGGESTIONS_MAX_QUANTITY_MESSAGE = "Max 'suggestions' size is " + SUGGESTIONS_MAX_QUANTITY_VALUE;

    public static final int PHONE_SIZE = 12;

}
