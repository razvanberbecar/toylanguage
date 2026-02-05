package model.statement;

import model.exception.MyException;
import model.expression.Expression;
import model.state.ProgramState;
import model.state.TypeEnvironment;
import model.type.RefType;
import model.type.Type;
import model.value.RefValue;
import model.value.Value;

public record HeapAllocationStatement(String varName, Expression expression) implements Statement {


    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        var symbolTable=state.getSymbolTable();
        var heap=state.getHeap();

        if (!symbolTable.isDefined(varName)) {
            throw new MyException("HeapAllocation: variable not defined: " + varName);
        }

        var varType=symbolTable.getType(varName);
        if (!(varType instanceof RefType)) {
            throw new MyException("HeapAllocation: variable must be of type ref: " + varName);
        }

        Value expressionValue=expression.evaluate(symbolTable, state.getHeap());
        RefType refType=(RefType) varType;
        var locationType=refType.getInner();
        if (!(expressionValue.getType().equals(locationType))){
            throw new MyException("HeapAllocation: expression must evaluate to a " + locationType.toString());
        }

        int newAddress=heap.allocate(expressionValue);
        symbolTable.update(varName, new RefValue(newAddress, locationType));

        return state;
    }

    @Override
    public TypeEnvironment typecheck(TypeEnvironment typeEnv) throws MyException {
        Type varType = typeEnv.lookup(varName);
        Type expType = expression.typecheck(typeEnv);
        if (!(varType instanceof RefType refType))
            throw new MyException("HeapAllocation: variable is not of Ref type");
        if (!refType.getInner().equals(expType))
            throw new MyException("HeapAllocation: expression type does not match location type");
        return typeEnv;
    }

    @Override
    public String toString() {
        return "new(" + varName + ", " + expression + ")";
    }

}
