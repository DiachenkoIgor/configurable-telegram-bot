package com.incuube.agent.googlelib;

// [START of the RBM API Helper]

// [START import_libraries]


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.*;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.rcsbusinessmessaging.v1.RCSBusinessMessaging;
import com.google.api.services.rcsbusinessmessaging.v1.model.*;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.incuube.agent.googlelib.cards.CardOrientation;
import com.incuube.agent.googlelib.cards.CardWidth;
import com.incuube.agent.googlelib.cards.MediaHeight;
import com.incuube.agent.googlelib.util.DomainConverter;
import com.incuube.rcs.datamodel.exceptions.RbmConnectionException;
import com.incuube.rcs.datamodel.exceptions.RbmNotSupportingException;
import com.incuube.rcs.datamodel.pubsub.PushSubscriptionCreationMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;
// [END import_libraries]

//TODO(igordiachenko): Refactor methods,exceptions, logic, initiation. Think about interface
//TODO(igordiachenko): Change endpoint for rcs
@Log4j2
@Component
public class RbmApiHelper {

    // the URL for the API endpoint
    private static final String RBM_API_URL = "https://europe-rcsbusinessmessaging.googleapis.com/";

    // credentials used for RBM agent API
    private GoogleCredential credential;

    private GoogleCredential credentialForPubSub;

    // reference to the RBM api builder
    private RCSBusinessMessaging.Builder builder;

    private final String credentialScope = "https://www.googleapis.com/auth/rcsbusinessmessaging";

    private final String credentialScopeForPubSubConfig = "https://www.googleapis.com/auth/pubsub";

    private final String templateForPubSub = "https://pubsub.googleapis.com/v1/projects/{google_project_id}/subscriptions/{subs_name}";

    private String pathToCredentials;

    private ObjectMapper objectMapper;

    private HttpRequestFactory requestFactory;

    @Autowired
    public RbmApiHelper(ObjectMapper objectMapper,
                        @Value("${spring.cloud.gcp.pubsub.credentials.location}") String path) {
        this.objectMapper = objectMapper;
        this.pathToCredentials = path;
        initCredentials(pathToCredentials);
        initRbmApi();
        initCredentialsForPubSub(pathToCredentials);
    }

