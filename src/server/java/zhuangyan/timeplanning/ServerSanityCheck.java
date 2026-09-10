package zhuangyan.timeplanning;

import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.model.ScheduledTask;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.service.CsvParser;
import zhuangyan.timeplanning.service.schedule.engine.EngineConfig;
import zhuangyan.timeplanning.service.schedule.engine.SatEngine;
import zhuangyan.timeplanning.service.schedule.strategies.MemorizableSchedule;
import zhuangyan.timeplanning.time.TimeConverter;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public class ServerSanityCheck {

    public static void main(String[] args) {

        try {
            // Load tasks from CSV
            CsvParser repository = new CsvParser();
            List<Task> tasks = repository.readTasks(Path.of("tasks.csv"));
            System.out.println("Loaded " + tasks.size() + " tasks.");

            // Schedule
            TimeConverter converter = TimeConverter.create(15);
            List<GroupConstraint> constraints = List.of(new GroupConstraint(1L, "Meal", "Gym", Duration.ofMinutes(60), null));
            EngineConfig config = new EngineConfig(converter, 3, 15, new MemorizableSchedule(converter), constraints);

            List<ScheduledTask> schedule = SatEngine.schedule(tasks, config).get(0);

            // Print result
            System.out.println();
            System.out.println("=== SCHEDULE ===");

            for (ScheduledTask st : schedule) {
                Task task = st.task();
                System.out.printf(
                        "%-20s %-15s %-10s %s%n",
                        task.name(),
                        task.group(),
                        task.duration(),
                        st.start()
                );
            }

        } catch (Exception e) {
            System.err.println("Scheduling failed:");
            e.printStackTrace();
        }
    }
}