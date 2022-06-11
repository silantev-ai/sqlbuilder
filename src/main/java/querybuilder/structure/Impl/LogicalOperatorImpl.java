package querybuilder.structure.Impl;

import querybuilder.structure.*;
import querybuilder.structure.enums.Logical;

import java.util.function.BiFunction;

public class LogicalOperatorImpl extends WhereTokenAbstract implements LogicalOperator {

    private final Logical operator;

    public LogicalOperatorImpl(Where where, Logical operator) {
        super(where);
        this.operator = operator;
    }

    @Override
    public Logical getOperator() {
        return operator;
    }

    @Override
    public OpenBracket openBracket() {
        return SqlStructureFactory.openBracket(where);
    }

    @Override
    public Statement statement(Statement statement) {
        return SqlStructureFactory.statement(where, statement);
    }

    @Override
    public <T> Statement statement(BiFunction<Where, T, Statement> func, T value) {
        return func.apply(where, value);
    }
}
