import java.util.Scanner;
import java.util.ArrayList;

public class Gizmo {
    private static ArrayList<String> taskList = new ArrayList<>();
    private static ArrayList<Boolean> taskCheckList = new ArrayList<>();

    public static void main() {
        String banner = "    ________________________________________________________________________________________\n"
            + "    Hello! I'm Gizmo. \n"
            + "    How may I assist you today?\n"
            + "    ________________________________________________________________________________________\n";
        System.out.println(banner);
        handleCommands(taskList);
    }

    private static void handleCommands(ArrayList<String> taskList){
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
                handleMarkCommands(taskCheckList, Integer.parseInt(splitCommand[1]) - 1);
            }else {
                taskList.add(command);
                taskCheckList.add(false);
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

    private static void listAllTasks(ArrayList<String> taskList){
        System.out.println("    ________________________________________________________________________________________");

        if (taskList.isEmpty()){
            System.out.println("    wow, Such empty!");
        }else {
            for (int i=0; i<taskList.size(); i++) {
                String status = taskCheckList.get(i) ? "[X]" : "[ ]";
                System.out.println(
                        "    " + (i+1) + ". " + status + " " + taskList.get(i)
                );
            }
        }
        System.out.println("    ________________________________________________________________________________________\n");
    }

    private static void handleMarkCommands(ArrayList<Boolean> taskCheckList, int taskListIndex){
        System.out.println("    ________________________________________________________________________________________");
        if (taskListIndex >= taskCheckList.size() || taskListIndex < 0){
            System.out.println("    failed: You tried to mark a non-existent task");
        }else if (taskCheckList.get(taskListIndex)){
            System.out.println("    you tried to mark a marked item");
        }else{
            System.out.println(
                "    Nice, I'll be marking this as done!:\n"
                + "    [X] " + taskList.get(taskListIndex)
            );
            taskCheckList.set(taskListIndex, true);
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

