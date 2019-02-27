package messagecreators;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incuube.rcs.datamodel.messages.income.TextUserMessage;
import com.incuube.rcs.datamodel.messages.income.UserMessage;
import com.incuube.receiver.messagefactory.messagecreator.TextMessageCreator;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TextMessageCreatorTest {

    private String valideJson;

    private String wrongJson;

    private ObjectMapper mapper;

    private TextMessageCreator textMessageCreator;

    public TextMessageCreatorTest() {
        valideJson = "{\n" +
                "  \"senderPhoneNumber\": \"+380952715167\",\n" +
                "  \"messageId\": \"MsPoLFWm3IQ42aE6q32bDc6g\",\n" +
                "  \"sendTime\": \"2019-01-08T11:16:51.770407Z\",\n" +
                "  \"text\": \"Yj\"\n" +
                "}";

        wrongJson = "{\n" +
                "  \"senderPhoneNumber\": \"+380952715167\",\n" +
                "  \"messageId\": \"MsPoLFWm3IQ42aE6q32bDc6g\",\n" +
                "  \"sendTime\": \"2019-01-08T11:16:51.770407Z\",\n" +
                "  \"text\":{ \"t\":1234.0}\n" +
                "}";


        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper = objectMapper;

        textMessageCreator = new TextMessageCreator(objectMapper);
    }


    @Test
    public void testValideJsonDeserializationForTextMessageCreator() throws IOException {
        Assert.assertTrue(textMessageCreator.support(valideJson));
        UserMessage userMessage = textMessageCreator.parseMessage(valideJson);
        mapper.writeValueAsString(userMessage);
        Assert.assertTrue(userMessage instanceof TextUserMessage);
        TextUserMessage textUserMessage = (TextUserMessage) userMessage;
        Assert.assertNotNull(textUserMessage.getMessageId());
        Assert.assertNotNull(textUserMessage.getNumber());
        Assert.assertNotNull(textUserMessage.getText());


    }


    @Test(expected = IOException.class)
    public void testWrongJsonDeserializationForTextMessageCreator() throws IOException {
        Assert.assertTrue(!textMessageCreator.support(wrongJson));
        textMessageCreator.parseMessage(wrongJson);

    }


}
