package querybuilder.structure.Impl;

import querybuilder.structure.CloseBracket;
import querybuilder.structure.LogicalOperator;
import querybuilder.structure.QueryBuilder;
import querybuilder.structure.Where;
import querybuilder.structure.enums.Logical;

public class CloseBracketImpl extends WhereTokenAbstract implements CloseBracket {

    public CloseBracketImpl(Where where) {
        super(where);
    }

    @Override
    public LogicalOperator operator(Logical operator) {
        return SqlStructureFactory.operator(where, operator);
    }

    @Override
    public CloseBracket closeBracket() {
        return SqlStructureFactory.closeBracket(where);
    }

    @Override
    public QueryBuilder endWhere() {
        return where.endWhere();
    }
}
