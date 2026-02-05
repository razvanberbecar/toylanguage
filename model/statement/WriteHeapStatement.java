package model.statement;

import model.exception.MyException;
import model.expression.Expression;
import model.state.ProgramState;
import model.state.TypeEnvironment;
import model.type.RefType;
import model.type.Type;
import model.value.RefValue;
import model.value.Value;


public record WriteHeapStatement(String varName, Expression expression) implements Statement {

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        var symTable = state.getSymbolTable();
        var heap = state.getHeap();

        if (!symTable.isDefined(varName)) {
            throw new MyException("wH: variable not defined: " + varName);
        }
        var varType = symTable.getType(varName);
        if (!(varType instanceof RefType refType)) {
            throw new MyException("wH: variable must be of Ref type: " + varName);
        }
        var varValue = symTable.getValue(varName);
        if (!(varValue instanceof RefValue refV)) {
            throw new MyException("wH: variable value is not RefValue: " + varName);
        }
        int address = refV.address();
        if (!heap.isDefined(address)) {
            throw new MyException("wH: address not defined in heap: " + address);
        }
        Value evalValue = expression.evaluate(symTable, heap);
        if (!evalValue.getType().equals(refType.getInner())) {
            throw new MyException("wH: expression type does not match location type");
        }
        heap.update(address, evalValue);
        return state;
    }

    @Override
    public TypeEnvironment typecheck(TypeEnvironment typeEnv) throws MyException {
        Type varType = typeEnv.lookup(varName);
        Type expType = expression.typecheck(typeEnv);
        if (!(varType instanceof RefType refType))
            throw new MyException("wH: variable is not of Ref type");
        if (!refType.getInner().equals(expType))
            throw new MyException("wH: expression type does not match location type");
        return typeEnv;
    }

    @Override
    public String toString() {
        return "wH(" + varName + ", " + expression + ")";
    }
}
