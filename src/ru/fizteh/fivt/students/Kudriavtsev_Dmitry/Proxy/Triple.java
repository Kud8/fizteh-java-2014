package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy;

/**
 * Created by Дмитрий on 25.12.2014.
 */
public class Triple<T1, T2, T3> {
    public T1 path;
    public T2 directory;
    public T3 file;
    public Triple(T1 path, T2 directory, T3 file) {
        this.path = path;
        this.directory = directory;
        this.file = file;
    }
}
