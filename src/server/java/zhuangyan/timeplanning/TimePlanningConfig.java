package zhuangyan.timeplanning;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.model.Schedule;
import zhuangyan.timeplanning.model.Task;
import zhuangyan.timeplanning.repository.BaseRepository;
import zhuangyan.timeplanning.service.schedule.CsvParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Configuration
// Scans the contents of this classes package and all subpackages.
// -> Keep at root level of server package!
@ComponentScan
public class TimePlanningConfig {
    @Bean
    public BaseRepository<Task> taskRepository() throws IOException {
        BaseRepository<Task> repository = new BaseRepository<>();
        CsvParser parser = new CsvParser();
        List<Task> tasks = parser.readTasks(Path.of("tasks.csv"));
        repository.save(tasks, 1, Task::id);
        return repository;
    }

    @Bean
    public BaseRepository<GroupConstraint> constraintRepository() throws IOException {
        BaseRepository<GroupConstraint> repository = new BaseRepository<>();
        CsvParser parser = new CsvParser();
        List<GroupConstraint> constraints = parser.readConstraints(Path.of("constraints.csv"));
        repository.save(constraints, 1, GroupConstraint::id);
        return repository;
    }

    @Bean
    public BaseRepository<Schedule> scheduleRepository() throws IOException {
        return new BaseRepository<>();
    }
}
