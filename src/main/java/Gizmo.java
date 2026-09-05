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
            LINE_SEPERATOR
            + "    invalid command :P\n"
            + "    valid commands are:\n"
            + "    todo...\n"
            + "    deadline.../by...\n"
            + "    event.../from.../to...\n"
            + "    mark {task number}\n"
            + "    unmark {task number}\n"
            + "    list\n"
            + "    bye\n"
            + LINE_SEPERATOR;

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

        String[] splitCommand = command.split("\\s+");

        if (command.equals("list")){
            listAllTasks(taskList);
        }else if (splitCommand.length == 2 && splitCommand[0].equals("mark") && isInteger(splitCommand[1])){
            handleMarkCommand(taskList, Integer.parseInt(splitCommand[1]) - 1);
        }else if (splitCommand.length == 2 && splitCommand[0].equals("unmark") && isInteger(splitCommand[1])) {
            handleUnmarkCommand(taskList, Integer.parseInt(splitCommand[1]) - 1);
        }else {

            Task task = filterTaskCommand(command);
            if (task != null) {
                taskList.add(task);
                System.out.println(
                        LINE_SEPERATOR
                        + "    added: \n    " + task + "\n"
                        + "    to task list\n"
                        + LINE_SEPERATOR
                );

            } else {
                System.out.println(VALID_COMMANDS);
            }
        }
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

    /** Returns whether the supplied text represents an integer. */
    private static boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parses a task command into the corresponding task type.
     * @return the parsed task, or null if the command is invalid
     */
    private static Task filterTaskCommand (String task){
        if (task.startsWith(TODO_PREFIX)) {
            return parseTodo(task);
        }
        else if (task.startsWith(DEADLINE_PREFIX)) {
            return parseDeadline(task);
        }
        else if (task.startsWith(EVENT_PREFIX)) {
            return parseEvent(task);
        }
        else {
            return null;
        }
    }

    /** Parses a todo command and extracts its description. */
    private static Task parseTodo(String task) {
        String description = task.substring(TODO_PREFIX.length()).strip();
        boolean isAnyStringEmpty = description.isEmpty();

        return isAnyStringEmpty ? null : new Todo(description);
    }

    /**
     * Parses a deadline command in the form:
     * {@code deadline <description> /by <date or time>}.
     */
    private static Task parseDeadline(String task) {
        int byMarkerIndex = task.indexOf(BY_MARKER);

        if (byMarkerIndex < 0){
            return null;
        }

        String[] taskParts = task.split(BY_MARKER, 2);
        String description = taskParts[0].substring(DEADLINE_PREFIX.length()).strip();
        String completeBy = taskParts[1].strip();
        boolean isAnyStringEmpty = description.isEmpty() || completeBy.isEmpty();

        return isAnyStringEmpty ? null : new Deadline(description, completeBy);
    }

    /**
     * Parses an event command in the form:
     * {@code event <description> /from <start> /to <end>}.
     */
    private static Task parseEvent(String task) {
        int fromMarkerIndex = task.indexOf(FROM_MARKER);
        int toMarkerIndex = task.indexOf(TO_MARKER);
        boolean isMarkerValid = !(fromMarkerIndex < 0
                || toMarkerIndex < 0
                || fromMarkerIndex >= toMarkerIndex);

        if (!isMarkerValid){
            return null;
        }

        String[] taskParts = task.split(FROM_MARKER, 2);
        String[] eventDurationParts = taskParts[1].split(TO_MARKER, 2);
        String description = taskParts[0].substring(EVENT_PREFIX.length()).strip();
        String eventFrom = eventDurationParts[0].strip();
        String eventTo = eventDurationParts[1].strip();
        boolean isAnyStringEmpty = description.isEmpty() || eventFrom.isEmpty() || eventTo.isEmpty();

        return isAnyStringEmpty ? null : new Event(description, eventFrom, eventTo);
    }

}

