package gizmo;

import gizmo.task.Deadline;
import gizmo.task.Event;
import gizmo.task.Task;
import gizmo.task.Todo;

import java.util.Scanner;
import java.util.ArrayList;

/**
 * Runs Gizmo's command-line task management interface.
 */
public class Gizmo {
    private static final String LINE_SEPERATOR = "    ________________________________________________________________________________________\n";

    private static final String WELCOME_BANNER =
            LINE_SEPERATOR
            + "    Hello! I'm Gizmo. \n"
            + "    How may I assist you today?\n"
            + LINE_SEPERATOR;

    private static final String BYE_MESSAGE =
            LINE_SEPERATOR
            + "    Disconnecting... I'll be here whenever you need me again.\n"
            + LINE_SEPERATOR;

    private static final String VALID_COMMANDS =
            "invalid command :P\n"
            + "    valid commands are:\n"
            + "    todo...\n"
            + "    deadline.../by...\n"
            + "    event.../from.../to...\n"
            + "    mark {task number}\n"
            + "    unmark {task number}\n"
            + "    list\n"
            + "    bye";

    private static final String TODO_PREFIX = "todo ";
    private static final String DEADLINE_PREFIX = "deadline ";
    private static final String EVENT_PREFIX = "event ";

    private static final String BY_MARKER = " /by ";
    private static final String FROM_MARKER = " /from ";
    private static final String TO_MARKER = " /to ";


    public static void main() {
        ArrayList<Task> taskList = new ArrayList<>();
        System.out.println(WELCOME_BANNER);

        Scanner in = new Scanner(System.in);

        while (in.hasNextLine()) {
            String command = in.nextLine().strip();

            if (command.equals("bye")) {
                break;
            }
            handleCommand(command, taskList);
        }
        System.out.println(BYE_MESSAGE);
    }

    /** Processes one user command. */
    private static void handleCommand(String command, ArrayList<Task> taskList){

        try {
            if (command.equals("list")) {
                listAllTasks(taskList);
                return;
            }

            String[] splitCommand = command.split("\\s+");

            if (splitCommand.length > 0 && (splitCommand[0].equals("mark") || splitCommand[0].equals("unmark"))) {

                if (splitCommand.length != 2) {
                    throw new GizmoException("Use the command in this format: mark <task number> or unmark <task number>.");
                }

                int taskListIndex = parseTaskIndex(splitCommand[1], taskList.size());

                if (splitCommand[0].equals("mark")) {
                    handleMarkCommand(taskList, taskListIndex);
                } else if (splitCommand[0].equals("unmark")){
                    handleUnmarkCommand(taskList, taskListIndex);
                }
                return;
            }

            Task task = filterTaskCommand(command);

            taskList.add(task);
            System.out.println(
                    LINE_SEPERATOR
                    + "    added: \n    " + task + "\n"
                    + "    to task list\n"
                    + LINE_SEPERATOR);

        } catch (GizmoException e) {
            System.out.println(
                    LINE_SEPERATOR
                    + "    " + e.getMessage() + "\n"
                    + LINE_SEPERATOR
            );
        }
    }

    /**
     * Converts a one-based task number into a zero-based list index.
     */
    private static int parseTaskIndex(String text, int taskListSize) throws GizmoException {

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new GizmoException("The task number given is not an integer!");
        }

        if (taskNumber < 1) {
            throw new GizmoException("The task number must be a positive integer!");
        }

