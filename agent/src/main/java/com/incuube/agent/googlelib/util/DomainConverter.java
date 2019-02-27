package com.incuube.agent.googlelib.util;

import com.google.api.services.rcsbusinessmessaging.v1.model.*;
import com.incuube.rcs.datamodel.exceptions.RbmConnectionException;
import com.incuube.rcs.datamodel.rest.*;

import java.io.IOException;

public class DomainConverter {
    public static Suggestion convertSuggestionAction(RcsSuggestionActionMessage actionMessage) {

        SuggestedAction action = new SuggestedAction();


        if (actionMessage.getLocation() != null) {
            action = prepareLocationAction(actionMessage);
        }
        if (actionMessage.getDialAction() != null) {
            action = prepareDialAction(actionMessage);
        }
        if (actionMessage.getUrlAction() != null) {
            action = prepareUrlAction(actionMessage);
        }
        if (actionMessage.getCalendar() != null) {
            action = prepareCalendarAction(actionMessage);
        }

        action.setPostbackData(actionMessage.getPostbackData());
        action.setText(actionMessage.getButtonText());

        return new Suggestion().setAction(action);
    }

    private static SuggestedAction prepareLocationAction(RcsSuggestionActionMessage actionMessage) {
        RestLocation location = actionMessage.getLocation();
        LatLng liblatLng = createLocationPojo(location);


        ViewLocationAction viewLocationAction = new ViewLocationAction();
        viewLocationAction.setLabel(location.getTextMaps());
        viewLocationAction.setLatLong(liblatLng);


        SuggestedAction suggestedAction = new SuggestedAction();
        suggestedAction.setViewLocationAction(viewLocationAction);

        return suggestedAction;

    }

    private static SuggestedAction prepareDialAction(RcsSuggestionActionMessage actionMessage) {
        RestDialAction restDialAction = actionMessage.getDialAction();

        DialAction dial = new DialAction();
        dial.setPhoneNumber(
                convertNumber(restDialAction.getNumber()));

        SuggestedAction dialSuggestedMessage = new SuggestedAction();
        dialSuggestedMessage.setDialAction(dial);

        return dialSuggestedMessage;

    }

    private static SuggestedAction prepareUrlAction(RcsSuggestionActionMessage actionMessage) {
        RestUrlAction urlAction = actionMessage.getUrlAction();

        OpenUrlAction openUrlAction = new OpenUrlAction();
        openUrlAction.setUrl(urlAction.getUrl());

        SuggestedAction dialSuggestedMessage = new SuggestedAction();
        dialSuggestedMessage.setOpenUrlAction(openUrlAction);

        return dialSuggestedMessage;

    }

    private static SuggestedAction prepareCalendarAction(RcsSuggestionActionMessage actionMessage) {
        RestCalendar calendar = actionMessage.getCalendar();

        CreateCalendarEventAction calendarEventAction = new CreateCalendarEventAction();
        calendarEventAction.setStartTime(calendar.getStartTime());
        calendarEventAction.setEndTime(calendar.getEndTime());
        calendarEventAction.setTitle(calendar.getTitle());
        calendarEventAction.setDescription(calendar.getDescription());

        SuggestedAction suggestedAction = new SuggestedAction();
        suggestedAction.setCreateCalendarEventAction(calendarEventAction);

        return suggestedAction;

    }

    public static Suggestion convertSuggestionReply(RcsSuggestionMessage rcsSuggestionMessage) {

        SuggestedReply suggestedReply = new SuggestedReply();
        suggestedReply.setPostbackData(rcsSuggestionMessage.getPostbackData());
        suggestedReply.setText(rcsSuggestionMessage.getButtonText());

        return new Suggestion().setReply(suggestedReply);
    }

    private static LatLng createLocationPojo(RestLocation restLatLng) {
        LatLng liblatLng = new LatLng();
        liblatLng.setLatitude(restLatLng.getLatitude());
        liblatLng.setLongitude(restLatLng.getLongitude());
        return liblatLng;
    }

    public static RbmConnectionException prepareIoExceptionAdapter(IOException ex) {
        RbmConnectionException connectionException = new RbmConnectionException();

        connectionException.setMessage(ex.getMessage());
        connectionException.initCause(ex);

        return connectionException;
    }

    private static String convertNumber(String number) {
        return "+" + number;
    }
}
