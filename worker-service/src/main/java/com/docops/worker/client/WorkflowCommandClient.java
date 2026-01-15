package com.docops.worker.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class WorkflowCommandClient {

    private static final String BASE_URL = "http://localhost:8083/workflows";
    private final RestTemplate restTemplate = new RestTemplate();

    public void complete(Long documentId) {
        restTemplate.postForEntity(
                BASE_URL + "/" + documentId + "/complete",
                null,
                Void.class
        );
    }

    public void advance(Long documentId, String event) {
        restTemplate.postForEntity(
                BASE_URL + "/" + documentId + "/advance",
                Map.of("event", event),
                Void.class
        );
    }

    public void fail(Long documentId, String error) {
        restTemplate.postForEntity(
                BASE_URL + "/" + documentId + "/fail?errorMessage=" + error,
                null,
                Void.class
        );
    }
    
    // ✅ ADD THIS METHOD (IDEMPOTENCY CHECK)
    public boolean canExecute(Long documentId, String step) {
        Boolean result = restTemplate.getForObject(
                BASE_URL + "/" + documentId + "/can-execute/" + step,
                Boolean.class
        );
        return Boolean.TRUE.equals(result);
    }
}