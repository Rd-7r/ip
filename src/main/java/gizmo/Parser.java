package gizmo;

import gizmo.task.Deadline;
import gizmo.task.Event;
import gizmo.task.Task;
import gizmo.task.Todo;

/**
 * Converts raw user input into validated commands for Gizmo to execute.
 */
public class Parser {
    private static final String VALID_COMMANDS =
            "invalid command :P\n"
            + "    valid commands are:\n"
            + "    todo...\n"
            + "    deadline.../by...\n"
            + "    event.../from.../to...\n"
            + "    mark {task number}\n"
            + "    unmark {task number}\n"
            + "    delete {task number}\n"
            + "    list\n"
            + "    bye";

    private static final String TODO_PREFIX = "todo ";
    private static final String DEADLINE_PREFIX = "deadline ";
    private static final String EVENT_PREFIX = "event ";
    private static final String BY_MARKER = " /by ";
    private static final String FROM_MARKER = " /from ";
    private static final String TO_MARKER = " /to ";

    /** Parses one raw command. */
    public Command parse(String input) throws GizmoException {
        String command = input.strip();

        if (command.equals("bye")) {
            return new Command(CommandType.EXIT, null, 0);
        }
        if (command.equals("list")) {
            return new Command(CommandType.LIST, null, 0);
        }

        String[] words = command.split("\\s+");
        if (words.length > 0 && isTaskNumberCommand(words[0])) {
            return parseTaskNumberCommand(words);
        }
        if (command.startsWith(TODO_PREFIX)) {
            return new Command(CommandType.ADD, parseTodo(command), 0);
        }
        if (command.startsWith(DEADLINE_PREFIX)) {
            return new Command(CommandType.ADD, parseDeadline(command), 0);
        }
        if (command.startsWith(EVENT_PREFIX)) {
            return new Command(CommandType.ADD, parseEvent(command), 0);
        }

        throw new GizmoException(VALID_COMMANDS);
    }

    /** Returns whether a command operates on a numbered task. */
    private boolean isTaskNumberCommand(String commandWord) {
        return commandWord.equals("mark")
                || commandWord.equals("unmark")
                || commandWord.equals("delete");
    }

    /** Parses mark, unmark, and delete commands. */
    private Command parseTaskNumberCommand(String[] words) throws GizmoException {
        if (words.length != 2) {
            throw new GizmoException(
                    "Use the command in this format: mark <task number> or "
                    + "unmark <task number> or delete <task number>.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(words[1]);
        } catch (NumberFormatException e) {
            throw new GizmoException("The task number given is not an integer!");
        }

        if (taskNumber < 1) {
            throw new GizmoException("The task number must be a positive integer!");
        }

        CommandType type = switch (words[0]) {
        case "mark" -> CommandType.MARK;
        case "unmark" -> CommandType.UNMARK;
        case "delete" -> CommandType.DELETE;
        default -> throw new GizmoException(VALID_COMMANDS);
        };
        return new Command(type, null, taskNumber);
    }

    /** Parses a todo command and extracts its description. */
    private Task parseTodo(String command) throws GizmoException {
        String description = command.substring(TODO_PREFIX.length()).strip();
        if (description.isEmpty()) {
            throw new GizmoException("the provided description is empty.");
        }
        return new Todo(description);
    }

    /** Parses a deadline command in the form {@code deadline <description> /by <time>}. */
    private Task parseDeadline(String command) throws GizmoException {
        int byMarkerIndex = command.indexOf(BY_MARKER);
        if (byMarkerIndex < DEADLINE_PREFIX.length()) {
            throw new GizmoException("A deadline must have a description and use /by <date or time> ");
        }

        String description = command.substring(DEADLINE_PREFIX.length(), byMarkerIndex).strip();
        String completeBy = command.substring(byMarkerIndex + BY_MARKER.length()).strip();
        if (description.isEmpty()) {
            throw new GizmoException("The provided deadline has no description");
        }
        if (completeBy.isEmpty()) {
            throw new GizmoException("A deadline must include a date or time after the `/by` keyword");
        }
        if (command.indexOf(BY_MARKER, byMarkerIndex + BY_MARKER.length()) >= 0) {
            throw new GizmoException("A deadline should contain only one `/by` marker.");
        }
        return new Deadline(description, completeBy);
    }

    /** Parses an event command in the form {@code event <description> /from <start> /to <end>}. */
    private Task parseEvent(String command) throws GizmoException {
        int fromMarkerIndex = command.indexOf(FROM_MARKER);
        int toMarkerIndex = command.indexOf(TO_MARKER);
        if (fromMarkerIndex < EVENT_PREFIX.length()
                || toMarkerIndex < 0
                || fromMarkerIndex >= toMarkerIndex) {
            throw new GizmoException("An event must have a description and include `/from <start>` and `/to <end>`.");
        }

        String description = command.substring(EVENT_PREFIX.length(), fromMarkerIndex).strip();
        String eventFrom = command.substring(fromMarkerIndex + FROM_MARKER.length(), toMarkerIndex).strip();
        String eventTo = command.substring(toMarkerIndex + TO_MARKER.length()).strip();
        if (description.isEmpty()) {
            throw new GizmoException("The event description provided is empty.");
        }
        if (eventFrom.isEmpty()) {
            throw new GizmoException("An event must include a start time after `/from` marker");
        }
        if (eventTo.isEmpty()) {
            throw new GizmoException("An event must include an end time after `/to` marker");
        }
        if (command.indexOf(FROM_MARKER, fromMarkerIndex + FROM_MARKER.length()) >= 0
                || command.indexOf(TO_MARKER, toMarkerIndex + TO_MARKER.length()) >= 0) {
            throw new GizmoException(
                    "An event should contain only one `/from` marker and one `/to` marker.");
        }
        return new Event(description, eventFrom, eventTo);
    }

    /** Types of commands that Gizmo can execute. */
    public enum CommandType {
        EXIT, LIST, ADD, MARK, UNMARK, DELETE
    }

    /** Immutable result produced by parsing a user command. */
    public static final class Command {
        private final CommandType type;
        private final Task task;
        private final int taskNumber;

        private Command(CommandType type, Task task, int taskNumber) {
            this.type = type;
            this.task = task;
            this.taskNumber = taskNumber;
        }

        public CommandType type() {
            return type;
        }

        public Task task() {
            return task;
        }

        public int taskNumber() {
            return taskNumber;
        }
    }
}