        if (taskNumber > taskListSize) {
            throw new GizmoException("Failed: you can't mark/unmark a task that doesn't exist :/");
        }
        return taskNumber - 1;
    }

    /** Displays every task currently stored in the task list. */
    private static void listAllTasks(ArrayList<Task> taskList){
        System.out.print(LINE_SEPERATOR);

        if (taskList.isEmpty()){
            System.out.println("    wow, Such empty!");
        }else {
            for (int i=0; i<taskList.size(); i++) {
                Task task = taskList.get(i);
                System.out.println("    " + (i+1) + ". " + task);
            }
        }
        System.out.println(LINE_SEPERATOR);
    }

    /** Marks the task at the given zero-based index as completed. */
    private static void handleMarkCommand(ArrayList<Task> taskList, int taskListIndex){
        System.out.print(LINE_SEPERATOR);
        if (!isValidTaskIndex(taskList, taskListIndex)){
            System.out.println("    failed: You can't to mark a non-existent task :/");
            System.out.println(LINE_SEPERATOR);
            return;
        }

        Task task = taskList.get(taskListIndex);

        if (task.isDone()){
            System.out.println("    somehow, against all odds, you've managed to mark an already marked task!");
        }else{
            task.markAsDone();
            System.out.println(
                "    Nice, I'll be marking this as done!:\n    "
                + task
            );
        }
        System.out.println(LINE_SEPERATOR);
    }

    /** Marks the task at the given zero-based index as incomplete. */
    private static void handleUnmarkCommand(ArrayList<Task> taskList, int taskListIndex){
        System.out.print(LINE_SEPERATOR);
        if (!isValidTaskIndex(taskList, taskListIndex)){
            System.out.println("    failed: you can't unmark something that doesn't exist :/");
            System.out.println(LINE_SEPERATOR);
            return;
        }

        Task task = taskList.get(taskListIndex);

        if (!task.isDone()){
            System.out.println("    unmarking an unmarked task won't magically delete the task :)");
        }else{
            task.unmark();
            System.out.println(
                    "    marking this as undone, be sure to get back to it later!:\n    "
                    + task
            );
        }
        System.out.println(LINE_SEPERATOR);
    }

    /**
     * Checks whether a task index refers to an existing task.
     * @param taskIndex zero-based index into the task list
     */
    private static boolean isValidTaskIndex(ArrayList<Task> taskList, int taskIndex) {
        return (taskIndex >= 0) && (taskIndex < taskList.size());
    }

    /**
     * Parses a task command into the corresponding task type.
     * @return the parsed task
     * @throws GizmoException if the command is invalid
     */
    private static Task filterTaskCommand (String task) throws GizmoException {
        if (task.startsWith(TODO_PREFIX)) {
            return parseTodo(task);
        }
        else if (task.startsWith(DEADLINE_PREFIX)) {
            return parseDeadline(task);
        }
        else if (task.startsWith(EVENT_PREFIX)) {
            return parseEvent(task);
        }
        throw new GizmoException(VALID_COMMANDS);
    }

    /** Parses a todo command and extracts its description. */
    private static Task parseTodo(String task) throws GizmoException {
        String description = task.substring(TODO_PREFIX.length()).strip();
        boolean isAnyStringEmpty = description.isEmpty();
        if (isAnyStringEmpty) {
            throw new GizmoException("the provided description is empty.");
        }

        return new Todo(description);
    }

    /**
     * Parses a deadline command in the form:
     * {@code deadline <description> /by <date or time>}.
     */
    private static Task parseDeadline(String task) throws GizmoException {
        int byMarkerIndex = task.indexOf(BY_MARKER);

        if (byMarkerIndex < DEADLINE_PREFIX.length()){
            throw new GizmoException("A deadline must have a description and use /by <date or time> ");
        }

        String description = task.substring(DEADLINE_PREFIX.length(), byMarkerIndex).strip();
        String completeBy = task.substring(byMarkerIndex + BY_MARKER.length()).strip();

        if (description.isEmpty()) {
            throw new GizmoException("The provided deadline has no description");
        }
        if (completeBy.isEmpty()) {
            throw new GizmoException("A deadline must include a date or time after the `/by` keyword");
        }
        int anotherByMarkerIndex = task.indexOf(BY_MARKER, byMarkerIndex + BY_MARKER.length());

        if (anotherByMarkerIndex >= 0) {
            throw new GizmoException("A deadline should contain only one `/by` marker.");
        }

        return new Deadline(description, completeBy);
    }

    /**
     * Parses an event command in the form:
     * {@code event <description> /from <start> /to <end>}.
     */
    private static Task parseEvent(String task) throws GizmoException {
        int fromMarkerIndex = task.indexOf(FROM_MARKER);
        int toMarkerIndex = task.indexOf(TO_MARKER);
        boolean isMarkerValid = !(fromMarkerIndex < EVENT_PREFIX.length()
                || toMarkerIndex < 0
                || fromMarkerIndex >= toMarkerIndex);

        if (!isMarkerValid){
            throw new GizmoException("An event must have a description and include `/from <start>` and `/to <end>`.");
        }

        String description = task.substring(EVENT_PREFIX.length(), fromMarkerIndex).strip();
        String eventFrom = task.substring(fromMarkerIndex + FROM_MARKER.length(), toMarkerIndex).strip();
        String eventTo = task.substring(toMarkerIndex + TO_MARKER.length()).strip();

        if (description.isEmpty()) {
            throw new GizmoException("The event description provided is empty.");
        }
        if (eventFrom.isEmpty()) {
            throw new GizmoException("An event must include a start time after `/from` marker");
        }
        if (eventTo.isEmpty()) {
            throw new GizmoException("An event must include an end time after `/to` marker");
        }

        int anotherFromMarkerIndex = task.indexOf(FROM_MARKER, fromMarkerIndex + FROM_MARKER.length());
        int anotherToMarkerIndex = task.indexOf(TO_MARKER, toMarkerIndex + TO_MARKER.length());

        if (anotherFromMarkerIndex >= 0 || anotherToMarkerIndex >= 0) {
            throw new GizmoException("An event should contain only one `/from` marker and one `/to` marker.");
        }

        return new Event(description, eventFrom, eventTo);
    }

}

