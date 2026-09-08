package zhuangyan.timeplanning;

import org.springframework.core.ParameterizedTypeReference;
import zhuangyan.timeplanning.controller.*;
import zhuangyan.timeplanning.model.*;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;

/** Standalone test run that exercises the full CRUD cycle against a server. */
public class ClientSanityCheck {

    private final TaskController taskController;
    private final ConstraintController constraintController;
    private final ScheduleController scheduleController;
    private final AuthenticationController authenticationController;

    public ClientSanityCheck() {
        this.taskController = new TaskController();
        this.constraintController = new ConstraintController();
        this.scheduleController = new ScheduleController();
        this.authenticationController = new AuthenticationController();
    }

    public void sanityCheck() {
        System.out.println("Starting Sanity Check");
        Credentials loginCredentials = new Credentials("testuser", "testpass123");

        // Graceful Error Handling
        Consumer<Exception> errorHandler = e -> {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        };

        try {
            // Account Creation
            try {
                System.out.println("\n--- Testing Account Creation ---");
                authenticationController.createAccount(loginCredentials);
                System.out.println("Account created for: " + loginCredentials.username());
            } catch (Exception e) {
                errorHandler.accept(e);
            }

            
            // Login
            try {
                System.out.println("\n--- Testing Login ---");
                authenticationController.login(loginCredentials);
                System.out.println("Logged in successfully! Token: " + TokenStore.token);
            } catch (Exception e) {
                errorHandler.accept(e);
            }

            // Data
            TimePoint t1 = new TimePoint(DayOfWeek.MONDAY, LocalTime.MIDNIGHT);
            TimePoint t2 = new TimePoint(DayOfWeek.THURSDAY, LocalTime.MIDNIGHT);
            Task task1 = new Task(0L, "task1", "group1", Duration.ofHours(1), new TimeWindow(t1, t2));
            Task task2 = new Task(0L, "task2", "group2", Duration.ofHours(7), new TimeWindow(t1, t2));

            Consumer<List<Task>> taskConsumer = tasks -> {
                System.out.println("Current tasks: " + tasks.size());
                tasks.forEach(t -> System.out.println("  - Task: " + t));
            };

            
            // Add Tasks
            try {
                System.out.println("\n--- Testing Add Task (1) ---");
                taskController.add(task1, taskConsumer);
                System.out.println("--- Testing Add Task (2) ---");
                taskController.add(task2, taskConsumer);
            } catch (Exception e) {
                errorHandler.accept(e);
            }

            
            // Get All Tasks
            try {
                System.out.println("\n--- Testing Get All Tasks ---");
                taskController.getAll(taskConsumer, new ParameterizedTypeReference<List<Task>>() {});
            } catch (Exception e) {
                errorHandler.accept(e);
            }

            
            // Edit Task
            try {
                System.out.println("\n--- Testing Edit Task ---");
                taskController.getAll(tasks -> {
                            if (!tasks.isEmpty()) {
                                Task first = tasks.get(0);
                                Task edited = new Task(first.id(), "updatedTask", "group1",
                                        Duration.ofHours(2), new TimeWindow(t1, t2));
                                taskController.edit(edited, taskConsumer);
                                System.out.println("Task edited successfully");
                            } else {
                                System.out.println("No tasks to edit");
                            }
                        },
                        new ParameterizedTypeReference<List<Task>>() {});
            } catch (Exception e) {
                errorHandler.accept(e);
            }

            
            // Delete Task
            try {
                System.out.println("\n--- Testing Delete Task ---");
                taskController.getAll(tasks -> {
                            if (!tasks.isEmpty()) {
                                Task toDelete = tasks.get(0);
                                taskController.delete(toDelete, taskConsumer);
                                System.out.println("Task deleted successfully");
                            } else {
                                System.out.println("No tasks to delete");
                            }
                        },
                        new ParameterizedTypeReference<List<Task>>() {});
            } catch (Exception e) {
                errorHandler.accept(e);
            }

            // Data
            GroupConstraint constraint = new GroupConstraint(0L, "group1", "group2",
                    Duration.ofHours(1), Duration.ofHours(20));
            Consumer<List<GroupConstraint>> constraintConsumer = constraints -> {
                System.out.println("Current constraints: " + constraints.size());
                constraints.forEach(c -> System.out.println("  - Constraint: " + c));
            };

            // Add GroupConstraint
            try {
                System.out.println("\n--- Testing Add GroupConstraint ---");
                constraintController.add(constraint, constraintConsumer);
            } catch (Exception e) {
                errorHandler.accept(e);
            }

            
            // Get All Constraints
            try {
                System.out.println("\n--- Testing Get All Constraints ---");
                constraintController.getAll(constraintConsumer, new ParameterizedTypeReference<List<GroupConstraint>>() {});
            } catch (Exception e) {
                errorHandler.accept(e);
            }

            
            // Edit GroupConstraint
            try {
                System.out.println("\n--- Testing Edit GroupConstraint ---");
                constraintController.getAll(constraints -> {
                    if (!constraints.isEmpty()) {
                        GroupConstraint first = constraints.get(0);
                        GroupConstraint edited = new GroupConstraint(first.id(), "group1", "group2",
                                Duration.ofHours(2), Duration.ofHours(30));
                        constraintController.edit(edited, cons -> {
                            System.out.println("Constraint edited successfully");
                            cons.forEach(c -> System.out.println("  - Constraint: " + c));
                        });
                    } else {
                        System.out.println("No constraints to edit");
                    }
                },
                        new ParameterizedTypeReference<List<GroupConstraint>>(){});
            } catch (Exception e) {
                errorHandler.accept(e);
            }

            
            // Delete GroupConstraint
            try {
                System.out.println("\n--- Testing Delete GroupConstraint ---");
                constraintController.getAll(constraints -> {
                            if (!constraints.isEmpty()) {
                                GroupConstraint toDelete = constraints.get(0);
                                constraintController.delete(toDelete, cons -> {
                                    System.out.println("Constraint deleted successfully");
                                    cons.forEach(c -> System.out.println("  - Constraint: " + c));
                                });
                            } else {
                                System.out.println("No constraints to delete");
                            }
                        },
                        new ParameterizedTypeReference<List<GroupConstraint>>() {});
            } catch (Exception e) {
                errorHandler.accept(e);
            }

            // Data
            ScheduleConfig scheduleConfig = new ScheduleConfig(60, null, 3, 15); // adjust fields as needed
            Consumer<List<Schedule>> scheduleConsumer = schedules -> {
                System.out.println("Current schedules: " + schedules.size());
                schedules.forEach(s -> System.out.println("  - Schedule: " + s));
            };

            // Add Schedule
            try {
                System.out.println("\n--- Testing Add Schedule ---");
                scheduleController.addSchedule(scheduleConfig, scheduleConsumer);
            } catch (Exception e) {
                errorHandler.accept(e);
            }

            
            // Get All Schedules
            try {
                System.out.println("\n--- Testing Get All Schedules ---");
                scheduleController.getAllSchedules(scheduleConsumer);
            } catch (Exception e) {
                errorHandler.accept(e);
            }

        } finally {
            
            // Logout
            try {
                System.out.println("\n--- Testing Logout ---");
                authenticationController.logout();   // now uses POST /logout
                System.out.println("Logged out successfully!");
                System.out.println("Token after logout: " + TokenStore.token);
            } catch (Exception e) {
                errorHandler.accept(e);
            }

            
            // Login + Delete Account
            try {
                System.out.println("\n--- Testing Login (Setup for Account Deletion) ---");
                authenticationController.login(loginCredentials);
                System.out.println("Logged in successfully! Token: " + TokenStore.token);
            } catch (Exception e) {
                errorHandler.accept(e);
            }
            try {
                System.out.println("\n--- Testing Delete Account ---");
                authenticationController.deleteAccount();
                System.out.println("Account deleted successfully!");
            } catch (Exception e) {
                errorHandler.accept(e);
            }

            System.out.println("\nSanity Check Completed");
        }
    }

    public static void main(String[] args) {
        ClientSanityCheck check = new ClientSanityCheck();
        check.sanityCheck();
    }
}