package zhuangyan.timeplanning.service;

import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.model.TimePoint;
import zhuangyan.timeplanning.model.TimeWindow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    /**
     * Tool for Serialization/Deserialization of a Task-List in the .csv format.
     */

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    public List<Task> readTasks(Path path) throws IOException {
        long id = 0;
        List<Task> tasks = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path)) {

            // Skip header
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                String name = parts[0];
                String group = parts[1];
                Duration duration = Duration.parse(parts[2]);

                TimePoint earliestStart =
                        parseTimePoint(parts[3]);

                TimePoint latestEnd =
                        parseTimePoint(parts[4]);

                Task task = new Task(
                        id++,
                        name,
                        group,
                        duration,
                        new TimeWindow(
                                earliestStart,
                                latestEnd
                        )
                );

                tasks.add(task);
            }
        }

        return tasks;
    }

    public List<GroupConstraint> readConstraints(Path path) throws IOException {
        long id = 0;
        List<GroupConstraint> constraints = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path)) {

            // Skip header
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                String srcGroup = parts[0];
                String trgGroup = parts[1];
                Duration minGap = parts.length >= 3 && !parts[2].trim().isEmpty() ? Duration.parse(parts[2]) : null;
                Duration maxGap = parts.length >= 4 && !parts[3].trim().isEmpty() ? Duration.parse(parts[3]) : null;


                GroupConstraint constraint = new GroupConstraint(id++, srcGroup, trgGroup, minGap, maxGap);

                constraints.add(constraint);
            }
        }

        return constraints;

    }

    public void write(Path path, List<Task> tasks)
            throws IOException {

        try (BufferedWriter writer =
                     Files.newBufferedWriter(path)) {

            writer.write(
                    "name,group,duration,earliestStart,latestEnd"
            );
            writer.newLine();

            for (Task task : tasks) {

                writer.write(String.join(",",
                        task.name(),
                        task.group(),
                        task.duration().toString(),
                        formatTimePoint(
                                task.timeWindow().earliestStart()
                        ),
                        formatTimePoint(
                                task.timeWindow().latestEnd()
                        )
                ));

                writer.newLine();
            }
        }
    }

    private TimePoint parseTimePoint(String text) {

        String[] parts = text.split(" ");

        DayOfWeek day =
                DayOfWeek.valueOf(parts[0]);

        LocalTime time =
                LocalTime.parse(parts[1], TIME_FORMAT);

        return new TimePoint(day, time);
    }

    private String formatTimePoint(TimePoint point) {

        return point.day()
                + " "
                + point.time().format(TIME_FORMAT);
    }
}