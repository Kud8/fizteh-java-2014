package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Commands.*;

/**
 * Created by Дмитрий on 08.10.14.
 */
public class Connector {
    private final Path dbRoot;
    private Map<String, StoreableTable> tables;
    private StoreableTable activeTable;
    private final StoreableTableProvider activeTableProvider;
    private final ReentrantReadWriteLock lock;

    public final Map<String, Command> commands = new HashMap<>();

    public Map<String, StoreableTable> getTables() {
        lock.readLock().lock();
        try {
            return tables;
        } finally {
            lock.readLock().unlock();
        }
    }

    public StoreableTable getActiveTable() {
        lock.readLock().lock();
        try {
            return activeTable;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setActiveTable(StoreableTable activeTable) {
            this.activeTable = activeTable;
    }

    public StoreableTableProvider getActiveTableProvider() {
        lock.readLock().lock();
        try {
            return activeTableProvider;
        } finally {
            lock.readLock().unlock();
        }
    }


    public Connector(Path dbPath) throws IOException {
        if (!Files.exists(dbPath)) {
            System.err.println("destination does not exist");
            System.exit(-1);
        }
        if (!Files.isDirectory(dbPath)) {
            System.err.println("destination is not a directory");
            System.exit(-1);
        }
        activeTableProvider = new StoreableTableProvider(dbPath.toString());
        dbRoot = dbPath;
        lock = new ReentrantReadWriteLock();
        open();

        Command[] commandsArray = {new Create(), new Drop(), new Use(), new Show(),
                                    new Put(), new Get(), new List(), new Remove(),
                                    new Commit(), new Rollback(), new Size()};
        for (Command command : commandsArray) {
            commands.put(command.name, command);
        }
    }

    private void open() {
        if (tables == null) {
            tables = new HashMap<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dbRoot)) {
                for (Path file : stream) {
                    if (Files.isDirectory(file)) {
                        StoreableTable table;
                        try {
                        table = new StoreableTable(file);
                        } catch (IOException e) {
                            System.err.println("can't create directory: " + file.toString());
                            table = null;
                            System.exit(-1);
                        }
                        table.readDb();
                        tables.put(file.getFileName().toString(), table);
                    }
                }
            } catch (IOException e) {
                System.err.println("can't load the database");
                System.exit(-1);
            }
        }
    }

    public void close() {
        if (tables != null) {
            for (StoreableTable table : tables.values()) {
                table.unload(table, table.getName());
            }
        }
        if (activeTable != null) {
            activeTable.unload(activeTable, activeTable.getName());
        }
    }

    public boolean run(String name, String[] args, boolean batchMode, boolean batchModeInInteractive) {
        Command command = commands.get(name);
        command.batchMode = batchMode;
        command.batchModeInInteractive = batchModeInInteractive;
        if (command != null) {
            if (!command.exec(this, args)) {
                return false;
            }
        } else if (!args[0].equals("")) {
            System.err.println(args[0] + " : command not found");
            return false;
        }
        return true;
    }
}
