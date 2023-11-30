package com.chenjie.cloud.webclient;

import io.micrometer.core.instrument.Tag;
import org.springframework.boot.actuate.metrics.web.reactive.client.WebClientExchangeTags;
import org.springframework.boot.actuate.metrics.web.reactive.client.WebClientExchangeTagsProvider;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.util.Arrays;

public class CustomWebClientExchangeTagsProvider implements WebClientExchangeTagsProvider {

    @Override
    public Iterable<Tag> tags(ClientRequest request, ClientResponse response, Throwable throwable) {
        Tag method = WebClientExchangeTags.method(request);
        Tag originalUri = WebClientExchangeTags.uri(request);
        Tag uri = Tag.of(originalUri.getKey(), getUri(originalUri.getValue()));
        Tag clientName = Tag.of("clientName", (String) request.attribute("clientName").orElse("none"));
        Tag host = Tag.of("host", request.url().getHost());
        Tag status = WebClientExchangeTags.status(response, throwable);
        Tag outcome = WebClientExchangeTags.outcome(response);
        return Arrays.asList(method, uri, clientName, host, status, outcome);
    }

    private String getUri(String path) {
        if (null == path) {
            return "none";
        }
        int index = path.indexOf("?");
        if (index > -1) {
            return path.substring(0, index);
        }
        return path;
    }
}
