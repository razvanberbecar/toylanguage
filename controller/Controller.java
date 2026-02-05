package controller;

import model.state.ProgramState;
import repository.RepoInterface;
import model.exception.MyException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static controller.GarbageCollector.*;

public class Controller implements ControllerInterface {
    boolean viewFlag = false;
    RepoInterface repo;
    private ExecutorService executor;

    public Controller(RepoInterface repo) {
        this.repo = repo;
    }

    public void setViewFlagFalse() {
        this.viewFlag = false;
    }

    public void setViewFlagTrue() {
        this.viewFlag = true;
    }

    @Override
    public void add(ProgramState prg) {
        repo.addPrg(prg);
    }

    @Override
    public List<ProgramState> getAll() {
        return repo.getAllPrgs();
    }


    private List<ProgramState> removeCompletedPrg(List<ProgramState> inPrgList) {
        return inPrgList.stream()
                .filter(ProgramState::isNotCompleted)
                .collect(Collectors.toList());
    }


    private List<ProgramState> oneStepForAllPrg(List<ProgramState> prgList) throws MyException {
        prgList.forEach(p -> {
            try {
                repo.logPrgStateExec(p);
            } catch (Exception ignored) {
            }
        });

        List<Callable<ProgramState>> callList = prgList.stream()
                .map((ProgramState p) -> (Callable<ProgramState>) p::oneStep)
                .collect(Collectors.toList());

        List<ProgramState> newPrgList;
        try {
            newPrgList = executor.invokeAll(callList).stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            // Log exceptions from thread execution
                            System.out.println(e.getMessage());
                            return null;
                        }
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MyException("Execution interrupted: " + e.getMessage());
        }

        // Schedule newly forked programs to run before their parents in the next step
        if (!newPrgList.isEmpty()) {
            List<ProgramState> combined = new ArrayList<>(newPrgList.size() + prgList.size());
            combined.addAll(newPrgList);
            combined.addAll(prgList);
            prgList = combined;
        }

        prgList.forEach(p -> {
            try {
                repo.logPrgStateExec(p);
            } catch (Exception ignored) {
            }
        });

        repo.setPrgLst(prgList);

        return prgList;
    }

    public List<ProgramState> runOneStepForAll() throws MyException {
        executor = Executors.newFixedThreadPool(2);
        try {
            List<ProgramState> prgList = removeCompletedPrg(repo.getPrgLst());
            if (prgList.isEmpty()) {
                throw new MyException("Nothing left to execute");
            }

            var heap = prgList.get(0).getHeap();
            var heapContent = heap.getContent();
            Collection<?> allSymValues = prgList.stream()
                    .flatMap(p -> p.getSymbolTable().getContent().values().stream())
                    .collect(Collectors.toList());
            @SuppressWarnings("unchecked")
            List<Integer> usableAddresses = getAddrFromSymTable((Collection) allSymValues, heapContent);
            var newHeapContent = safeGarbageCollector(usableAddresses, heapContent);
            heap.setContent(newHeapContent);

            oneStepForAllPrg(prgList);

            return removeCompletedPrg(repo.getPrgLst());
        } finally {
            executor.shutdownNow();
        }
    }

    @Override
    public void allSteps() throws MyException {
        executor = Executors.newFixedThreadPool(2);
        List<ProgramState> prgList = removeCompletedPrg(repo.getPrgLst());
        try {
            while (!prgList.isEmpty()) {
                // Garbage Collection
                var heap = prgList.get(0).getHeap();
                var heapContent = heap.getContent();
                Collection<?> allSymValues = prgList.stream()
                        .flatMap(p -> p.getSymbolTable().getContent().values().stream())
                        .collect(Collectors.toList());
                @SuppressWarnings("unchecked")
                List<Integer> usableAddresses = getAddrFromSymTable((Collection) allSymValues, heapContent);
                var newHeapContent = safeGarbageCollector(usableAddresses, heapContent);
                heap.setContent(newHeapContent);

                // Execute one step and get the updated list
                oneStepForAllPrg(prgList);

                // Filter out completed programs for the next loop iteration
                prgList = removeCompletedPrg(repo.getPrgLst());
            }
        } finally {
            executor.shutdownNow();
            // Set the final (empty) list in the repository
            repo.setPrgLst(prgList);
        }
    }


    private void display(ProgramState state) {
        System.out.println(state);
    }
}
