package repository;

import model.exception.MyException;
import model.state.ProgramState;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Repository implements RepoInterface {
    private List<ProgramState> programStates;
    private final String logFilePath;

    public Repository(String logFilePath) {
        this.programStates = new ArrayList<>();
        this.logFilePath = logFilePath;
    }

    public void addPrg(ProgramState state) {
        this.programStates.add(state);
    }

    @Override
    public List<ProgramState> getAllPrgs() {
        return this.programStates;
    }

    @Override
    public List<ProgramState> getPrgLst() {
        return this.programStates;
    }

    @Override
    public void setPrgLst(List<ProgramState> lst) {
        this.programStates = lst;
    }

    @Override
    public void logPrgStateExec(ProgramState prgState) throws MyException {
        try (PrintWriter logFile = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true)))) {
            logFile.println(prgState.toString());
        } catch (IOException e) {
            throw new MyException("Error writing to log file: " + e.getMessage());
        }
    }

}
