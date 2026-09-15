package gizmo;

import gizmo.task.Deadline;
import gizmo.task.Event;
import gizmo.task.Task;
import gizmo.task.Todo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads Gizmo tasks from a text file.
 */
public final class Storage {
    private static final Path DATA_FILE = Path.of("data", "gizmo.txt");

    private Storage() {
        // Prevent creating Storage objects.
    }

    /**
     * Saves all tasks to the data file.
     *
     * @throws GizmoException if the file cannot be written
     */
    public static void save(List<Task> tasks) throws GizmoException {
        try {
            Files.createDirectories(DATA_FILE.getParent());

            try (BufferedWriter writer = Files.newBufferedWriter(
                    DATA_FILE, StandardCharsets.UTF_8)) {

                for (Task task : tasks) {
                    writer.write(serializeTask(task));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new GizmoException("Unable to save tasks: " + e.getMessage());
        }
    }

    /**
     * Loads tasks from the data file.
     * If the file does not exist, Gizmo starts with an empty list.
     *
     * @throws GizmoException if the file cannot be read or is malformed
     */
    public static void load(List<Task> tasks) throws GizmoException {
        if (Files.notExists(DATA_FILE)) {
            return;
        }

        ArrayList<Task> loadedTasks = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(
                DATA_FILE, StandardCharsets.UTF_8)) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (!line.isBlank()) {
                    loadedTasks.add(deserializeTask(line, lineNumber));
                }
            }
        } catch (IOException e) {
            throw new GizmoException("Unable to load tasks: " + e.getMessage());
        }

        tasks.addAll(loadedTasks);
    }

    private static String serializeTask(Task task) throws GizmoException {
        String completionStatus = task.isDone() ? "1" : "0";

        if (task instanceof Todo) {
            return "T | " + completionStatus + " | " + task.getDescription();
        }

        if (task instanceof Deadline deadline) {
            return "D | " + completionStatus + " | "
                    + task.getDescription() + " | "
                    + deadline.getCompleteBy();
        }

        if (task instanceof Event event) {
            return "E | " + completionStatus + " | "
                    + task.getDescription() + " | "
                    + event.getEventStart() + " | "
                    + event.getEventEnd();
        }

        throw new GizmoException("Unsupported task type in storage.");
    }

    private static Task deserializeTask(String line, int lineNumber)
            throws GizmoException {

        String[] parts = line.split("\\s*\\|\\s*", -1);

        if (parts.length < 3) {
            throw new GizmoException("Invalid data on line " + lineNumber + ".");
        }
        if (!parts[1].equals("0") && !parts[1].equals("1")) {
            throw new GizmoException("Invalid completion status on line "
                    + lineNumber + ".");
        }

        boolean isDone = parts[1].equals("1");
        Task task;

        switch (parts[0]) {
            case "T":
                if (parts.length != 3) {
                    throw new GizmoException("Invalid todo on line "
                            + lineNumber + ".");
                }
                task = new Todo(parts[2]);
                break;

            case "D":
                if (parts.length != 4) {
                    throw new GizmoException("Invalid deadline on line "
                            + lineNumber + ".");
                }
                task = new Deadline(parts[2], parts[3]);
                break;

            case "E":
                if (parts.length != 5) {
                    throw new GizmoException("Invalid event on line "
                            + lineNumber + ".");
                }
                task = new Event(parts[2], parts[3], parts[4]);
                break;

            default:
                throw new GizmoException("Unknown task type on line "
                        + lineNumber + ".");
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }
}
