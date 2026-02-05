package repository;

import model.exception.MyException;
import model.state.ProgramState;
import java.util.List;

public interface RepoInterface {
    void addPrg(ProgramState state);
    List<ProgramState> getAllPrgs();
    void logPrgStateExec(ProgramState prgState) throws MyException;
    List<ProgramState> getPrgLst();
    void setPrgLst(List<ProgramState> lst);
}
