package model.state;

import model.exception.MyException;
import model.value.StringValue;
import java.io.BufferedReader;

public interface FileTable {
    boolean isDefined(StringValue fileName);
    void add (StringValue filename, BufferedReader fileDescriptor) throws MyException;
    BufferedReader get(StringValue fileName);
    void remove(StringValue fileName) throws MyException;
    String toString();
}
