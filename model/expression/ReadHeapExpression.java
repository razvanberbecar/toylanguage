package model.expression;

import model.exception.MyException;
import model.state.Heap;
import model.state.SymbolTable;
import model.state.TypeEnvironment;
import model.type.RefType;
import model.value.RefValue;
import model.value.Value;


public record ReadHeapExpression(Expression expression) implements Expression {

    @Override
    public Value evaluate(SymbolTable symTable, Heap hp) throws MyException {
        Value v = expression.evaluate(symTable, hp);
        if (!(v instanceof RefValue refV)) {
            throw new MyException("rH: expression must evaluate to a RefValue");
        }
        int address = refV.address();
        if (!hp.isDefined(address)) {
            throw new MyException("rH: address not defined in heap: " + address);
        }
        return hp.get(address);
    }

    @Override
    public model.type.Type typecheck(TypeEnvironment typeEnv) throws MyException {
        var t = expression.typecheck(typeEnv);
        if (t instanceof RefType refType) {
            return refType.getInner();
        } else throw new MyException("the rH argument is not a Ref Type");
    }

    @Override
    public String toString() {
        return "rH(" + expression + ")";
    }
}
