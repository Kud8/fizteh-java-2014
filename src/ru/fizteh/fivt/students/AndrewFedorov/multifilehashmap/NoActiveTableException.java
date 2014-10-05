package ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap;

public class NoActiveTableException extends Exception {
    private static final long serialVersionUID = 7115979649947958701L;

    public NoActiveTableException() {
	super("No active table is chosen in current database");
    }
}
