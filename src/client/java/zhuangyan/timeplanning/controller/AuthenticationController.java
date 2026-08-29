package zhuangyan.timeplanning.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import zhuangyan.timeplanning.model.Credentials;

public class AuthenticationController {

    private final RestClient restClient;

    public AuthenticationController() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:8080/")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public void login(Credentials credentials) {
        TokenStore.token = restClient.post()
                .uri("sessions")
                .body(credentials)
                .retrieve()
                .toEntity(String.class)
                .getBody();
    }

    public void logout() {
        restClient.delete()
                .uri("sessions")
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .retrieve()
                .toBodilessEntity();
        TokenStore.token = null;
    }

    public void createAccount(Credentials credentials) {
        restClient.post()
                .uri("accounts")
                .body(credentials)
                .retrieve()
                .toBodilessEntity();
    }

    public void deleteAccount() {
        restClient.delete()
                .uri("accounts")
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .retrieve()
                .toBodilessEntity();
    }
}
