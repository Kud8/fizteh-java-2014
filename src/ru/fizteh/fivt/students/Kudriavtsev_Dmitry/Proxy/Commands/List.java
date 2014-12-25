package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Welcome;

import java.io.PrintStream;
import java.util.StringJoiner;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class List extends StoreableCommand {
    public List() {
        super("list", 0);
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
        java.util.List<String> keySet = dbConnector.getActiveTable().list();
        StringJoiner strJoin = new StringJoiner(" ,");
        for (String key : keySet) {
            strJoin.add(key);
        }
        out.println(strJoin.toString());
        return true;
    }
}
