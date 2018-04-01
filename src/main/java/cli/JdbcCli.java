package cli;

import cli.commands.CommandHandler;
import cli.commands.impl.CreateCatCommandHandler;
import cli.commands.impl.ListCatsCommandHandler;
import cli.commands.impl.SetCatOwnerCommandHandler;
import storage.JdbcStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class JdbcCli {
    private JdbcStorage storage;

    private Scanner scanner;
    boolean end;

    private String command;
    private String[] commandArgs;

    private Map<String, CommandHandler> commandHandlers;

    public JdbcCli() {
        storage = new JdbcStorage();
        scanner = new Scanner(System.in);

        initCommandHandlers();

        while (!end) {
            readCommand();
            handleCommand();
        }
    }

    private void initCommandHandlers() {
        commandHandlers = new HashMap<String, CommandHandler>();

        commandHandlers.put("createCat", new CreateCatCommandHandler());
        commandHandlers.put("listCats", new ListCatsCommandHandler());
        commandHandlers.put("setCatOwner", new SetCatOwnerCommandHandler());
    }

    private void readCommand(){
        String rawCommand = scanner.nextLine();

        String[] rawCommandParts = rawCommand.split(" ");

        command = rawCommandParts[0];

        commandArgs = new String[rawCommandParts.length - 1];
        for(int i = 0; i < commandArgs.length; i++) {
            commandArgs[i] = rawCommandParts[i+1];
        }
    }

    private void handleCommand() {
        if (command.equals("exit")) {
            end = true;
            return;
        }

        if (!commandHandlers.containsKey(command)) {
            System.out.println("Unknown command: " + command);
            return;
        }

        commandHandlers.get(command).handleCommand(storage, commandArgs);
    }

    public static void main(String[] args) {
        new JdbcCli();
    }
}
