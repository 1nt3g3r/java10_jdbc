package cli.commands.impl;

import cli.commands.CommandHandler;
import entity.Cat;
import entity.Owner;
import storage.JdbcStorage;

/**
 * setCatOwner cat_id owner_id
 */
public class SetCatOwnerCommandHandler implements CommandHandler {
    public void handleCommand(JdbcStorage storage, String[] args) {
        if (args.length < 2) {
            System.out.println("Not enough params");
            return;
        }

        long catId = 0;
        long ownerId = 0;

        try {
            catId = Long.parseLong(args[0]);
            ownerId = Long.parseLong(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("Wrong command format");
            return;
        }

        Cat cat = storage.getCatById(catId);
        if (cat == null) {
            System.out.println("Cat not found");
            return;
        }

        Owner owner = storage.getOwnerById(ownerId);
        if (owner == null) {
            System.out.println("Owner not found");
            return;
        }

        storage.setOwnerForCat(cat, owner);
    }
}
