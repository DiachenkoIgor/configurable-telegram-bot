package com.incuube.bot.services.outcome.sender;

import com.incuube.bot.model.exceptions.BotConfigException;
import com.incuube.bot.model.exceptions.RcsApiBadGatewayException;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@Log4j2
public class OkHttpSender implements Sender {
    private OkHttpClient okHttpClient;

    @Autowired
    public OkHttpSender(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public void sendPostRequest(String body, String path, String url) {
        this.sendPostRequest(body, path, url, null);
    }

    public void sendPostRequest(String jsonRequestBody, String path, String url, Map<String, String> params) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url + path).newBuilder();

        if (params != null) {
            params.forEach(httpBuilder::addQueryParameter);
        }

        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/json"), jsonRequestBody);

        Request request = new Request.Builder()
                .post(requestBody)
                .url(httpBuilder.build())
                .build();

        try (Response response = this.okHttpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    throw new BotConfigException(String.format("Error sending REST request to RCS Gateway!! Code - %d ! Message - %s", response.code(), responseBody.string()));
                } else {
                    throw new BotConfigException("Error sending REST request to RCS Gateway!! Code - " + response.code());
                }
            } else {
                log.info("Successful sending for path: {}", path);
            }

        } catch (IOException e) {
            throw new RcsApiBadGatewayException("CONNECTION PROBLEM WITH RCS API!!" + e.getMessage());
        }
    }
}
