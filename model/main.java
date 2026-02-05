
// Remove the old View import since we're not using it anymore
import model.type.*;
import view.TextMenu;
import view.ExitCommand;
import view.RunExampleCommand;
import model.exception.MyException;
import model.statement.*;
import model.expression.*;
import model.value.*;
import model.type.Type;
import model.state.*;
import controller.Controller;
import repository.Repository;


void main() throws MyException {
    // Example 1: int v; v = 2; print(v)
    Statement ex1 = new CompoundStatement(
            new VariableDeclarationStatement(new IntType(), "v"),
            new CompoundStatement(
                    new AssignmentStatement(new ConstantExpression(new IntegerValue(2)), "v"),
                    new PrintStatement(new VariableExpression("v"))
            )
    );

    ex1.typecheck(new MapTypeEnvironment());

    ProgramState prg1 = new ProgramState(
            new StackExecutionStack(),
            new MapSymbolTable(),
            new ListOut(),
            ex1,
            new MapFileTable(),
            new MapHeap()
    );
    Repository repo1 = new Repository("log1.txt");
    Controller ctr1 = new Controller(repo1);
    ctr1.setViewFlagTrue();
    ctr1.add(prg1);

    // Example 2: int a, int b, a=2+3*5, b=a+1, print(b)
    Statement ex2 = new CompoundStatement(
            new VariableDeclarationStatement(new IntType(), "a"),
            new CompoundStatement(
                    new VariableDeclarationStatement(new IntType(), "b"),
                    new CompoundStatement(
                            new AssignmentStatement(
                                    new ArithmeticalExpression("+",
                                            new ConstantExpression(new IntegerValue(2)),
                                            new ArithmeticalExpression(
                                                    "*",
                                                    new ConstantExpression(new IntegerValue(3)),
                                                    new ConstantExpression(new IntegerValue(5))
                                            )
                                    ), "a"
                            ),
                            new CompoundStatement(
                                    new AssignmentStatement(
                                            new ArithmeticalExpression("+",
                                                    new VariableExpression("a"),
                                                    new ConstantExpression(new IntegerValue(1))
                                            ), "b"
                                    ),
                                    new PrintStatement(new VariableExpression("b"))
                            )
                    )
            )
    );

    ex2.typecheck(new MapTypeEnvironment());

    ProgramState prg2 = new ProgramState(
            new StackExecutionStack(),
            new MapSymbolTable(),
            new ListOut(),
            ex2,
            new MapFileTable(),
            new MapHeap()
    );
    Repository repo2 = new Repository("log2.txt");
    Controller ctr2 = new Controller(repo2);
    ctr2.setViewFlagTrue();
    ctr2.add(prg2);

    // Example 3: bool a; int v; a=false; (If a Then v=2 Else v=3); Print(v)
    Statement ex3 = new CompoundStatement(
            new VariableDeclarationStatement(new BoolType(), "a"),
            new CompoundStatement(
                    new VariableDeclarationStatement(new IntType(), "v"),
                    new CompoundStatement(
                            new AssignmentStatement(new ConstantExpression(new BooleanValue(false)), "a"),
                            new CompoundStatement(
                                    new IfStatement(
                                            new VariableExpression("a"),
                                            new AssignmentStatement(new ConstantExpression(new IntegerValue(2)), "v"),
                                            new AssignmentStatement(new ConstantExpression(new IntegerValue(3)), "v")
                                    ),
                                    new PrintStatement(new VariableExpression("v"))
                            )
                    )
            )
    );

    ex3.typecheck(new MapTypeEnvironment());

    ProgramState prg3 = new ProgramState(
            new StackExecutionStack(),
            new MapSymbolTable(),
            new ListOut(),
            ex3,
            new MapFileTable(),
            new MapHeap()
    );
    Repository repo3 = new Repository("log3.txt");
    Controller ctr3 = new Controller(repo3);
    ctr3.setViewFlagTrue();
    ctr3.add(prg3);

    // Example 4: string varf; varf="test.in"; openRFile(varf); int varc; readFile(varf,varc); print(varc); readFile(varf,varc); print(varc); closeRFile(varf)
    Statement ex4 = new CompoundStatement(
            new VariableDeclarationStatement(new StringType(), "varf"),
            new CompoundStatement(
                    new AssignmentStatement(new ConstantExpression(new StringValue("test.in")), "varf"),
                    new CompoundStatement(
                            new OpenRFileStatement(new VariableExpression("varf")),
                            new CompoundStatement(
                                    new VariableDeclarationStatement(new IntType(), "varc"),
                                    new CompoundStatement(
                                            new ReadFileStatement(new VariableExpression("varf"), "varc"),
                                            new CompoundStatement(
                                                    new PrintStatement(new VariableExpression("varc")),
                                                    new CompoundStatement(
                                                            new ReadFileStatement(new VariableExpression("varf"), "varc"),
                                                            new CompoundStatement(
                                                                    new PrintStatement(new VariableExpression("varc")),
                                                                    new CloseRFileStatement(new VariableExpression("varf"))
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            )
    );

    ex4.typecheck(new MapTypeEnvironment());

    ProgramState prg4 = new ProgramState(
            new StackExecutionStack(),
            new MapSymbolTable(),
            new ListOut(),
            ex4,
            new MapFileTable(),
            new MapHeap()
    );
    Repository repo4 = new Repository("log4.txt");
    Controller ctr4 = new Controller(repo4);
    ctr4.setViewFlagTrue();
    ctr4.add(prg4);

    // Example 5: Ref int v; new(v,20); Ref Ref int a; new(a,v); print(v); print(a)
    // Expected: Heap={1->20, 2->(1,int)}, SymTable={v->(1,int), a->(2,Ref int)} and Out={(1,int),(2,Ref int)}
    Statement ex5 = new CompoundStatement(
            new VariableDeclarationStatement(new RefType(new IntType()), "v"),
            new CompoundStatement(
                    new HeapAllocationStatement("v", new ConstantExpression(new IntegerValue(20))),
                    new CompoundStatement(
                            new VariableDeclarationStatement(new RefType(new RefType(new IntType())), "a"),
                            new CompoundStatement(
                                    new HeapAllocationStatement("a", new VariableExpression("v")),
                                    new CompoundStatement(
                                            new PrintStatement(new VariableExpression("v")),
                                            new PrintStatement(new VariableExpression("a"))
                                    )
                            )
                    )
            )
    );
    
    ex5.typecheck(new MapTypeEnvironment());
    
    ProgramState prg5 = new ProgramState(
            new StackExecutionStack(),
            new MapSymbolTable(),
            new ListOut(),
            ex5,
            new MapFileTable(),
            new MapHeap()
    );
    Repository repo5 = new Repository("log5.txt");
    Controller ctr5 = new Controller(repo5);
    ctr5.setViewFlagTrue();
    ctr5.add(prg5);

    // Example 6: Ref int v; new(v,20); Ref Ref int a; new(a,v); print(rH(v)); print(rH(rH(a))+5)
    Statement ex6 = new CompoundStatement(
            new VariableDeclarationStatement(new RefType(new IntType()), "v"),
            new CompoundStatement(
                    new HeapAllocationStatement("v", new ConstantExpression(new IntegerValue(20))),
                    new CompoundStatement(
                            new VariableDeclarationStatement(new RefType(new RefType(new IntType())), "a"),
                            new CompoundStatement(
                                    new HeapAllocationStatement("a", new VariableExpression("v")),
                                    new CompoundStatement(
                                            new PrintStatement(new ReadHeapExpression(new VariableExpression("v"))),
                                            new PrintStatement(
                                                    new ArithmeticalExpression("+",
                                                            new ReadHeapExpression(
                                                                    new ReadHeapExpression(new VariableExpression("a"))
                                                            ),
                                                            new ConstantExpression(new IntegerValue(5))
                                                    )
                                            )
                                    )
                            )
                    )
            )
    );
    
    ex6.typecheck(new MapTypeEnvironment());
    
    ProgramState prg6 = new ProgramState(
            new StackExecutionStack(),
            new MapSymbolTable(),
            new ListOut(),
            ex6,
            new MapFileTable(),
            new MapHeap()
    );
    Repository repo6 = new Repository("log6.txt");
    Controller ctr6 = new Controller(repo6);
    ctr6.setViewFlagTrue();
    ctr6.add(prg6);

    //Example 7: Ref int v; new(v,20); print(rH(v)); wH(v,30); print(rH(v)+5)
    Statement ex7 = new CompoundStatement(
            new VariableDeclarationStatement(new RefType(new IntType()), "v"),
            new CompoundStatement(
                    new HeapAllocationStatement("v", new ConstantExpression(new IntegerValue(20))),
                    new CompoundStatement(
                            new PrintStatement(new ReadHeapExpression(new VariableExpression("v"))),
                            new CompoundStatement(
                                    new WriteHeapStatement("v", new ConstantExpression(new IntegerValue(30))),
                                    new PrintStatement(
                                            new ArithmeticalExpression("+",
                                                    new ReadHeapExpression(new VariableExpression("v")),
                                                    new ConstantExpression(new IntegerValue(5)))
                                    )
                            )
                    )
            )
    );
    
    ex7.typecheck(new MapTypeEnvironment());
    
    ProgramState prg7 = new ProgramState(
            new StackExecutionStack(),
            new MapSymbolTable(),
            new ListOut(),
            ex7,
            new MapFileTable(),
            new MapHeap()
    );
    Repository repo7 = new Repository("log7.txt");
    Controller ctr7 = new Controller(repo7);
    ctr7.setViewFlagTrue();
    ctr7.add(prg7);

    // Example 8: int v; v=4; while (v>0) { print(v); v=v-1; }; print(v)
    Statement ex8 = new CompoundStatement(
            new VariableDeclarationStatement(new IntType(), "v"),
            new CompoundStatement(
                    new AssignmentStatement(new ConstantExpression(new IntegerValue(4)), "v"),
                    new CompoundStatement(
                            new WhileStatement(
                                    new RelationalExpression(
                                            ">",
                                            new VariableExpression("v"),
                                            new ConstantExpression(new IntegerValue(0))
                                    ),
                                    new CompoundStatement(
                                            new PrintStatement(new VariableExpression("v")),
                                            new AssignmentStatement(
                                                    new ArithmeticalExpression("-",
                                                            new VariableExpression("v"),
                                                            new ConstantExpression(new IntegerValue(1))
                                                    ),
                                                    "v"
                                            )
                                    )
                            ),
                            new PrintStatement(new VariableExpression("v"))
                    )
            )
    );
    
    ex8.typecheck(new MapTypeEnvironment());
    
    ProgramState prg8 = new ProgramState(
            new StackExecutionStack(),
            new MapSymbolTable(),
            new ListOut(),
            ex8,
            new MapFileTable(),
            new MapHeap()
    );
    Repository repo8 = new Repository("log8.txt");
    Controller ctr8 = new Controller(repo8);
    ctr8.setViewFlagTrue();
    ctr8.add(prg8);

    // Example 9: int v; Ref int a; v=10; new(a,22);
    // fork(wH(a,30); v=32; print(v); print(rH(a)));
    // print(v); print(rH(a))
    Statement ex9 = new CompoundStatement(
            new VariableDeclarationStatement(new IntType(), "v"),
            new CompoundStatement(
                    new VariableDeclarationStatement(new RefType(new IntType()), "a"),
                    new CompoundStatement(
                            new AssignmentStatement(new ConstantExpression(new IntegerValue(10)), "v"),
                            new CompoundStatement(
                                    new HeapAllocationStatement("a", new ConstantExpression(new IntegerValue(22))),
                                    new CompoundStatement(
                                            new ForkStatement(
                                                    new CompoundStatement(
                                                            new WriteHeapStatement("a", new ConstantExpression(new IntegerValue(30))),
                                                            new CompoundStatement(
                                                                    new AssignmentStatement(new ConstantExpression(new IntegerValue(32)), "v"),
                                                                    new CompoundStatement(
                                                                            new PrintStatement(new VariableExpression("v")),
                                                                            new PrintStatement(new ReadHeapExpression(new VariableExpression("a")))
                                                                    )
                                                            )
                                                    )
                                            ),
                                            new CompoundStatement(
                                                    new PrintStatement(new VariableExpression("v")),
                                                    new PrintStatement(new ReadHeapExpression(new VariableExpression("a")))
                                            )
                                    )
                            )
                    )
            )
    );

    ex9.typecheck(new MapTypeEnvironment());

    ProgramState prg9 = new ProgramState(
            new StackExecutionStack(),
            new MapSymbolTable(),
            new ListOut(),
            ex9,
            new MapFileTable(),
            new MapHeap()
    );
    Repository repo9 = new Repository("log9.txt");
    Controller ctr9 = new Controller(repo9);
    ctr9.setViewFlagTrue();
    ctr9.add(prg9);


    TextMenu menu = new TextMenu();
    menu.addCommand(new ExitCommand("0", "Exit"));
    menu.addCommand(new RunExampleCommand("1", ex1.toString(), ctr1));
    menu.addCommand(new RunExampleCommand("2", ex2.toString(), ctr2));
    menu.addCommand(new RunExampleCommand("3", ex3.toString(), ctr3));
    menu.addCommand(new RunExampleCommand("4", ex4.toString(), ctr4));
    menu.addCommand(new RunExampleCommand("5", ex5.toString(), ctr5));
    menu.addCommand(new RunExampleCommand("6", ex6.toString(), ctr6));
    menu.addCommand(new RunExampleCommand("7", ex7.toString(), ctr7));
    menu.addCommand(new RunExampleCommand("8", ex8.toString(), ctr8));
    menu.addCommand(new RunExampleCommand("9", ex9.toString(), ctr9));

    menu.show();
}