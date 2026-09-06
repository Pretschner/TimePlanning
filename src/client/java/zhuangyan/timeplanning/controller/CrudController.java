package zhuangyan.timeplanning.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import zhuangyan.timeplanning.model.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class CrudController<T> {
    protected final RestClient restClient;
    protected final List<T> cache = new ArrayList<>();
    private final String endpoint;
    private final Class<T> type;
    private final Function<T, Long> idExtractor;

    protected CrudController(String endpoint, Class<T> type, Function<T, Long> idExtractor) {
        this.endpoint = endpoint;
        this.type = type;
        this.idExtractor = idExtractor;
        this.restClient = RestClient.builder()
                .baseUrl(Config.base_url)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public void add(T item, Consumer<List<T>> consumer) {
        T addedItem = restClient.post()
                .uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .body(item)
                .retrieve()
                .toEntity(type)
                .getBody();

        synchronized (this) {
            cache.add(addedItem);
        }
        consumer.accept(snapshot());
    }

    public void edit(T item, Consumer<List<T>> consumer) {
        Long id = idExtractor.apply(item);
        T updatedItem = restClient.put()
                .uri(endpoint + "/" + id)
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .body(item)
                .retrieve()
                .toEntity(type)
                .getBody();

        synchronized (this) {
            cache.replaceAll(old -> idExtractor.apply(old).equals(id) ? updatedItem : old);
        }
        consumer.accept(snapshot());
    }

    public void delete(T item, Consumer<List<T>> consumer) {
        Long id = idExtractor.apply(item);
        restClient.delete()
                .uri(endpoint + "/" + id)
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .retrieve()
                .toBodilessEntity();

        synchronized (this) {
            cache.removeIf(old -> idExtractor.apply(old).equals(id));
        }
        consumer.accept(snapshot());
    }

    public void getAll(Consumer<List<T>> consumer) {
        List<T> received = restClient.get()
                .uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<T>>() {})
                .getBody();

        synchronized (this) {
            cache.clear();
            cache.addAll(received);
        }
        consumer.accept(snapshot());
    }

    protected final synchronized List<T> snapshot() {
        return new ArrayList<>(cache);
    }
}