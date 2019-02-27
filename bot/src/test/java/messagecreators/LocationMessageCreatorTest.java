package messagecreators;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incuube.rcs.datamodel.messages.income.LocationUserMessage;
import com.incuube.rcs.datamodel.messages.income.UserMessage;
import com.incuube.receiver.messagefactory.messagecreator.LocationMessageCreator;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class LocationMessageCreatorTest {
    private String valideJson;

    private String wrongJson;

    private ObjectMapper mapper;

    private LocationMessageCreator locationMessageCreator;

    public LocationMessageCreatorTest() {
        valideJson = "{\n" +
                "  \"senderPhoneNumber\": \"+380952715167\",\n" +
                "  \"messageId\": \"MspC0LeLx1SoeR\\u003dVYOjKwjbQ\",\n" +
                "  \"sendTime\": \"2019-01-09T10:08:40.421550Z\",\n" +
                "  \"location\": {\n" +
                "    \"latitude\": 50.4377171,\n" +
                "    \"longitude\": 30.5006453\n" +
                "  }\n" +
                "}";

        wrongJson = "{\n" +
                "  \"senderPhoneNumber\": \"+380952715167\",\n" +
                "  \"messageId\": \"MspC0LeLx1SoeR\\u003dVYOjKwjbQ\",\n" +
                "  \"sendTime\": \"2019-01-09T10:08:40.421550Z\",\n" +
                "  \"location\":123" +
                "}";


        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper = objectMapper;

        locationMessageCreator = new LocationMessageCreator(objectMapper);
    }


    @Test
    public void testValideJsonDeserializationForLocationMessageCreator() throws IOException {
        Assert.assertTrue(locationMessageCreator.support(valideJson));
        UserMessage userMessage = locationMessageCreator.parseMessage(valideJson);
        mapper.writeValueAsString(userMessage);
        Assert.assertTrue(userMessage instanceof LocationUserMessage);
        LocationUserMessage locationUserMessage = (LocationUserMessage) userMessage;
        Assert.assertNotNull(locationUserMessage.getMessageId());
        Assert.assertNotNull(locationUserMessage.getNumber());
        Assert.assertNotNull(locationUserMessage.getLocation());
    }


    @Test(expected = IOException.class)
    public void testWrongJsonDeserializationForLocationMessageCreator() throws IOException {
        Assert.assertTrue(!locationMessageCreator.support(wrongJson));
        locationMessageCreator.parseMessage(wrongJson);

    }
}
