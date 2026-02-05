package model.state;

import model.exception.MyException;
import model.value.StringValue;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class MapFileTable implements FileTable{
    private final Map<String, BufferedReader> fileTable = new HashMap<>();

    @Override
    public boolean isDefined(StringValue fileName) {
        return fileTable.containsKey(fileName.value());
    }

    @Override
    public void add(StringValue filename, BufferedReader fileDescriptor) throws MyException {
        if (isDefined(filename)) { throw new MyException("file already defined");}
        fileTable.put(filename.value(), fileDescriptor);
    }

    @Override
    public BufferedReader get(StringValue fileName) {
        return fileTable.get(fileName.value());
    }

    @Override
    public void remove(StringValue fileName) throws MyException {
        if (!isDefined(fileName)) {
            throw new MyException("File not opened: " + fileName.value());
        }
        fileTable.remove(fileName.value());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String fileName : fileTable.keySet()) {
            sb.append(fileName).append("\n");
        }
        return sb.toString();
    }
}
