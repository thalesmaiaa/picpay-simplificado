package com.example.picpay.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MockyService {

    private static final String TRANSFER_CREDENTIALS = "https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc";
    private static final String TRANSFER_NOTIFICATION = "https://run.mocky.io/v3/54dc2cf1-3add-45b5-b5a9-6bf7e7f1f4a6";

    public ObjectNode getTransferCrendentials() {
        RestTemplate restTemplate = new RestTemplate();
        ObjectNode response = restTemplate.getForObject(TRANSFER_CREDENTIALS, ObjectNode.class);

        return response;
    }

    public void notifyPostTransfer() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(TRANSFER_NOTIFICATION, ObjectNode.class);
    }
}
