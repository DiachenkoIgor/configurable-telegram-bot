package messagecreators;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incuube.rcs.datamodel.messages.income.SuggestionUserMessage;
import com.incuube.rcs.datamodel.messages.income.UserMessage;
import com.incuube.receiver.messagefactory.messagecreator.SuggestionMessageCreator;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SuggestionMessageCreatorTest {
    private String valideJson;

    private String wrongJson;

    private ObjectMapper mapper;

    private SuggestionMessageCreator suggestionMessageCreator;

    public SuggestionMessageCreatorTest() {
        valideJson = "{\n" +
                "  \"senderPhoneNumber\": \"+380952715167\",\n" +
                "  \"messageId\": \"Ms-i49fQ6VSsal-bz3xoQkzg\",\n" +
                "  \"sendTime\": \"2019-01-09T09:58:57.369526Z\",\n" +
                "  \"suggestionResponse\": {\n" +
                "    \"postbackData\": \"cool\",\n" +
                "    \"buttonText\": \"Java\"\n" +
                "  }\n" +
                "}";

        wrongJson = "{\n" +
                "  \"senderPhoneNumber\": \"+380952715167\",\n" +
                "  \"messageId\": \"Ms-i49fQ6VSsal-bz3xoQkzg\",\n" +
                "  \"sendTime\": \"2019-01-09T09:58:57.369526Z\",\n" +
                "  \"suggestionResponse\": 123\n" +
                "}";


        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper = objectMapper;

        suggestionMessageCreator = new SuggestionMessageCreator(objectMapper);
    }


    @Test
    public void testValideJsonDeserializationForSuggestionMessageCreator() throws IOException {
        Assert.assertTrue(suggestionMessageCreator.support(valideJson));
        UserMessage userMessage = suggestionMessageCreator.parseMessage(valideJson);
        mapper.writeValueAsString(userMessage);
        Assert.assertTrue(userMessage instanceof SuggestionUserMessage);
        SuggestionUserMessage suggestionUserMessage = (SuggestionUserMessage) userMessage;
        Assert.assertNotNull(suggestionUserMessage.getSuggestionResponse());
        Assert.assertNotNull(suggestionUserMessage.getMessageId());
        Assert.assertNotNull(suggestionUserMessage.getNumber());
    }


    @Test(expected = IOException.class)
    public void testWrongJsonDeserializationForSuggestionMessageCreator() throws IOException {
        Assert.assertTrue(!suggestionMessageCreator.support(wrongJson));
        suggestionMessageCreator.parseMessage(wrongJson);

    }
}
