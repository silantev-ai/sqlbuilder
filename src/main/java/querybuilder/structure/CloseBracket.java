package querybuilder.structure;


import querybuilder.structure.enums.Logical;

public interface CloseBracket extends WhereToken {
    LogicalOperator operator(Logical operator);
    CloseBracket closeBracket();
    QueryBuilder endWhere();
    default QueryBuilder endHaving() {
        return endWhere();
    }
}
