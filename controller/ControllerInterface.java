package controller;

import model.state.ProgramState;
import model.exception.MyException;
import java.util.List;

public interface ControllerInterface {
    void add(ProgramState prg);
    List<ProgramState> getAll();
    void allSteps() throws MyException;
}