    /**
     * Initializes credentials used by the RBM API.
     *
     * @param credentialsFileLocation The location for the GCP service account file.
     */
    private void initCredentials(String credentialsFileLocation) {
        log.info("Initializing credentials for RBM.");

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream input = classLoader.getResourceAsStream(credentialsFileLocation);

            this.credential = GoogleCredential
                    .fromStream(input);

            this.credential = credential.createScoped(Collections.singletonList(
                    credentialScope));
        } catch (IOException e) {
            log.error("Error in Google credentials initialization - " + e.getMessage().replace("\n", ""));
        }
    }

    private void initCredentialsForPubSub(String credentialsFileLocation) {
        log.info("Initializing credentials for PubSub.");

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream input = classLoader.getResourceAsStream(credentialsFileLocation);

            this.credentialForPubSub = GoogleCredential
                    .fromStream(input);
            this.credentialForPubSub = this.credentialForPubSub.createScoped(Collections.singletonList(
                    credentialScopeForPubSubConfig
            ));
        } catch (IOException e) {
            log.error("Error in Google Pub/Sub credentials initialization - " + e.getMessage().replace("\n", ""));
        }
    }

    /**
     * Initializes the RBM api object.
     */
    private void initRbmApi() {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            this.requestFactory = httpTransport.createRequestFactory();

            // create instance of the RBM API
            builder = new RCSBusinessMessaging
                    .Builder(httpTransport, jsonFactory, null)
                    .setApplicationName(credential.getServiceAccountProjectId());

            // set the API credentials and endpoint
            builder.setHttpRequestInitializer(credential);
            builder.setRootUrl(RBM_API_URL);
        } catch (IOException | GeneralSecurityException e) {
            log.error("Error in HttpTransport initialization - " + e.getMessage().replace("\n", ""));
        }
    }

    /**
     * Takes the msisdn and converts it into the format we need to make API calls.
     *
     * @param msisdn The phone url in E.164 format.
     * @return The phone url reformatted for the API.
     */
    private String convertToApiFormat(String msisdn) {
        return "phones/" + msisdn;
    }

    public void createPushSubscription(String googleProjectId, String subscriptionName, PushSubscriptionCreationMessage config) throws IOException {

        UriTemplate template = new UriTemplate(this.templateForPubSub);
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("google_project_id", googleProjectId);
        pathVariables.put("subs_name", subscriptionName);

        String json = objectMapper.writeValueAsString(config);
        HttpContent content = new ByteArrayContent(MediaType.APPLICATION_JSON_VALUE, json.getBytes(StandardCharsets.UTF_8));

        HttpRequest httpRequest = requestFactory.buildPutRequest(new GenericUrl(template.expand(pathVariables)), content);
        httpRequest.setInterceptor(this.credentialForPubSub);
        HttpResponse response = httpRequest.execute();

        log.info(response.parseAsString());
    }

    public void deleteSubscription(String googleProjectId, String subscriptionName) throws IOException {

        UriTemplate template = new UriTemplate(this.templateForPubSub);
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("google_project_id", googleProjectId);
        pathVariables.put("subs_name", subscriptionName);


        HttpRequest httpRequest = requestFactory.buildDeleteRequest(new GenericUrl(template.expand(pathVariables)));
        httpRequest.setInterceptor(this.credentialForPubSub);
        HttpResponse response = httpRequest.execute();

        log.info(response.parseAsString());
    }

    /**
     * Registers the device as a tester for this agent.
     *
     * @param msisdn The phone in E.164 format.
     */
    public void registerTester(String msisdn) throws Exception {
        Tester tester = new Tester();

        // convert the msisdn into the API format
        String clientDevice = convertToApiFormat(msisdn);

        // create the test request
        RCSBusinessMessaging.Phones.Testers.Create createTester
                = builder.build().phones().testers().create(clientDevice, tester);

        Tester response = createTester.execute();

        log.info(response);
    }

    /**
     * Checks whether the device associated with the phone url is RCS enabled.
     * This uses the asynchronous capability check API.
     *
     * @param msisdn The phone url in E.164 format.
     */
    public void performCapabilityCheck(String msisdn) throws Exception {
        // convert the msisdn into the API format
        String parent = convertToApiFormat(msisdn);

        // create a random UUID for the request
        String requestId = UUID.randomUUID().toString();

        // initialize the capability request payload
        RequestCapabilityCallbackRequest capabilityCallbackRequest
                = new RequestCapabilityCallbackRequest();

        // set the request id
        capabilityCallbackRequest.setRequestId(requestId);

        log.info("Device: " + parent);

        // build the request
        RCSBusinessMessaging.Phones.Capability.RequestCapabilityCallback request
                = builder
                .build()
                .phones()
                .capability()
                .requestCapabilityCallback(parent, capabilityCallbackRequest);

        Empty response = request.execute();

        // execute the capability request
        log.info(response);
    }

    /**
     * Checks whether the device associated with the phone url is RCS enabled.
     * This uses the alpha synchronous capability check API.
     *
     * @param msisdn The phone url in E.164 format.
     * @return True if the device is RCS enabled.
     */
    public Capabilities getCapability(String msisdn) throws RbmNotSupportingException, RbmConnectionException {
        msisdn = convertNumber(msisdn);
        // convert the msisdn into the API format
        String parent = convertToApiFormat(msisdn);

        log.info("Device: " + parent);

        try {
            // build the request
            RCSBusinessMessaging.Phones.GetCapabilities capabilityCheck
                    = builder
                    .build()
                    .phones()
                    .getCapabilities(parent);

            capabilityCheck.setRequestId(UUID.randomUUID().toString());

            Capabilities execute = capabilityCheck.execute();
            // execute synchronous capability check and log the result
            log.info(execute);

            return execute;
        } catch (GoogleJsonResponseException e) {
            log.error("RCS not supported - " + e.getMessage());

            RbmNotSupportingException supportingException = new RbmNotSupportingException();
            supportingException.setCode(e.getDetails().getCode());
            supportingException.setMessage(e.getDetails().getMessage());
            supportingException.initCause(e);
            throw supportingException;

        } catch (IOException ex) {
            log.error("RCS Api connection problem - " + ex.getMessage().replace("\n", ""));

            throw DomainConverter.prepareIoExceptionAdapter(ex);
        }
    }

    /**
     * Uploads the file located at the publicly available URL to the RBM platform.
     *
     * @param fileUrl          A publicly available URL.
     * @param thumbnailFileUrl Includes the thumbnail if there is one.
     * @return A unique file resource id.
     */
    public String uploadFile(String fileUrl, String thumbnailFileUrl) throws RbmConnectionException {
        String resourceId = null;


        CreateFileRequest fileRequest = new CreateFileRequest();
        fileRequest.setFileUrl(fileUrl);

        // add the thumbnail if there is one
        if (thumbnailFileUrl != null && thumbnailFileUrl.length() > 0) {
            fileRequest.setThumbnailUrl(thumbnailFileUrl);
        }

        try {
            RCSBusinessMessaging.Files.Create file =
                    builder.build().files().create(fileRequest);

            String jsonResponse = file.execute().toString();

            log.info("jsonResponse:" + jsonResponse);

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> jsonMap = gson.fromJson(jsonResponse, type);

            resourceId = jsonMap.get("name");
        } catch (IOException e) {
            log.error("File upload connection problem - " + e.getMessage().replace("\n", ""));
            throw DomainConverter.prepareIoExceptionAdapter(e);
        }

        return resourceId;
    }

    /**
     * Creates a card content object based on the parameters.
     *
     * @param title       The title for the card.
     * @param description The description for the card.
     * @param imageUrl    The image URL for the card's media.
     * @param height      The height to display the media.
     * @param suggestions List of suggestions to attach to the card.
     * @return The standalone card object.
     */
    public CardContent createCardContent(String title,
                                         String description,
                                         String imageUrl,
                                         MediaHeight height,
                                         List<Suggestion> suggestions) {
        CardContent cardContent = new CardContent();

        // have to build card from bottom up, starting with the media
        if (imageUrl != null) {
            // create content info for media element with the image URL
            Media media = new Media();
            media.setContentInfo(new ContentInfo().setFileUrl(imageUrl));
            media.setHeight(height.toString());

            // attach media to the card content
            cardContent.setMedia(media);
        }

        // make sure we have a title
        if (title != null) {
            cardContent.setTitle(title);
        }

        // make sure we have a description
        if (description != null) {
            cardContent.setDescription(description);
        }

        // make sure there are suggestions
        if (suggestions != null && suggestions.size() > 0) {
            cardContent.setSuggestions(suggestions);
        }

        return cardContent;
    }

    /**
     * Creates a standalone card object based on the passed in parameters.
     *
     * @param title       The title for the card.
     * @param description The description for the card.
     * @param imageUrl    The image URL for the card's media.
     * @param height      The height to display the media.
     * @param orientation The orientation of the card.
     * @param suggestions List of suggestions to attach to the card.
     * @return The standalone card object.
     */
    public StandaloneCard createStandaloneCard(String title,
                                               String description,
                                               String imageUrl,
                                               MediaHeight height,
                                               CardOrientation orientation,
                                               List<Suggestion> suggestions) {
        // create the card content representation of the parameters
        CardContent cardContent = createCardContent(
                title,
                description,
                imageUrl,
                height,
                suggestions
        );

        // create a standalone vertical card
        StandaloneCard standaloneCard = new StandaloneCard();
        standaloneCard.setCardContent(cardContent);
        standaloneCard.setCardOrientation(orientation.toString());

        return standaloneCard;
    }

    /**
     * Generic method to send a buttonText Message using the RBM api to the user with
     * the phone url msisdn.
     *
     * @param messageText The buttonText to send the user.
     * @param msisdn      The phone url in E.164 format.
     */
    public String sendTextMessage(String messageText, String msisdn) throws RbmConnectionException {
        try {
            return sendTextMessage(messageText, msisdn, null);
        } catch (IOException ex) {
            log.error("Text Message connection problem - " + ex.getMessage().replace("\n", ""));
            throw DomainConverter.prepareIoExceptionAdapter(ex);
        }
    }

    /**
     * Generic method to send a buttonText Message using the RBM api to the user with
     * the phone url msisdn.
     *
     * @param messageText The buttonText to send the user.
     * @param msisdn      The phone url in E.164 format.
     * @param suggestions The chip list suggestions.
     */
    public String sendTextMessage(String messageText, String msisdn, List<Suggestion> suggestions)
            throws IOException {
        msisdn = convertNumber(msisdn);
        // create content to send to the user
        AgentContentMessage agentContentMessage = new AgentContentMessage();
        agentContentMessage.setText(messageText);

        // attach suggestions if there are some
        if (suggestions != null && suggestions.size() > 0) {
            agentContentMessage.setSuggestions(suggestions);
        }

        // attach content to Message
        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setContentMessage(agentContentMessage);

        return sendAgentMessage(agentMessage, msisdn);
    }

    /**
     * Generic method to execute the sending a standalone card to a client.
     *
     * @param standaloneCard The card object to send.
     * @param msisdn         The phone url in E.164 format.
     * @throws IOException
     */
    public void sendStandaloneCard(StandaloneCard standaloneCard, String msisdn) throws IOException {
        // attach the standalone card to a rich card
        RichCard richCard = new RichCard();
        richCard.setStandaloneCard(standaloneCard);

        // attach the rich card to the content for the Message
        AgentContentMessage agentContentMessage = new AgentContentMessage();
        agentContentMessage.setRichCard(richCard);

        // attach content to Message
        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setContentMessage(agentContentMessage);

        // send the Message to the user
        sendAgentMessage(agentMessage, msisdn);
    }

    /**
     * Generic method to execute the sending of a carousel rich card to a client.
     *
     * @param cardContents List of CardContent items to be attached to the CarourselCard.
     * @param cardWidth    Width of the cards for the carousel.
     * @param msisdn       The phone url in E.164 format.
     * @throws IOException
     */
    public void sendCarouselCards(List<CardContent> cardContents, CardWidth cardWidth, String msisdn)
            throws IOException {
        // create a carousel card and attach the falist of card contents
        CarouselCard carouselCard = new CarouselCard();
        carouselCard.setCardContents(cardContents);
        carouselCard.setCardWidth(cardWidth.toString());

        // attach the carousel card to a rich card
        RichCard richCard = new RichCard();
        richCard.setCarouselCard(carouselCard);

        // attach the rich card to the content for the Message
        AgentContentMessage agentContentMessage = new AgentContentMessage();
        agentContentMessage.setRichCard(richCard);

        // attach content to Message
        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setContentMessage(agentContentMessage);

        // send the Message to the user
        sendAgentMessage(agentMessage, msisdn);
    }

    /**
     * Generic method to execute the sending of an agent Message to a client.
     *
     * @param agentMessage The Message payload to send.
     * @param msisdn       The phone url in E.164 format.
     */
    public String sendAgentMessage(AgentMessage agentMessage, String msisdn) throws IOException {
        // create a Message request to send to the msisdn
        RCSBusinessMessaging.Phones.AgentMessages.Create message =
                builder.build().phones().agentMessages().create(convertToApiFormat(msisdn), agentMessage);

        // generate a unique Message id
        message.setMessageId(UUID.randomUUID().toString());

        // execute the request, sending the buttonText to the user's phone
        AgentMessage response = message.execute();

        log.info(response);

        return message.getMessageId();
    }

    /**
     * Sends a READ request to a user's phone.
     *
     * @param messageId The Message id for the Message that was read.
     * @param msisdn    The phone url in E.164 format to send the event to.
     */
    public void sendReadMessage(String messageId, String msisdn) throws RbmConnectionException {
        try {
            msisdn = convertNumber(msisdn);
            String deviceNumber = convertToApiFormat(msisdn);

            // create READ event to send user
            AgentEvent agentEvent = new AgentEvent();
            agentEvent.setEventType(EventType.READ.toString());
            agentEvent.setMessageId(messageId);

            // create an agent event request to send to the msisdn
            RCSBusinessMessaging.Phones.AgentEvents.Create agentEventMessage =
                    builder.build().phones().agentEvents().create(deviceNumber, agentEvent);

            // set a unique event id
            agentEventMessage.setEventId(UUID.randomUUID().toString());

            // execute the request, sending the READ event to the user's phone
            agentEventMessage.execute();
        } catch (IOException e) {
            log.error("Read event sending connection problem - " + e.getMessage().replace("\n", ""));
            throw DomainConverter.prepareIoExceptionAdapter(e);
        }
    }

    /**
     * Sends the IS_TYPING event to the user.
     *
     * @param msisdn The phone url in E.164 format to send the event to.
     */
    public void sendIsTypingMessage(String msisdn) {
        try {
            String deviceNumber = convertToApiFormat(msisdn);

            // create READ event to send user
            AgentEvent agentEvent = new AgentEvent();
            agentEvent.setEventType(EventType.IS_TYPING.toString());

            // create an agent event request to send to the msisdn
            RCSBusinessMessaging.Phones.AgentEvents.Create agentEventMessage =
                    builder.build().phones().agentEvents().create(deviceNumber, agentEvent);

            // set a unique event id
            agentEventMessage.setEventId(UUID.randomUUID().toString());

            // execute the request, sending the READ event to the user's phone
            agentEventMessage.execute();
        } catch (IOException e) {
            log.error("IS_TYPING event sending connection problem - " + e.getMessage().replace("\n", ""));
        }
    }

    public String sendSuggestions(List<Suggestion> suggestions, String phone, String text) throws RbmConnectionException {
        phone = convertNumber(phone);

        AgentContentMessage agentContentMessage = new AgentContentMessage();

        agentContentMessage.setText(text);
        agentContentMessage.setSuggestions(suggestions);

        try {
            return sendAgentMessage(new AgentMessage().setContentMessage(agentContentMessage), phone);
        } catch (IOException e) {
            log.error("Suggestion  sending connection problem - " + e.getMessage().replace("\n", ""));
            throw DomainConverter.prepareIoExceptionAdapter(e);
        }
    }


    public String sendFile(String phone, String fileName) throws RbmConnectionException {

        phone = convertNumber(phone);

        AgentContentMessage agentContentMessage = new AgentContentMessage();
        agentContentMessage.setFileName(fileName);

        try {
            return sendAgentMessage(new AgentMessage().setContentMessage(agentContentMessage), phone);
        } catch (IOException e) {
            log.error("File sending connection problem - " + e.getMessage().replace("\n", ""));
            throw DomainConverter.prepareIoExceptionAdapter(e);
        }
    }

    private String convertNumber(String number) {
        return "+" + number;
    }

}
// [END of the RBM API Helper]


