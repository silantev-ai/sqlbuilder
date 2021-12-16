package querybuilder.structure.Impl;

import querybuilder.structure.LogicalOperator;
import querybuilder.structure.OpenBracket;
import querybuilder.structure.Statement;
import querybuilder.structure.Where;
import querybuilder.structure.enums.Logical;

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
        return ExprFactory.openBracket(where);
    }

    @Override
    public Statement statement(Statement statement) {
        return ExprFactory.statement(where, statement);
    }
}
