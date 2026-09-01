package zhuangyan.timeplanning.controller;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import zhuangyan.timeplanning.model.Config;
import zhuangyan.timeplanning.model.Schedule;
import zhuangyan.timeplanning.model.ScheduleConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScheduleController {

    private final RestClient restClient;
    private final List<Schedule> schedules;

    public ScheduleController() {
        restClient = RestClient.builder()
                .baseUrl(Config.base_url)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        schedules = new ArrayList<>();
    }

    public void addSchedule(ScheduleConfig config, Consumer<List<Schedule>> schedulesConsumer) {
        List<Schedule> addedSchedules = restClient.post()
                .uri("schedules")
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .body(config)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<Schedule>>() {
                })
                .getBody();

        synchronized (this) {
            schedules.clear();
            schedules.addAll(addedSchedules);
        }
        schedulesConsumer.accept(getSnapshot());
    }

    public void getAllSchedules(Consumer<List<Schedule>> schedulesConsumer) {
        List<Schedule> receivedSchedules = restClient.get()
                .uri("schedules")
                .header(HttpHeaders.AUTHORIZATION, TokenStore.getHeader())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<Schedule>>() {})
                .getBody();

        synchronized (this) {
            schedules.clear();
            schedules.addAll(receivedSchedules);
        }
        schedulesConsumer.accept(getSnapshot());
    }

    private synchronized List<Schedule> getSnapshot() {
        return new ArrayList<>(schedules);
    }

}
