package messagecreators;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incuube.rcs.datamodel.messages.income.FileUserMessage;
import com.incuube.rcs.datamodel.messages.income.UserMessage;
import com.incuube.receiver.messagefactory.messagecreator.FileMessageCreator;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class FileMessageCreatorTest {
    private String valideJson;

    private String wrongJson;

    private ObjectMapper mapper;

    private FileMessageCreator fileMessageCreator;

    public FileMessageCreatorTest() {
        valideJson = "{\n" +
                "  \"senderPhoneNumber\": \"+380952715167\",\n" +
                "  \"messageId\": \"Ms4IToyTR6R72KlDYId08Zhw\",\n" +
                "  \"sendTime\": \"2019-01-09T10:13:50.395933Z\",\n" +
                "  \"userFile\": {\n" +
                "    \"thumbnail\": {\n" +
                "      \"mimeType\": \"image/jpeg\",\n" +
                "      \"fileSizeBytes\": 30840,\n" +
                "      \"fileUri\": \"https://rcs-content-us.storage.googleapis.com/59ae4a7d-0c80-4167-b10d-ea12b6aabff4/b1c884b75182c9500150daa4abcf65d20680c68e61d57d9b9f9c36767d11\"\n" +
                "    },\n" +
                "    \"payload\": {\n" +
                "      \"mimeType\": \"image/png\",\n" +
                "      \"fileSizeBytes\": 16337,\n" +
                "      \"fileName\": \"18\",\n" +
                "      \"fileUri\": \"https://rcs-content-us.storage.googleapis.com/59ae4a7d-0c80-4167-b10d-ea12b6aabff4/e539951e54fa11bf8e1eac11eaa2d7dbaade656ec42091707f4bdeb7a482\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        wrongJson = "{\n" +
                "  \"senderPhoneNumber\": \"+380952715167\",\n" +
                "  \"messageId\": \"Ms4IToyTR6R72KlDYId08Zhw\",\n" +
                "  \"sendTime\": \"2019-01-09T10:13:50.395933Z\",\n" +
                "  \"userFil\": {\n" +
                "    \"thumbnail\": {\n" +
                "      \"mimeType\": \"image/jpeg\",\n" +
                "      \"fileSizeBytes\": 30840,\n" +
                "      \"fileUri\": \"https://rcs-content-us.storage.googleapis.com/59ae4a7d-0c80-4167-b10d-ea12b6aabff4/b1c884b75182c9500150daa4abcf65d20680c68e61d57d9b9f9c36767d11\"\n" +
                "    },\n" +
                "    \"payload\":12314, {\n" +
                "      \"mimeType\": \"image/png\",\n" +
                "      \"fileSizeBytes\": 16337,\n" +
                "      \"fileName\": \"18\",\n" +
                "      \"fileUri\": \"https://rcs-content-us.storage.googleapis.com/59ae4a7d-0c80-4167-b10d-ea12b6aabff4/e539951e54fa11bf8e1eac11eaa2d7dbaade656ec42091707f4bdeb7a482\"\n" +
                "    }\n" +
                "  }\n" +
                "}";


        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper = objectMapper;

        fileMessageCreator = new FileMessageCreator(objectMapper);
    }


    @Test
    public void testValideJsonDeserializationForFileMessageCreator() throws IOException {
        Assert.assertTrue(fileMessageCreator.support(valideJson));
        UserMessage userMessage = fileMessageCreator.parseMessage(valideJson);
        mapper.writeValueAsString(userMessage);
        Assert.assertTrue(userMessage instanceof FileUserMessage);
        FileUserMessage fileUserMessage = (FileUserMessage) userMessage;
        Assert.assertNotNull(fileUserMessage.getMessageId());
        Assert.assertNotNull(fileUserMessage.getNumber());
        Assert.assertNotNull(fileUserMessage.getUserFile());
    }


    @Test(expected = IOException.class)
    public void testWrongJsonDeserializationForFileMessageCreator() throws IOException {
        Assert.assertTrue(!fileMessageCreator.support(wrongJson));
        fileMessageCreator.parseMessage(wrongJson);

    }


}
