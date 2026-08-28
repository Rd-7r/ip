import java.util.Scanner;

public class Gizmo {
    public static void main() {
        String banner = "    ________________________________________________________________________________________\n"
            + "    Hello! I'm Gizmo. \n"
            + "    How may I assist you today?\n"
            + "    ________________________________________________________________________________________\n";
        System.out.println(banner);
        runEcho();
    }

    private static void runEcho(){
        Scanner in = new Scanner(System.in);
        String command = in.nextLine();

        while (!command.strip().equals("bye")){
            System.out.println(
                "    ____________________________________________________________________________________\n"
                + "    " + command + "\n"
                + "    ____________________________________________________________________________________\n"
            );
            command = in.nextLine();
        }

        System.out.println(
            "    ________________________________________________________________________________________\n"
            + "    Disconnecting... I'll be here whenever you need me again.\n"
            + "    ________________________________________________________________________________________\n"
        );
    }
}

