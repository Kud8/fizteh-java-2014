package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Welcome;

import java.io.PrintStream;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class Remove extends StoreableCommand {
    public Remove() {
        super("remove", 1);
    }

    @Override
    public boolean exec(Welcome dbConnector, String[] args, PrintStream out, PrintStream err) {
        if (!checkArguments(args.length, err)) {
            return !batchModeInInteractive;
        }
        if (dbConnector.getActiveTable() == null) {
            if (batchModeInInteractive) {
                err.println("No table");
                return false;
            }
            noTable(err);
            return true;
        }
        if (dbConnector.getActiveTable().remove(args[0]) != null) {
            out.println("removed");
            dbConnector.getActiveTable().getChangedFiles().put(
                    dbConnector.getActiveTable().whereToSave("", args[0]).path, 0);
        } else {
            err.println("not found");
            if (batchModeInInteractive) {
                return false;
            }
        }
        return true;
    }

}
