package view;

import controller.Controller;
import model.exception.MyException;

public class RunExampleCommand extends Command {
    private Controller ctr;

    public RunExampleCommand(String key, String desc, Controller ctr) {
        super(key, desc);
        this.ctr = ctr;
    }

    @Override
    public void execute() {
        try {
            ctr.allSteps();
        } catch (MyException e) {
            System.out.println("Execution error: " + e.getMessage());
        }
    }
}