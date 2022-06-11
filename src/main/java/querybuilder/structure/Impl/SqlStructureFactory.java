package querybuilder.structure.Impl;

import querybuilder.structure.*;
import querybuilder.structure.enums.Logical;

public class SqlStructureFactory {

    public static Statement statement(Where where, Statement statement) {
        where.getElements().addLast(statement);
        statement.setWhere(where);
        return statement;
    }

    public static OpenBracket openBracket(Where where) {
        OpenBracket item = new OpenBracketImpl(where);
        where.getElements().addLast(item);
        where.getBracketsCounter().incrementAndGet();
        return item;
    }

    public static CloseBracket closeBracket(Where where) {
        CloseBracket item = new CloseBracketImpl(where);
        where.getElements().addLast(item);
        where.getBracketsCounter().decrementAndGet();
        return item;
    }

    public static LogicalOperator operator(Where where, Logical operator) {
        LogicalOperator item = new LogicalOperatorImpl(where, operator);
        where.getElements().addLast(item);
        return item;
    }
}
