package com.incuube.bot.services.outcome.sender;

import java.util.Map;

public interface Sender {
    void sendPostRequest(String body, String path, String url, Map<String, String> params);

    void sendPostRequest(String body, String path, String url);
}
