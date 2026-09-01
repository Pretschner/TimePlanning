package zhuangyan.timeplanning.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import zhuangyan.timeplanning.model.Config;
import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConstraintController {
    private final RestClient restClient;
    private final List<GroupConstraint> constraints;

    public ConstraintController() {
        restClient = RestClient.builder()
                .baseUrl(Config.base_url)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        constraints = new ArrayList<>();
    }

    public void addGroupConstraint(GroupConstraint constraint, Consumer<List<GroupConstraint>> constraintsConsumer) {
        GroupConstraint addedGroupConstraint = restClient.post()
                .uri("constraints")
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .body(constraint)
                .retrieve()
                .toEntity(GroupConstraint.class)
                .getBody();

        synchronized (this) {
            constraints.add(addedGroupConstraint);
        }
        constraintsConsumer.accept(getSnapshot());
    }

    public void editGroupConstraint(GroupConstraint constraint, Consumer<List<GroupConstraint>> constraintsConsumer) {
        GroupConstraint updatedGroupConstraint = restClient.put()
                .uri("constraints/" + constraint.id())
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .body(constraint)
                .retrieve()
                .toEntity(GroupConstraint.class)
                .getBody();

        synchronized (this) {
            constraints.replaceAll(oldGroupConstraint -> oldGroupConstraint.id().equals(updatedGroupConstraint.id()) ? updatedGroupConstraint : oldGroupConstraint);
        }
        constraintsConsumer.accept(getSnapshot());
    }

    public void deleteGroupConstraint(GroupConstraint constraint, Consumer<List<GroupConstraint>> constraintsConsumer) {
        GroupConstraint deletedGroupConstraint = restClient.delete()
                .uri("constraints/" + constraint.id())
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .retrieve()
                .toEntity(GroupConstraint.class)
                .getBody();

        synchronized (this) {
            constraints.removeIf(t -> t.id().equals(deletedGroupConstraint.id()));
        }
        constraintsConsumer.accept(getSnapshot());
    }

    public void getAllConstraints(Consumer<List<GroupConstraint>> constraintsConsumer) {
        List<GroupConstraint> receivedConstraints = restClient.get()
                .uri("constraints")
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<GroupConstraint>>() {})
                .getBody();

        synchronized (this) {
            constraints.clear();
            constraints.addAll(receivedConstraints);
        }
        constraintsConsumer.accept(getSnapshot());
    }

    private synchronized List<GroupConstraint> getSnapshot() {
        return new ArrayList<>(constraints);
    }

}
