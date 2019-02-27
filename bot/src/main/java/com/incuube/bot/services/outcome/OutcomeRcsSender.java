package com.incuube.bot.services.outcome;

import com.incuube.bot.model.common.Button;
import com.incuube.bot.model.common.users.RcsUser;
import com.incuube.bot.model.exceptions.BotConfigException;
import com.incuube.bot.model.outcome.OutcomeSuggestionMessage;
import com.incuube.bot.model.outcome.OutcomeTextMessage;
import com.incuube.bot.services.outcome.sender.Sender;
import com.incuube.bot.util.JsonConverter;
import com.incuube.bot.util.ParamsExtractor;
import com.incuube.rcs.datamodel.rest.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class OutcomeRcsSender {

    private Sender sender;
    @Value("${bot.rcs.api}")
    private String apiUrl;

    public OutcomeRcsSender(Sender sender) {
        this.sender = sender;
    }

    public void sendOutcomeMessage(OutcomeSuggestionMessage suggestionMessage, RcsUser rcsUser) {
        List<Button> buttons = suggestionMessage.getButtons();

        if (!buttons.isEmpty()) {
            //REMEMBER! If button doesn't have parameters,  it is suggestion reply, else it is suggestion action. RCS doesn't allow to mix actions and replies
            if (buttons.get(0).getParams().isEmpty()) {
                sendSuggestionReplies(buttons, rcsUser, suggestionMessage.getText());
            } else {
                sendSuggestionActions(buttons, rcsUser, suggestionMessage.getText());
            }
        }
        if (buttons.isEmpty()) {
            throw new BotConfigException("Missed buttons.");
        }
    }

    private void sendSuggestionReplies(List<Button> replies, RcsUser rcsUser, String commonText) {
        List<RcsSuggestionMessage> rcsSuggestionRepliesMessages = replies.stream().map(this::rcsSuggestionReplyConverter).collect(Collectors.toList());
        Optional<String> optionalJson = JsonConverter.convertObject(rcsSuggestionRepliesMessages);

        Map<String, String> params = new HashMap<>();
        params.put("phone", rcsUser.getNumber());
        params.put("text", commonText);

        optionalJson.ifPresent(value -> this.sender.sendPostRequest(value, "/rcs/suggestions/replies", apiUrl, params));
    }

    private RcsSuggestionMessage rcsSuggestionReplyConverter(Button button) {
        RcsSuggestionMessage rcsSuggestionMessage = new RcsSuggestionMessage();
        rcsSuggestionMessage.setButtonText(button.getButtonText());
        rcsSuggestionMessage.setPostbackData(button.getNextActionId());
        return rcsSuggestionMessage;
    }

    private void sendSuggestionActions(List<Button> actions, RcsUser rcsUser, String commonText) {
        List<RcsSuggestionActionMessage> rcsSuggestionActionMessages =
                actions.stream().map(this::rcsSuggestionActionMessageConverter).collect(Collectors.toList());

        Optional<String> optionalJson = JsonConverter.convertObject(rcsSuggestionActionMessages);
        Map<String, String> params = new HashMap<>();
        params.put("phone", rcsUser.getNumber());
        params.put("text", commonText);

        optionalJson.ifPresent(value -> this.sender.sendPostRequest(value, "/rcs/suggestions/actions", apiUrl, params));
    }


    private RcsSuggestionActionMessage rcsSuggestionActionMessageConverter(Button button) {

        Object numberButton = button.getParams().get("rcs_number_button");

        RcsSuggestionActionMessage rcsSuggestionActionMessage = new RcsSuggestionActionMessage();
        rcsSuggestionActionMessage.setPostbackData(button.getNextActionId());
        rcsSuggestionActionMessage.setButtonText(button.getButtonText());

        if (numberButton != null) {
            RestDialAction restDialAction = new RestDialAction();
            Optional<String> number = ParamsExtractor.getParamFromMap((Map<String, Object>) numberButton, "number");
            if (number.isPresent()) {
                restDialAction.setNumber(number.get());
                rcsSuggestionActionMessage.setDialAction(restDialAction);
                return rcsSuggestionActionMessage;
            }
            throw new BotConfigException("Wrong configuration for 'Dial Action'! Missed parameter 'number'!");
        }

        Object urlButton = button.getParams().get("rcs_url_button");
        if (urlButton != null) {
            RestUrlAction restUrlAction = new RestUrlAction();
            Optional<String> url = ParamsExtractor.getParamFromMap((Map<String, Object>) urlButton, "url");

            if (url.isPresent()) {
                restUrlAction.setUrl(url.get());
                rcsSuggestionActionMessage.setUrlAction(restUrlAction);
                return rcsSuggestionActionMessage;
            }
            throw new BotConfigException("Wrong configuration for 'Dial Action'! Missed parameter 'number'!");
        }
        throw new BotConfigException("Wrong configuration for Suggested Action! Missed actions!");
    }

    public void sendOutcomeMessage(OutcomeTextMessage textMessage, RcsUser rcsUser) {
        RcsTextMessage rcsTextMessage = new RcsTextMessage();
        rcsTextMessage.setNumber(rcsUser.getNumber());
        rcsTextMessage.setText(textMessage.getText());

        Optional<String> optionalJson = JsonConverter.convertObject(rcsTextMessage);

        optionalJson.ifPresent(value -> this.sender.sendPostRequest(value, "/rcs/text", apiUrl));
    }
}
