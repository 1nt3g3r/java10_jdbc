package cli.commands.impl;

import cli.commands.CommandHandler;
import entity.Cat;
import entity.Owner;
import storage.JdbcStorage;

import java.util.List;

public class ListCatsCommandHandler implements CommandHandler {
    public void handleCommand(JdbcStorage storage, String[] args) {
        List<Cat> allCats = storage.getAllCats();

        printLine(80);

        //Header
        printCell("NAME", true, false);
        printCell("WEIGHT", false, false);
        printCell("SEX", false, false);
        printCell("OWNER", false, true);
        System.out.println();

        printLine(80);

        for(Cat cat: allCats) {
            printCell(cat.getCatName(), true, true);
            printCell(cat.getWeight() + "", false, true);
            printCell(cat.getSex() + "", false, true);
            printCell(getCatOwner(cat, storage), false, true);
            System.out.println();
        }

        printLine(80);
    }

    private String getCatOwner(Cat cat, JdbcStorage storage) {
        Owner owner = storage.getOwnerById(cat.getOwnerId());

        if (owner == null) {
            return "<no owner>";
        }

        return owner.getFirstName() + ", " + owner.getLastName();
    }

    private void printLine(int width) {
        for(int i = 0; i < width; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    private void printCell(String value, boolean leftBorder, boolean rightBorder) {
        StringBuilder builder = new StringBuilder();
        if (leftBorder) {
            builder.append("|");
        }
        builder.append(value);

        int spaceCount = 20 - builder.length();
        if (rightBorder) {
            spaceCount--;
        }

        for(int i = 0; i < spaceCount; i++) {
            builder.append(" ");
        }

        if (rightBorder) {
            builder.append("|");
        }

        System.out.print(builder.toString());
    }
}
