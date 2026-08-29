import java.util.Scanner;
import java.util.ArrayList;

public class Gizmo {
    private static ArrayList<Task> taskList = new ArrayList<>();

    public static void main() {
        String banner = "    ________________________________________________________________________________________\n"
            + "    Hello! I'm Gizmo. \n"
            + "    How may I assist you today?\n"
            + "    ________________________________________________________________________________________\n";
        System.out.println(banner);
        handleCommands(taskList);
    }

    private static void handleCommands(ArrayList<Task> taskList){
        Scanner in = new Scanner(System.in);

        while (in.hasNextLine()){
            String command = in.nextLine();
            String trimmedCommand = command.strip();

            if (trimmedCommand.equals("bye")) {
                break;
            }

            String[] splitCommand = trimmedCommand.split("\\s+");

            if (trimmedCommand.equals("list")){
                listAllTasks(taskList);
            }else if (splitCommand.length == 2 && splitCommand[0].equals("mark") && isInteger(splitCommand[1])){
                handleMarkCommand(Integer.parseInt(splitCommand[1]) - 1);
            }else if (splitCommand.length == 2 && splitCommand[0].equals("unmark") && isInteger(splitCommand[1])) {
                handleUnmarkCommand(Integer.parseInt(splitCommand[1]) - 1);
            }else {
                taskList.add(new Task(command));
                System.out.println(
                    "    ____________________________________________________________________________________\n"
                    + "    added: " + command + "\n"
                    + "    to word list\n"
                    + "    ____________________________________________________________________________________\n"
                );
            }
        }

        System.out.println(
            "    ________________________________________________________________________________________\n"
            + "    Disconnecting... I'll be here whenever you need me again.\n"
            + "    ________________________________________________________________________________________\n"
        );
    }

    private static void listAllTasks(ArrayList<Task> taskList){
        System.out.println("    ________________________________________________________________________________________");

        if (taskList.isEmpty()){
            System.out.println("    wow, Such empty!");
        }else {
            for (int i=0; i<taskList.size(); i++) {
                Task task = taskList.get(i);
                System.out.println(
                        "    " + (i+1) + ". " + task.getStatusIcon() + " " + task.getDescription()
                );
            }
        }
        System.out.println("    ________________________________________________________________________________________\n");
    }

    private static void handleMarkCommand(int taskListIndex){
        System.out.println("    ________________________________________________________________________________________");
        if (taskListIndex >= taskList.size() || taskListIndex < 0){
            System.out.println("    failed: You can't to mark a non-existent task :/");
            System.out.println("    ________________________________________________________________________________________\n");
            return;
        }

        Task task = taskList.get(taskListIndex);

        if (task.isDone()){
            System.out.println("    somehow, against all odds, you've managed to mark an already marked task!");
        }else{
            System.out.println(
                "    Nice, I'll be marking this as done!:\n"
                + "    [X] " + task.getDescription()
            );
            task.markAsDone();
        }
        System.out.println("    ________________________________________________________________________________________\n");
    }

    private static void handleUnmarkCommand(int taskListIndex){
        System.out.println("    ________________________________________________________________________________________");
        if (taskListIndex >= taskList.size() || taskListIndex < 0){
            System.out.println("    failed: you can't unmark something that doesn't exist :/");
            System.out.println("    ________________________________________________________________________________________\n");
            return;
        }

        Task task = taskList.get(taskListIndex);

        if (!task.isDone()){
            System.out.println("    unmarking an unmarked task won't magically delete the task :)");
        }else{
            System.out.println(
                    "    marking this as undone, be sure to get back to it later!:\n"
                            + "    [ ] " + task.getDescription()
            );
            task.unmark();
        }
        System.out.println("    ________________________________________________________________________________________\n");
    }

    private static boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

