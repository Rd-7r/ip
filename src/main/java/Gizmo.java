import java.util.Scanner;
import java.util.ArrayList;

public class Gizmo {
    private static ArrayList<String> taskList = new ArrayList<>();

    public static void main() {
        String banner = "    ________________________________________________________________________________________\n"
            + "    Hello! I'm Gizmo. \n"
            + "    How may I assist you today?\n"
            + "    ________________________________________________________________________________________\n";
        System.out.println(banner);
        addToList(taskList);
    }

    private static void addToList(ArrayList<String> taskList){
        Scanner in = new Scanner(System.in);
        String command = in.nextLine();

        while (!command.strip().equals("bye")){
            if (command.strip().equals("list")){
                listAllTasks(taskList);
            }else {
                taskList.add(command);
                System.out.println(
                    "    ____________________________________________________________________________________\n"
                    + "    added: " + command + "\n"
                    + "    to word list\n"
                    + "    ____________________________________________________________________________________\n"
                );
            }
            command = in.nextLine();
        }

        System.out.println(
            "    ________________________________________________________________________________________\n"
            + "    Disconnecting... I'll be here whenever you need me again.\n"
            + "    ________________________________________________________________________________________\n"
        );
    }

    private static void listAllTasks(ArrayList<String> taskList){
        int taskNumber = 0;
        System.out.println("    ________________________________________________________________________________________");

        if (taskList.isEmpty()){
            System.out.println("wow, Such empty!");
        }else {
            for (String task : taskList) {
                taskNumber++;

                System.out.println(
                        "    " + taskNumber + ". " + task
                );
            }
        }
        System.out.println("    ________________________________________________________________________________________\n");
    }
}

