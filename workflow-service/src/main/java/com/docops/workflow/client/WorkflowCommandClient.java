package com.docops.workflow.client;

import com.docops.workflow.dto.AdvanceWorkflowRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

        AdvanceWorkflowRequest request = new AdvanceWorkflowRequest();
        request.setEvent(event);
        request.setActor("SYSTEM"); // optional, but useful later

        restTemplate.postForEntity(
                BASE_URL + "/" + documentId + "/advance",
                request,
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
}
