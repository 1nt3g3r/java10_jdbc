package cli.commands;

import storage.JdbcStorage;

public interface CommandHandler {
    void handleCommand(JdbcStorage storage, String[] args);
}
