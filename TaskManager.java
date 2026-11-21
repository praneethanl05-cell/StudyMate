
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.io.IOException;

class Task {
    private String taskName;
    private boolean completed;

    public Task(String taskName) {
        this.taskName = taskName;
        this.completed = false;
    }

    public String getTaskName() {
        return taskName;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void markCompleted() {
        completed = true;
    }

    public void undoCompleted() {
    completed = false;
}
}

public class TaskManager {

    private ArrayList<Task> tasks = new ArrayList<>();

    private static final Path DATA_FILE = Path.of("tasks.txt");

    public TaskManager() {
        loadTasks();
    }

    public void addTask(String name) {
        tasks.add(new Task(name));
        saveTasks();
        System.out.println("Task added successfully!");
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void completeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).markCompleted();
            saveTasks();
            System.out.println("Task completed!");
        } else {
            System.out.println("Invalid task number!");
        }
    }
    public void undoTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).undoCompleted();
            saveTasks();
            System.out.println("Task status undone!");
        }
    }

    public void deleteTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            saveTasks();
            System.out.println("Task deleted!");
        } else {
            System.out.println("Invalid task number!");
        }
    }

    private void saveTasks() {
        try {
            ArrayList<String> lines = new ArrayList<>();

            for (Task task : tasks) {
                String name = Base64.getEncoder().encodeToString(
                    task.getTaskName().getBytes(StandardCharsets.UTF_8)
                );

                lines.add(name + "|" + task.isCompleted());
            }

            Files.write(DATA_FILE, lines, StandardCharsets.UTF_8);

        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    private void loadTasks() {
        if (!Files.exists(DATA_FILE)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(DATA_FILE)) {
                String[] parts = line.split("\\|", 2);

                String name = new String(
                    Base64.getDecoder().decode(parts[0]),
                    StandardCharsets.UTF_8
                );

                Task task = new Task(name);

                if (parts.length == 2 &&
                    Boolean.parseBoolean(parts[1])) {
                    task.markCompleted();
                }

                tasks.add(task);
            }

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
    }
}
