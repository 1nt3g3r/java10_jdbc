package cli.commands.impl;

import cli.commands.CommandHandler;
import entity.Cat;
import storage.JdbcStorage;

/**
 * createCat catName, weight, sex
 */
public class CreateCatCommandHandler implements CommandHandler {
    public void handleCommand(JdbcStorage storage, String[] args) {
        String catName = args[0];
        float catWeight = Float.parseFloat(args[1]);
        boolean catSex = args[2].equalsIgnoreCase("boy");

        Cat cat = new Cat();
        cat.setCatName(catName);
        cat.setWeight(catWeight);
        cat.setSex(catSex);

        storage.createCat(cat);
    }
}
