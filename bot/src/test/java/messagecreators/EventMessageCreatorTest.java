package messagecreators;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incuube.rcs.datamodel.messages.income.EventUserMessage;
import com.incuube.rcs.datamodel.messages.income.UserMessage;
import com.incuube.receiver.messagefactory.messagecreator.EventMessageCreator;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class EventMessageCreatorTest {
    private String valideJson;

    private String wrongJson;

    private ObjectMapper mapper;

    private EventMessageCreator eventMessageCreator;

    public EventMessageCreatorTest() {
        valideJson = "{\n" +
                "  \"senderPhoneNumber\": \"+380952715167\",\n" +
                "  \"eventType\": \"DELIVERED\",\n" +
                "  \"eventId\": \"MsRBPde-6vTfOJodW6FC8vxg\",\n" +
                "  \"messageId\": \"08d515b3-ad9e-4294-b8a6-498bd94c207f\",\n" +
                "  \"sendTime\": \"2019-01-09T10:18:24.977724Z\"\n" +
                "}";

        wrongJson = "{\n" +
                "  \"senderPhoneNumber\": \"+380952715167\",\n" +
                "  \"eventType\":{},\n" +
                "  \"eventId\": \"MsRBPde-6vTfOJodW6FC8vxg\",\n" +
                "  \"messageId\": \"08d515b3-ad9e-4294-b8a6-498bd94c207f\",\n" +
                "  \"sendTime\": \"2019-01-09T10:18:24.977724Z\"\n" +
                "}";


        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper = objectMapper;

        eventMessageCreator = new EventMessageCreator(objectMapper);
    }


    @Test
    public void testValideJsonDeserializationForEventMessageCreator() throws IOException {
        Assert.assertTrue(eventMessageCreator.support(valideJson));
        UserMessage userMessage = eventMessageCreator.parseMessage(valideJson);
        mapper.writeValueAsString(userMessage);
        Assert.assertTrue(userMessage instanceof EventUserMessage);
        EventUserMessage eventUserMessage = (EventUserMessage) userMessage;
        Assert.assertNotNull(eventUserMessage.getMessageId());
        Assert.assertNotNull(eventUserMessage.getNumber());
        Assert.assertNotNull(eventUserMessage.getEventType());
    }


    @Test(expected = IOException.class)
    public void testWrongJsonDeserializationForEventMessageCreator() throws IOException {
        Assert.assertTrue(!eventMessageCreator.support(wrongJson));
        eventMessageCreator.parseMessage(wrongJson);

    }


}
